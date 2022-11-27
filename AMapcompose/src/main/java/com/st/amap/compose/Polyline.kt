package com.st.amap.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Polyline
import com.st.amap.ktx.addPolyline

internal class PolylineNode(
    val polyline: Polyline,
    var onPolylineClick: (Polyline) -> Unit
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
@AMapComposable
fun Polyline(
    points: List<LatLng>,
    color: Color = Color.Black,
    lineCapType: LineCapType = LineCapType.Butt,
    lineJoinType: LineJoinType = LineJoinType.Miter,
    isDottedLine: Boolean = false,
    dottedLineType: StrokeDottedLineType = StrokeDottedLineType.Default,
    geodesic: Boolean = false,
    visible: Boolean = true,
    width: Float = 10f,
    zIndex: Float = 0f,
    onClick: (Polyline) -> Unit = {}
) {
    val mapApplier = currentComposer.applier as MapApplier?
    ComposeNode<PolylineNode, MapApplier>(
        factory = {
            val polyline = mapApplier?.map?.addPolyline {
                addAll(points)
                color(color.toArgb())
                lineCapType(lineCapType.polylineType)
                lineJoinType(lineJoinType.polylineType)
                setDottedLine(isDottedLine)
                setDottedLineType(dottedLineType.value)
                geodesic(geodesic)
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