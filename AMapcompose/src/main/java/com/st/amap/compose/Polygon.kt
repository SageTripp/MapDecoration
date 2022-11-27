package com.st.amap.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Polygon
import com.amap.api.maps.model.PolygonOptions
import com.st.amap.ktx.addPolygon

internal class PolygonNode(
    val polygon: Polygon
) : MapNode {
    override fun onRemoved() {
        polygon.remove()
    }
}

/**
 * 地图上多边形的可组合项。
 *
 * @param points 多边形边框的顶点
 * @param fillColor 多边形的填充颜色，默认黑色。
 * @param holes 多边形上的空心洞
 * @param strokeColor 多边形的边框颜色，默认为黑色。
 * @param strokeWidth 多边形的边框宽度，单位：像素。默认为10
 * @param useStroke 是否使用边框
 * @param lineJoinType 边框连接处形状
 * @param visible 多边形是否可见。默认为可见
 * @param zIndex 多边形的Z轴数值
 */
@Composable
@AMapComposable
fun Polygon(
    points: List<LatLng>,
    fillColor: Color = Color.Black,
    holes: List<Hole> = emptyList(),
    strokeColor: Color = Color.Black,
    strokeWidth: Float = 10f,
    useStroke: Boolean = true,
    lineJoinType: LineJoinType = LineJoinType.Bevel,
    visible: Boolean = true,
    zIndex: Float = 0f,
) {
    val mapApplier = currentComposer.applier as MapApplier?
    ComposeNode<PolygonNode, MapApplier>(
        factory = {
            val polygon = mapApplier?.map?.addPolygon {
                addAll(points)
                fillColor(fillColor.toArgb())
                strokeColor(strokeColor.toArgb())
                strokeWidth(strokeWidth)
                usePolylineStroke(useStroke)
                lineJoinType(lineJoinType.polygonType)
                visible(visible)
                zIndex(zIndex)
                addHoles(holes)
            } ?: error("Error adding polygon")
            PolygonNode(polygon)
        },
        update = {
            set(points) { this.polygon.points = it }
            set(fillColor) { this.polygon.fillColor = it.toArgb() }
            set(holes) {
                this.polygon.holeOptions.clear()
                this.polygon.holeOptions.addAll(it.map { hole -> hole.mapToHoleOptions() })
            }
            set(strokeColor) { this.polygon.strokeColor = it.toArgb() }
            set(strokeWidth) { this.polygon.strokeWidth = it }
            set(visible) { this.polygon.isVisible = it }
            set(zIndex) { this.polygon.zIndex = it }
        }
    )
}

internal fun PolygonOptions.addHoles(holes: List<Hole>) {
    addHoles(holes.map { it.mapToHoleOptions() })
}
