package com.st.amap.compose

import androidx.compose.runtime.AbstractApplier
import com.amap.api.maps.AMap
import com.amap.api.maps.MapView
import com.amap.api.maps.model.Circle
import com.amap.api.maps.model.GroundOverlay
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.Polygon
import com.amap.api.maps.model.Polyline

internal interface MapNode {
    fun onAttached() {}
    fun onRemoved() {}
    fun onCleared() {}
}

private object MapNodeRoot : MapNode

internal class MapApplier(
    val map: AMap,
    private val mapView: MapView,
) : AbstractApplier<MapNode>(MapNodeRoot) {

    private val decorations = mutableListOf<MapNode>()

    init {
        attachClickListeners()
    }

    override fun onClear() {
        map.clear()
        decorations.forEach { it.onCleared() }
        decorations.clear()
    }

    override fun insertBottomUp(index: Int, instance: MapNode) {
        decorations.add(index, instance)
        instance.onAttached()
    }

    override fun insertTopDown(index: Int, instance: MapNode) {
        // insertBottomUp is preferred
    }

    override fun move(from: Int, to: Int, count: Int) {
        decorations.move(from, to, count)
    }

    override fun remove(index: Int, count: Int) {
        repeat(count) {
            decorations[index + it].onRemoved()
        }
        decorations.remove(index, count)
    }

    private fun attachClickListeners() {

        map.setOnPolylineClickListener {
            decorations.nodeForPolyline(it)
                ?.onPolylineClick
                ?.invoke(it)
        }

        // Marker
        map.setOnMarkerClickListener { marker ->
            decorations.nodeForMarker(marker)
                ?.onMarkerClick
                ?.invoke(marker)
                ?: false
        }
        map.setOnInfoWindowClickListener { marker ->
            decorations.nodeForMarker(marker)
                ?.onInfoWindowClick
                ?.invoke(marker)
        }
        map.setOnMarkerDragListener(object : AMap.OnMarkerDragListener {
            override fun onMarkerDrag(marker: Marker) {
                with(decorations.nodeForMarker(marker)) {
                    this?.markerState?.position = marker.position
                    this?.markerState?.dragState = DragState.DRAG
                }
            }

            override fun onMarkerDragEnd(marker: Marker) {
                with(decorations.nodeForMarker(marker)) {
                    this?.markerState?.position = marker.position
                    this?.markerState?.dragState = DragState.END
                }
            }

            override fun onMarkerDragStart(marker: Marker) {
                with(decorations.nodeForMarker(marker)) {
                    this?.markerState?.position = marker.position
                    this?.markerState?.dragState = DragState.START
                }
            }
        })
        map.setInfoWindowAdapter(
            ComposeInfoWindowAdapter(
                mapView,
                markerNodeFinder = { decorations.nodeForMarker(it) }
            )
        )
    }
}

private fun MutableList<MapNode>.nodeForCircle(circle: Circle): CircleNode? =
    firstOrNull { it is CircleNode && it.circle == circle } as? CircleNode

private fun MutableList<MapNode>.nodeForMarker(marker: Marker): MarkerNode? =
    firstOrNull { it is MarkerNode && it.marker == marker } as? MarkerNode

private fun MutableList<MapNode>.nodeForPolygon(polygon: Polygon): PolygonNode? =
    firstOrNull { it is PolygonNode && it.polygon == polygon } as? PolygonNode

private fun MutableList<MapNode>.nodeForPolyline(polyline: Polyline): PolylineNode? =
    firstOrNull { it is PolylineNode && it.polyline == polyline } as? PolylineNode

private fun MutableList<MapNode>.nodeForGroundOverlay(
    groundOverlay: GroundOverlay
): GroundOverlayNode? =
    firstOrNull { it is GroundOverlayNode && it.groundOverlay == groundOverlay } as? GroundOverlayNode
