package com.st.bmap.compose

import androidx.compose.runtime.AbstractApplier
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.Circle
import com.baidu.mapapi.map.GroundOverlay
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.Marker
import com.baidu.mapapi.map.Polygon
import com.baidu.mapapi.map.Polyline

internal interface MapNode {
    fun onAttached() {}
    fun onRemoved() {}
    fun onCleared() {}
}

private object MapNodeRoot : MapNode

internal class MapApplier(
    val map: BaiduMap,
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
                ?: false
        }
        // Marker
        map.setOnMarkerClickListener { marker ->
            decorations.nodeForMarker(marker)
                ?.onMarkerClick
                ?.invoke(marker)
                ?: false
        }
        map.setOnMarkerDragListener(object : BaiduMap.OnMarkerDragListener {
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
