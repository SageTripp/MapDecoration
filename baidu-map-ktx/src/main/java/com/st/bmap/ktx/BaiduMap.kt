package com.st.bmap.ktx

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.annotation.IntDef
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener
import com.baidu.mapapi.map.BaiduMapOptions
import com.baidu.mapapi.map.Circle
import com.baidu.mapapi.map.CircleOptions
import com.baidu.mapapi.map.GroundOverlay
import com.baidu.mapapi.map.GroundOverlayOptions
import com.baidu.mapapi.map.MapBaseIndoorMapInfo
import com.baidu.mapapi.map.MapPoi
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.Marker
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.map.Polygon
import com.baidu.mapapi.map.PolygonOptions
import com.baidu.mapapi.map.Polyline
import com.baidu.mapapi.map.PolylineOptions
import com.baidu.mapapi.map.TileOverlay
import com.baidu.mapapi.map.TileOverlayOptions
import com.baidu.mapapi.model.LatLng
import com.st.bmap.ktx.model.circleOptions
import com.st.bmap.ktx.model.groundOverlayOptions
import com.st.bmap.ktx.model.markerOptions
import com.st.bmap.ktx.model.polygonOptions
import com.st.bmap.ktx.model.polylineOptions
import com.st.bmap.ktx.model.tileOverlayOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@IntDef(
    OnMapStatusChangeListener.REASON_API_ANIMATION,
    OnMapStatusChangeListener.REASON_DEVELOPER_ANIMATION,
    OnMapStatusChangeListener.REASON_GESTURE,
)
@Retention(AnnotationRetention.SOURCE)
public annotation class MoveStartedReason

public sealed class MapStatusChangeEvent(open val mapStatus: MapStatus)
public data class MapStatusChangeFinishEvent(override val mapStatus: MapStatus) :
    MapStatusChangeEvent(mapStatus)

public data class MapStatusChangingEvent(override val mapStatus: MapStatus) :
    MapStatusChangeEvent(mapStatus)

public data class MapStatusChangeStartedEvent(
    override val mapStatus: MapStatus,
    @MoveStartedReason val reason: Int? = null,
) :
    MapStatusChangeEvent(mapStatus)

/**
 * Change event when a marker is dragged. See [BaiduMap.setOnMarkerDragListener]
 */
public sealed class OnMarkerDragEvent {
    public abstract val marker: Marker
}

/**
 * Event emitted repeatedly while a marker is being dragged.
 */
public data class MarkerDragEvent(public override val marker: Marker) : OnMarkerDragEvent()

/**
 * Event emitted when a marker has finished being dragged.
 */
public data class MarkerDragEndEvent(public override val marker: Marker) : OnMarkerDragEvent()

/**
 * Event emitted when a marker starts being dragged.
 */
public data class MarkerDragStartEvent(public override val marker: Marker) : OnMarkerDragEvent()


/**
 * A suspending function that awaits for the map to be loaded. Uses
 * [BaiduMap.setOnMapLoadedCallback].
 */
public suspend inline fun BaiduMap.awaitMapLoad(): Unit =
    suspendCoroutine { continuation ->
        setOnMapLoadedCallback {
            continuation.resume(Unit)
        }
    }

/**
 * Returns a flow that emits when the camera is idle. Using this to observe camera idle events will
 * override an existing listener (if any) to [BaiduMap.setOnMapStatusChangeListener].
 */
public fun BaiduMap.cameraStatusChangeEvents(): Flow<MapStatusChangeEvent> =
    callbackFlow {
        setOnMapStatusChangeListener(object : OnMapStatusChangeListener {
            override fun onMapStatusChangeStart(status: MapStatus) {
                trySend(MapStatusChangeStartedEvent(status))
            }

            override fun onMapStatusChangeStart(status: MapStatus, reason: Int) {
                trySend(MapStatusChangeStartedEvent(status, reason))
            }

            override fun onMapStatusChange(status: MapStatus) {
                trySend(MapStatusChangingEvent(status))
            }

            override fun onMapStatusChangeFinish(status: MapStatus) {
                trySend(MapStatusChangeFinishEvent(status))
            }
        })
        awaitClose {
            setOnMapStatusChangeListener(null)
        }
    }


/**
 * A suspending function that returns a bitmap snapshot of the current view of the map. Uses
 * [BaiduMap.snapshot].
 *
 * @return the snapshot
 */
public suspend inline fun BaiduMap.awaitSnapshot(rect: Rect? = null): Bitmap? =
    suspendCoroutine { continuation ->
        rect?.apply {
            snapshotScope(rect) { continuation.resume(it) }
        } ?: snapshot { continuation.resume(it) }
    }

/**
 * Returns a flow that emits when the indoor state changes. Using this to observe indoor state
 * change events will override an existing listener (if any) to
 * [BaiduMap.setOnBaseIndoorMapListener]
 */
public fun BaiduMap.indoorStateChangeEvents(): Flow<MapBaseIndoorMapInfo> =
    callbackFlow {
        setOnBaseIndoorMapListener { success, indoorMapInfo ->
            trySend(indoorMapInfo)
        }
        awaitClose {
            setOnBaseIndoorMapListener(null)
        }
    }


