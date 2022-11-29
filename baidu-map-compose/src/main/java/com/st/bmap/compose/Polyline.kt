package com.st.bmap.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.baidu.mapapi.map.Polyline
import com.baidu.mapapi.map.PolylineDottedLineType
import com.baidu.mapapi.map.PolylineOptions
import com.baidu.mapapi.model.LatLng
import com.st.bmap.ktx.addPolyline

internal class PolylineNode(
    val polyline: Polyline,
    var onPolylineClick: (Polyline) -> Boolean
) : MapNode {
    override fun onRemoved() {
        polyline.remove()
    }
}

/**
 * 地图上折线的可组合项。
 *
 * @param points 一批顶点到线段的坐标集合。
 * @param color 线段的颜色，默认为黑色
 * @param lineCapType Polyline尾部形状
 * @param lineJoinType Polyline连接处形状
 * @param isDottedLine 是否画虚线，默认为false，画实线。
 * @param dottedLineType 虚线形状。
 * @param geodesic 线段是否为大地曲线，默认false，不画大地曲线。
 * @param visible 线段的可见性。默认为可見
 * @param width 线段的宽度，默认为10。
 * @param zIndex 线段Z轴的值。
 * @param onClick 线段的点击事件
 */
@Composable
@BaiduMapComposable
fun Polyline(
    points: List<LatLng>,
    color: Color = Color.Black,
    lineCapType: PolylineOptions.LineCapType = PolylineOptions.LineCapType.LineCapButt,
    lineJoinType: PolylineOptions.LineJoinType = PolylineOptions.LineJoinType.LineJoinMiter,
    dottedLineType: PolylineDottedLineType = PolylineDottedLineType.DOTTED_LINE_CIRCLE,
    isDottedLine: Boolean = false,
    geodesic: Boolean = false,
    visible: Boolean = true,
    width: Int = 10,
    zIndex: Int = 0,
    onClick: (Polyline) -> Boolean = { false }
) {
    if (points.size < 2) return
    val mapApplier = currentComposer.applier as MapApplier?
    ComposeNode<PolylineNode, MapApplier>(
        factory = {
            val polyline = mapApplier?.map?.addPolyline {
                points(points.take(10000))
                color(color.toArgb())
                lineCapType(lineCapType)
                lineJoinType(lineJoinType)
                dottedLine(isDottedLine)
                dottedLineType(dottedLineType)
                isGeodesic(geodesic)
                visible(visible)
                width(width)
                zIndex(zIndex)
            } ?: error("Error adding Polyline")
            PolylineNode(polyline, onClick)
        },
        update = {
            update(onClick) { this.onPolylineClick = it }

            set(points) { this.polyline.points = it }
            set(color) { this.polyline.color = it.toArgb() }
            set(geodesic) { this.polyline.isGeodesic = it }
            set(isDottedLine) { this.polyline.isDottedLine = it }
            set(visible) { this.polyline.isVisible = it }
            set(width) { this.polyline.width = it }
            set(zIndex) { this.polyline.zIndex = it }
        }
    )
}