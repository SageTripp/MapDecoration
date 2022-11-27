package com.st.amap.ktx

import android.graphics.Bitmap
import android.location.Location
import com.amap.api.maps.AMap
import com.amap.api.maps.AMap.OnCameraChangeListener
import com.amap.api.maps.AMap.OnIndoorBuildingActiveListener
import com.amap.api.maps.AMap.OnInfoWindowClickListener
import com.amap.api.maps.AMap.OnMapClickListener
import com.amap.api.maps.AMap.OnMapLongClickListener
import com.amap.api.maps.AMap.OnMapScreenShotListener
import com.amap.api.maps.AMap.OnMarkerClickListener
import com.amap.api.maps.AMap.OnMarkerDragListener
import com.amap.api.maps.AMap.OnMyLocationChangeListener
import com.amap.api.maps.AMap.OnPOIClickListener
import com.amap.api.maps.AMap.OnPolylineClickListener
import com.amap.api.maps.AMapOptions
import com.amap.api.maps.CameraUpdate
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.Circle
import com.amap.api.maps.model.CircleOptions
import com.amap.api.maps.model.GroundOverlay
import com.amap.api.maps.model.GroundOverlayOptions
import com.amap.api.maps.model.IndoorBuildingInfo
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.Poi
import com.amap.api.maps.model.Polygon
import com.amap.api.maps.model.PolygonOptions
import com.amap.api.maps.model.Polyline
import com.amap.api.maps.model.PolylineOptions
import com.amap.api.maps.model.TileOverlay
import com.amap.api.maps.model.TileOverlayOptions
import com.st.amap.ktx.model.circleOptions
import com.st.amap.ktx.model.groundOverlayOptions
import com.st.amap.ktx.model.markerOptions
import com.st.amap.ktx.model.polygonOptions
import com.st.amap.ktx.model.polylineOptions
import com.st.amap.ktx.model.tileOverlayOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public sealed class CameraEvent(open val position: CameraPosition)
data class CameraChangeEvent(override val position: CameraPosition) : CameraEvent(position)
data class CameraChangeFinishEvent(override val position: CameraPosition) : CameraEvent(position)

/**
 * Change event when a marker is dragged. See [AMap.setOnMarkerDragListener]
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
 * A suspending function that awaits the completion of the [cameraUpdate] animation.
 *
 * @param cameraUpdate the [CameraUpdate] to apply on the map
 * @param durationMs the duration in milliseconds of the animation. Defaults to 3 seconds.
 */
public suspend inline fun AMap.awaitAnimateCamera(
    cameraUpdate: CameraUpdate,
    durationMs: Long = 3000
): Unit =
    suspendCancellableCoroutine { continuation ->
        animateCamera(cameraUpdate, durationMs, object : AMap.CancelableCallback {
            override fun onFinish() {
                continuation.resume(Unit)
            }

            override fun onCancel() {
                continuation.cancel()
            }
        })
    }

/**
 * A suspending function that awaits for the map to be loaded. Uses
 * [AMap.setOnMapLoadedListener].
 */
public suspend inline fun AMap.awaitMapLoad(): Unit =
    suspendCoroutine { continuation ->
        setOnMapLoadedListener {
            continuation.resume(Unit)
        }
    }

/**
 * Returns a flow that emits when the camera is idle. Using this to observe camera idle events will
 * override an existing listener (if any) to [AMap.addOnCameraChangeListener].
 */
public fun AMap.cameraIdleEvents(): Flow<CameraEvent> =
    callbackFlow {
        val cameraChangeListener = object : OnCameraChangeListener {
            override fun onCameraChange(position: CameraPosition) {
                trySend(CameraChangeEvent(position))
            }

            override fun onCameraChangeFinish(position: CameraPosition) {
                trySend(CameraChangeFinishEvent(position))
            }
        }
        addOnCameraChangeListener(cameraChangeListener)
        awaitClose {
            removeOnCameraChangeListener(cameraChangeListener)
        }
    }


/**
 * A suspending function that returns a bitmap snapshot of the current view of the map. Uses
 * [AMap.getMapScreenShot].
 *
 * @return the snapshot
 */
public suspend inline fun AMap.awaitSnapshot(): Bitmap? =
    suspendCoroutine { continuation ->
        getMapScreenShot(object : OnMapScreenShotListener {
            override fun onMapScreenShot(bitmap: Bitmap?) {
                continuation.resume(bitmap)
            }

            override fun onMapScreenShot(bitmap: Bitmap?, p1: Int) {
                continuation.resume(bitmap)
            }

        })
    }

/**
 * Returns a flow that emits when the indoor state changes. Using this to observe indoor state
 * change events will override an existing listener (if any) to
 * [AMap.addOnIndoorBuildingActiveListener]
 */
public fun AMap.indoorStateChangeEvents(): Flow<IndoorBuildingInfo> =
    callbackFlow {
        val onIndoorBuildingActivatedListener = OnIndoorBuildingActiveListener { trySend(it) }
        addOnIndoorBuildingActiveListener(onIndoorBuildingActivatedListener)
        awaitClose {
            removeOnIndoorBuildingActiveListener(onIndoorBuildingActivatedListener)
        }
    }

/**
 * Returns a flow that emits when a marker's info window is clicked. Using this to observe info
 * info window clicks will override an existing listener (if any) to
 * [AMap.addOnInfoWindowClickListener]
 */
public fun AMap.infoWindowClickEvents(): Flow<Marker> =
    callbackFlow {
        val onInfoWindowClickListener = OnInfoWindowClickListener { trySend(it) }
        addOnInfoWindowClickListener(onInfoWindowClickListener)
        awaitClose {
            removeOnInfoWindowClickListener(onInfoWindowClickListener)
        }
    }