/**
 * Returns a flow that emits when the map is clicked. Using this to observe map click events will
 * override an existing listener (if any) to [BaiduMap.setOnMapClickListener]
 */
public fun BaiduMap.mapClickEvents(): Flow<LatLng> =
    callbackFlow {
        setOnMapClickListener(object : OnMapClickListener {
            override fun onMapClick(latLng: LatLng) {
                trySend(latLng)
            }

            override fun onMapPoiClick(poi: MapPoi) {

            }

        })
        awaitClose {
            setOnMapClickListener(null)
        }
    }

/**
 * Returns a flow that emits when the map is long clicked. Using this to observe map click events
 * will override an existing listener (if any) to [BaiduMap.setOnMapLongClickListener]
 */
public fun BaiduMap.mapLongClickEvents(): Flow<LatLng> =
    callbackFlow {
        setOnMapLongClickListener { trySend(it) }
        awaitClose {
            setOnMapLongClickListener(null)
        }
    }

/**
 * Returns a flow that emits when a marker on the map is clicked. Using this to observe marker click
 * events will override an existing listener (if any) to [BaiduMap.setOnMarkerClickListener]
 */
public fun BaiduMap.markerClickEvents(): Flow<Marker> =
    callbackFlow {

        val onMarkerClickListener = BaiduMap.OnMarkerClickListener { trySend(it).isSuccess }
        setOnMarkerClickListener(onMarkerClickListener)
        awaitClose {
            removeMarkerClickListener(onMarkerClickListener)
        }
    }

/**
 * Returns a flow that emits when a marker is dragged. Using this to observer marker drag events
 * will override existing listeners (if any) to [BaiduMap.setOnMarkerDragListener]
 */
public fun BaiduMap.markerDragEvents(): Flow<OnMarkerDragEvent> =
    callbackFlow {
        val onMarkerDragListener = object : BaiduMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
                trySend(MarkerDragStartEvent(marker = marker))
            }

            override fun onMarkerDrag(marker: Marker) {
                trySend(MarkerDragEvent(marker = marker))
            }

            override fun onMarkerDragEnd(marker: Marker) {
                trySend(MarkerDragEndEvent(marker = marker))
            }
        }
        setOnMarkerDragListener(onMarkerDragListener)
        awaitClose {
            setOnMarkerDragListener(null)
        }
    }

/**
 * Returns a flow that emits when a Polyline is clicked. Using this to observe Polyline click events
 * will override an existing listener (if any) to [BaiduMap.setOnPolylineClickListener]
 */
public fun BaiduMap.polylineClickEvents(): Flow<Polyline> =
    callbackFlow {
        setOnPolylineClickListener {
            trySend(it).isSuccess
        }
        awaitClose {
            setOnPolylineClickListener(null)
        }
    }

/**
 * Builds a new [BaiduMapOptions] using the provided [optionsActions].
 *
 * @return the constructed [BaiduMapOptions]
 */
public inline fun buildBaiduMapOptions(optionsActions: BaiduMapOptions.() -> Unit): BaiduMapOptions =
    BaiduMapOptions().apply(
        optionsActions
    )

/**
 * Adds a [Circle] to this [BaiduMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Circle]
 */
public inline fun BaiduMap.addCircle(optionsActions: CircleOptions.() -> Unit): Circle =
    this.addOverlay(
        circleOptions(optionsActions)
    ) as Circle

/**
 * Adds a [GroundOverlay] to this [BaiduMap] using the function literal with receiver
 * [optionsActions].
 *
 * @return the added [GroundOverlay]
 */
public inline fun BaiduMap.addGroundOverlay(optionsActions: GroundOverlayOptions.() -> Unit): GroundOverlay =
    this.addOverlay(
        groundOverlayOptions(optionsActions)
    ) as GroundOverlay

/**
 * Adds a [Marker] to this [BaiduMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Marker]
 */
public inline fun BaiduMap.addMarker(optionsActions: MarkerOptions.() -> Unit): Marker =
    this.addOverlay(
        markerOptions(optionsActions)
    ) as Marker

/**
 * Adds a [Polygon] to this [BaiduMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Polygon]
 */
public inline fun BaiduMap.addPolygon(optionsActions: PolygonOptions.() -> Unit): Polygon =
    this.addOverlay(
        polygonOptions(optionsActions)
    ) as Polygon

/**
 * Adds a [Polyline] to this [BaiduMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Polyline]
 */
public inline fun BaiduMap.addPolyline(optionsActions: PolylineOptions.() -> Unit): Polyline =
    this.addOverlay(
        polylineOptions(optionsActions)
    ) as Polyline

/**
 * Adds a [TileOverlay] to this [BaiduMap] using the function literal with receiver
 * [optionsActions].
 *
 * @return the added [Polyline]
 */
public inline fun BaiduMap.addTileOverlay(optionsActions: TileOverlayOptions.() -> Unit): TileOverlay =
    this.addTileLayer(
        tileOverlayOptions(optionsActions)
    ) as TileOverlay