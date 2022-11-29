package com.st.bmap.compose

import androidx.annotation.UiThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MapStatusUpdate
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.Projection
import com.baidu.mapapi.model.LatLng
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

/**
 * Create and [rememberSaveable] a [BaiduMapState] using [BaiduMapState.Saver].
 * [init] will be called when the [BaiduMapState] is first created to configure its
 * initial state.
 */
@Composable
inline fun rememberBaiduMapState(
    key: String? = null,
    crossinline init: BaiduMapState.() -> Unit = {}
): BaiduMapState = rememberSaveable(key = key, saver = BaiduMapState.Saver) {
    BaiduMapState().apply(init)
}

val BeiJing = LatLng(39.914271, 116.404269)

/**
 * A state object that can be hoisted to control and observe the map's map state.
 * A [BaiduMapState] may only be used by a single [BaiduMap] composable at a time
 * as it reflects instance state for a single view of a map.
 *
 * @param status the initial map status
 */
class BaiduMapState(
    status: MapStatus = MapStatus.Builder().target(BeiJing).zoom(18f).build()
) {
    var mapStatusChangeStartedReason: MapStatusChangeStartedReason by mutableStateOf(
        MapStatusChangeStartedReason.NO_MOVEMENT_YET
    )
        internal set

    /**
     * Whether the map is currently moving or not. This includes any kind of movement:
     * panning, zooming, or rotation.
     */
    var isMoving: Boolean by mutableStateOf(false)
        internal set

    /**
     * Returns the current [Projection] to be used for converting between screen
     * coordinates and lat/lng.
     */
    val projection: Projection?
        get() = map?.projection

    /**
     * Local source of truth for the current map status.
     * While [map] is non-null this reflects the current status of [map] as it changes.
     * While [map] is null it reflects the last known map status, or the last value set by
     * explicitly setting [status].
     */
    internal var rawStatus by mutableStateOf(status)

    /**
     * Current status of the map on the map.
     */
    var status: MapStatus
        get() = rawStatus
        set(value) {
            synchronized(lock) {
                val map = map
                if (map == null) {
                    rawStatus = value
                } else {
                    map.setMapStatus(MapStatusUpdateFactory.newMapStatus(value))
                }
            }
        }

    // Used to perform side effects thread-safely.
    // Guards all mutable properties that are not `by mutableStateOf`.
    private val lock = Any()

    // The map currently associated with this BaiduMapState.
    // Guarded by `lock`.
    private var map: BaiduMap? = null

    // An action to run when the map becomes available or unavailable.
    // represents a mutually exclusive mutation to perform while holding `lock`.
    // Guarded by `lock`.
    private var onMapChanged: OnMapChangedCallback? = null

    /**
     * Set [onMapChanged] to [callback], invoking the current callback's
     * [OnMapChangedCallback.onCancelLocked] if one is present.
     */
    private fun doOnMapChangedLocked(callback: OnMapChangedCallback) {
        onMapChanged?.onCancelLocked()
        onMapChanged = callback
    }

    // A token representing the current owner of any ongoing motion in progress.
    // Used to determine if map animation should stop when calls to animate end.
    // Guarded by `lock`.
    private var movementOwner: Any? = null

    /**
     * Used with [onMapChangedLocked] to execute one-time actions when a map becomes available
     * or is made unavailable. Cancellation is provided in order to resume suspended coroutines
     * that are awaiting the execution of one of these callbacks that will never come.
     */
    private fun interface OnMapChangedCallback {
        fun onMapChangedLocked(newMap: BaiduMap?)
        fun onCancelLocked() {}
    }

    // The current map is set and cleared by side effect.
    // There can be only one associated at a time.
    internal fun setMap(map: BaiduMap?) {
        synchronized(lock) {
            if (this.map == null && map == null) return
            if (this.map != null && map != null) {
                error("BaiduMapState may only be associated with one BaiduMap at a time")
            }
            this.map = map
            if (map == null) {
                isMoving = false
            } else {
                map.setMapStatus(MapStatusUpdateFactory.newMapStatus(status))
            }
            onMapChanged?.let {
                // Clear this first since the callback itself might set it again for later
                onMapChanged = null
                it.onMapChangedLocked(map)
            }
        }
    }

    /**
     * Animate the map status as specified by [update], returning once the animation has
     * completed. [status] will reflect the status of the map as the animation proceeds.
     *
     * [animate] will throw [CancellationException] if the animation does not fully complete.
     * This can happen if:
     *
     * * The user manipulates the map directly
     * * [status] is set explicitly, e.g. `state.status = MapStatus(...)`
     * * [animate] is called again before an earlier call to [animate] returns
     * * [move] is called
     * * The calling job is [cancelled][kotlinx.coroutines.Job.cancel] externally
     *
     * If this [BaiduMapState] is not currently bound to a [BaiduMap] this call will
     * suspend until a map is bound and animation will begin.
     *
     * This method should only be called from a dispatcher bound to the map's UI thread.
     *
     * @param update the change that should be applied to the map
     * @param durationMs The duration of the animation in milliseconds. If [Int.MAX_VALUE] is
     * provided, the default animation duration will be used. Otherwise, the value provided must be
     * strictly positive, otherwise an [IllegalArgumentException] will be thrown.
     */
    @UiThread
    suspend fun animate(update: MapStatusUpdate, durationMs: Int = Int.MAX_VALUE) {
        val myJob = currentCoroutineContext()[Job]
        try {
            suspendCancellableCoroutine<Unit> { continuation ->
                synchronized(lock) {
                    movementOwner = myJob
                    val map = map
                    if (map == null) {
                        // Do it later
                        val animateOnMapAvailable = object : OnMapChangedCallback {
                            override fun onMapChangedLocked(newMap: BaiduMap?) {
                                if (newMap == null) {
                                    // Cancel the animate caller and crash the map setter
                                    @Suppress("ThrowableNotThrown")
                                    continuation.resumeWithException(
                                        CancellationException(
                                            "internal error; no BaiduMap available"
                                        )
                                    )
                                    error(
                                        "internal error; no BaiduMap available to animate status"
                                    )
                                }
                                performAnimateCameraLocked(newMap, update, durationMs)
                            }

                            override fun onCancelLocked() {
                                continuation.resumeWithException(
                                    CancellationException("Animation cancelled")
                                )
                            }
                        }
                        doOnMapChangedLocked(animateOnMapAvailable)
                        continuation.invokeOnCancellation {
                            synchronized(lock) {
                                if (onMapChanged === animateOnMapAvailable) {
                                    // External cancellation shouldn't invoke onCancel
                                    // so we set this to null directly instead of going through
                                    // doOnMapChangedLocked(null).
                                    onMapChanged = null
                                }
                            }
                        }
                    } else {
                        performAnimateCameraLocked(map, update, durationMs)
                    }
                }
            }
        } finally {
            // continuation.invokeOnCancellation might be called from any thread, so stop the
            // animation in progress here where we're guaranteed to be back on the right dispatcher.
            synchronized(lock) {
                if (myJob != null && movementOwner === myJob) {
                    movementOwner = null
                }
            }
        }
    }

    private fun performAnimateCameraLocked(
        map: BaiduMap,
        update: MapStatusUpdate,
        durationMs: Int
    ) {
        if (durationMs == Int.MAX_VALUE) {
            map.animateMapStatus(update)
        } else {
            map.animateMapStatus(update, durationMs)
        }
    }

    /**
     * Move the map instantaneously as specified by [update]. Any calls to [animate] in progress
     * will be cancelled. [status] will be updated when the bound map's status has been updated,
     * or if the map is currently unbound, [update] will be applied when a map is next bound.
     * Other calls to [move], [animate], or setting [status] will override an earlier pending
     * call to [move].
     *
     * This method must be called from the map's UI thread.
     */
    @UiThread
    fun move(update: MapStatusUpdate) {
        synchronized(lock) {
            val map = map
            movementOwner = null
            if (map == null) {
                // Do it when we have a map available
                doOnMapChangedLocked { it?.setMapStatus(update) }
            } else {
                map.setMapStatus(update)
            }
        }
    }

    companion object {
        /**
         * The default saver implementation for [BaiduMapState]
         */
        val Saver: Saver<BaiduMapState, MapStatus> = Saver(
            save = { it.status },
            restore = { BaiduMapState(it) }
        )
    }
}