/**
 * Returns a flow that emits when the map is clicked. Using this to observe map click events will
 * override an existing listener (if any) to [AMap.addOnMapClickListener]
 */
public fun AMap.mapClickEvents(): Flow<LatLng> =
    callbackFlow {
        val onMapClickListener = OnMapClickListener { trySend(it) }
        addOnMapClickListener(onMapClickListener)
        awaitClose {
            removeOnMapClickListener(onMapClickListener)
        }
    }

/**
 * Returns a flow that emits when the map is long clicked. Using this to observe map click events
 * will override an existing listener (if any) to [AMap.addOnMapLongClickListener]
 */
public fun AMap.mapLongClickEvents(): Flow<LatLng> =
    callbackFlow {
        val onMapLongClickListener = OnMapLongClickListener { trySend(it) }
        addOnMapLongClickListener(onMapLongClickListener)
        awaitClose {
            removeOnMapLongClickListener(onMapLongClickListener)
        }
    }

/**
 * Returns a flow that emits when a marker on the map is clicked. Using this to observe marker click
 * events will override an existing listener (if any) to [AMap.addOnMarkerClickListener]
 */
public fun AMap.markerClickEvents(): Flow<Marker> =
    callbackFlow {
        val onMarkerClickListener = OnMarkerClickListener { trySend(it).isSuccess }
        addOnMarkerClickListener(onMarkerClickListener)
        awaitClose {
            removeOnMarkerClickListener(onMarkerClickListener)
        }
    }

/**
 * Returns a flow that emits when a marker is dragged. Using this to observer marker drag events
 * will override existing listeners (if any) to [AMap.addOnMarkerDragListener]
 */
public fun AMap.markerDragEvents(): Flow<OnMarkerDragEvent> =
    callbackFlow {
        val onMarkerDragListener = object : OnMarkerDragListener {
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
        addOnMarkerDragListener(onMarkerDragListener)
        awaitClose {
            removeOnMarkerDragListener(onMarkerDragListener)
        }
    }


/**
 * Returns a flow that emits when the my location blue dot is clicked. Using this to observe my
 * location blue dot click events will override an existing listener (if any) to
 * [AMap.addOnMyLocationChangeListener]
 */
public fun AMap.myLocationChangeEvents(): Flow<Location> =
    callbackFlow {
        val onMyLocationListener = OnMyLocationChangeListener { trySend(it) }
        addOnMyLocationChangeListener(onMyLocationListener)
        awaitClose {
            removeOnMyLocationChangeListener(onMyLocationListener)
        }
    }

/**
 * Returns a flow that emits when a PointOfInterest is clicked. Using this to observe
 * PointOfInterest click events will override an existing listener (if any) to
 * [AMap.addOnPOIClickListener]
 */
public fun AMap.poiClickEvents(): Flow<Poi> =
    callbackFlow {
        val onPoiClickListener = OnPOIClickListener { trySend(it) }
        addOnPOIClickListener(onPoiClickListener)
        awaitClose {
            removeOnPOIClickListener(onPoiClickListener)
        }
    }

/**
 * Returns a flow that emits when a Polyline is clicked. Using this to observe Polyline click events
 * will override an existing listener (if any) to [AMap.setOnPolylineClickListener]
 */
public fun AMap.polylineClickEvents(): Flow<Polyline> =
    callbackFlow {
        val onPolylineClickListener = OnPolylineClickListener { trySend(it) }
        addOnPolylineClickListener(onPolylineClickListener)
        awaitClose {
            removeOnPolylineClickListener(onPolylineClickListener)
        }
    }

/**
 * Builds a new [AMapOptions] using the provided [optionsActions].
 *
 * @return the constructed [AMapOptions]
 */
public inline fun buildAMapOptions(optionsActions: AMapOptions.() -> Unit): AMapOptions =
    AMapOptions().apply(
        optionsActions
    )

/**
 * Adds a [Circle] to this [AMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Circle]
 */
public inline fun AMap.addCircle(optionsActions: CircleOptions.() -> Unit): Circle =
    this.addCircle(
        circleOptions(optionsActions)
    )

/**
 * Adds a [GroundOverlay] to this [AMap] using the function literal with receiver
 * [optionsActions].
 *
 * @return the added [Circle]
 */
public inline fun AMap.addGroundOverlay(optionsActions: GroundOverlayOptions.() -> Unit): GroundOverlay? =
    this.addGroundOverlay(
        groundOverlayOptions(optionsActions)
    )

/**
 * Adds a [Marker] to this [AMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Marker]
 */
public inline fun AMap.addMarker(optionsActions: MarkerOptions.() -> Unit): Marker? =
    this.addMarker(
        markerOptions(optionsActions)
    )

/**
 * Adds a [Polygon] to this [AMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Polygon]
 */
public inline fun AMap.addPolygon(optionsActions: PolygonOptions.() -> Unit): Polygon =
    this.addPolygon(
        polygonOptions(optionsActions)
    )

/**
 * Adds a [Polyline] to this [AMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Polyline]
 */
public inline fun AMap.addPolyline(optionsActions: PolylineOptions.() -> Unit): Polyline =
    this.addPolyline(
        polylineOptions(optionsActions)
    )

/**
 * Adds a [TileOverlay] to this [AMap] using the function literal with receiver
 * [optionsActions].
 *
 * @return the added [Polyline]
 */
public inline fun AMap.addTileOverlay(optionsActions: TileOverlayOptions.() -> Unit): TileOverlay? =
    this.addTileOverlay(
        tileOverlayOptions(optionsActions)
    )