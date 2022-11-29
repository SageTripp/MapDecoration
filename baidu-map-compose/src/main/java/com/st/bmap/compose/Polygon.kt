package com.st.bmap.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.baidu.mapapi.map.Polygon
import com.baidu.mapapi.map.PolygonOptions
import com.baidu.mapapi.map.Stroke
import com.baidu.mapapi.model.LatLng
import com.st.bmap.ktx.addPolygon

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
 * @param dottedStroke 是否是否绘制虚线边框
 * @param visible 多边形是否可见。默认为可见
 * @param zIndex 多边形的Z轴数值
 */
@Composable
@BaiduMapComposable
fun Polygon(
    points: List<LatLng>,
    fillColor: Color = Color.Black,
    holes: List<Hole> = emptyList(),
    strokeColor: Color = Color.Black,
    strokeWidth: Int = 10,
    dottedStroke: Boolean = false,
    visible: Boolean = true,
    zIndex: Int = 0,
) {
    if (points.size < 3) return
    val mapApplier = currentComposer.applier as MapApplier?
    ComposeNode<PolygonNode, MapApplier>(
        factory = {
            val polygon = (if (points.size < 3) null else mapApplier?.map?.addPolygon {
                points(points)
                fillColor(fillColor.toArgb())
                stroke(Stroke(strokeWidth, strokeColor.toArgb()))
                dottedStroke(dottedStroke)
                visible(visible)
                zIndex(zIndex)
                addHoles(holes)
            }) ?: error("Error adding polygon")
            PolygonNode(polygon)
        },
        update = {
            set(points) { this.polygon.points = it }
            set(fillColor) { this.polygon.fillColor = it.toArgb() }
            set(holes) {
                this.polygon.holeOptions.clear()
                this.polygon.holeOptions.addAll(it.map { hole -> hole.mapToHoleOptions() })
            }
            set(strokeColor) {
                this.polygon.stroke = Stroke(polygon.stroke.strokeWidth, it.toArgb())
            }
            set(strokeWidth) { this.polygon.stroke = Stroke(it, polygon.stroke.color) }
            set(visible) { this.polygon.isVisible = it }
            set(zIndex) { this.polygon.zIndex = it }
        }
    )
}

internal fun PolygonOptions.addHoles(holes: List<Hole>) {
    addHoleOptions(holes.map { it.mapToHoleOptions() })
}
