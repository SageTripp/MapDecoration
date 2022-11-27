package com.st.amap.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.amap.api.maps.model.Circle
import com.amap.api.maps.model.CircleOptions
import com.amap.api.maps.model.LatLng
import com.st.amap.ktx.addCircle

internal class CircleNode(
    val circle: Circle
) : MapNode {
    override fun onRemoved() {
        circle.remove()
    }
}

/**
 * 地图上[Circle]的可组合项。
 *
 * @param center 圆心经纬度坐标。圆心经纬度坐标不能为Null，圆心经纬度坐标无默认值。
 * @param radius 圆的半径，单位米。半径必须大于等于0。
 * @param holes 圆上面的空心洞
 * @param strokeWidth 圆的边框宽度，单位像素。参数必须大于等于0，默认10。
 * @param strokeColor 圆的边框颜色。如果设置透明，则边框不会被绘制。默认黑色。
 * @param fillColor 圆的填充颜色。填充颜色是绘制边框以内部分的颜色，默认透明。
 * @param visible 圆的可见属性
 * @param zIndex 圆的Z轴数值，默认为0。
 * @param strokeDottedLineType 边框虚线形状
 */
@Composable
@AMapComposable
fun Circle(
    center: LatLng,
    radius: Double = 0.0,
    holes: List<Hole> = emptyList(),
    strokeWidth: Float = 10f,
    strokeColor: Color = Color.Black,
    fillColor: Color = Color.Transparent,
    visible: Boolean = true,
    zIndex: Float = 0f,
    strokeDottedLineType: StrokeDottedLineType = StrokeDottedLineType.Default,
) {
    val mapApplier = currentComposer.applier as? MapApplier
    ComposeNode<CircleNode, MapApplier>(
        factory = {
            val circle = mapApplier?.map?.addCircle {
                addHoles(holes)
                center(center)
                fillColor(fillColor.toArgb())
                radius(radius)
                strokeColor(strokeColor.toArgb())
                strokeWidth(strokeWidth)
                setStrokeDottedLineType(strokeDottedLineType.value)
                visible(visible)
                zIndex(zIndex)
            } ?: error("Error adding circle")
            CircleNode(circle)
        },
        update = {
            set(center) { this.circle.center = it }
            set(fillColor) { this.circle.fillColor = it.toArgb() }
            set(radius) { this.circle.radius = it }
            set(holes) {
                this.circle.holeOptions.clear()
                this.circle.holeOptions.addAll(it.map { hole -> hole.mapToHoleOptions() })
            }
            set(strokeColor) { this.circle.strokeColor = it.toArgb() }
            set(strokeWidth) { this.circle.strokeWidth = it }
            set(visible) { this.circle.isVisible = it }
            set(zIndex) { this.circle.zIndex = it }
            set(strokeDottedLineType) { this.circle.strokeDottedLineType = it.value }
        }
    )
}

internal fun CircleOptions.addHoles(holes: List<Hole>) {
    addHoles(holes.map { it.mapToHoleOptions() })
}