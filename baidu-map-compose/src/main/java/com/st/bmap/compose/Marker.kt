package com.st.bmap.compose

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.baidu.mapapi.map.BitmapDescriptor
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.InfoWindow
import com.baidu.mapapi.map.Marker
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType
import com.baidu.mapapi.model.LatLng
import com.st.bmap.ktx.addMarker
import kotlin.math.roundToInt

internal class MarkerNode(
    val compositionContext: CompositionContext,
    val marker: Marker,
    val markerState: MarkerState,
    var onMarkerClick: (Marker) -> Boolean,
) : MapNode {
    override fun onAttached() {
        markerState.marker = marker
    }

    override fun onRemoved() {
        markerState.marker = null
        marker.remove()
    }

    override fun onCleared() {
        markerState.marker = null
        marker.remove()
    }
}

@Immutable
enum class DragState {
    START, DRAG, END
}

/**
 * 控制和观察标记状态的状态对象。
 *
 * @param position marker的初始位置
 */
class MarkerState(position: LatLng) {
    lateinit var ctx: Context

    /**
     * marker的当前位置.
     */
    var position: LatLng by mutableStateOf(position)

    /**
     * marker拖动时当前的[DragState].
     */
    var dragState: DragState by mutableStateOf(DragState.END)
        internal set

    // The marker associated with this MarkerState.
    internal var marker: Marker? = null
        set(value) {
            if (field == null && value == null) return
            if (field != null && value != null) {
                error("MarkerState may only be associated with one Marker at a time.")
            }
            field = value
        }

    internal var infoWindowContent: InfoWindow? by mutableStateOf(null)

    /**
     * Shows the info window for the underlying marker
     */
    fun showInfoWindow(smoothMove: Boolean = false) {
        marker?.run {
            if (smoothMove) showSmoothMoveInfoWindow(infoWindowContent)
            else showInfoWindow(infoWindowContent)
        }
    }

    /**
     * Hides the info window for the underlying marker
     */
    fun hideInfoWindow() {
        marker?.hideInfoWindow()

    }

    companion object {
        /**
         * The default saver implementation for [MarkerState]
         */
        val Saver: Saver<MarkerState, LatLng> = Saver(
            save = { it.position },
            restore = { MarkerState(it) }
        )
    }
}

@Composable
fun rememberMarkerState(
    position: LatLng,
    key: String? = null,
): MarkerState = rememberSaveable(key = key, saver = MarkerState.Saver) {
    MarkerState(position)
}

/**
 * 高德地图中Marker的内部实现
 *
 * @param state 声明 [MarkerState] 用于控制或观察标记状态，例如其位置和信息窗口
 * @param alpha Marker覆盖物的透明度,透明度范围[0,1] 1为不透明
 * @param anchor 锚点是marker 图标接触地图平面的点。图标的左顶点为（0,0）点，右底点为（1,1）点。默认为（0.5,1.0）
 * @param draggable 表示Marker是否可拖拽，true表示可拖拽，false表示不可拖拽。
 * @param flat 平贴地图设置为 true，面对镜头设置为 false。
 * @param icon 图标的BitmapDescriptor对象,相同图案的 icon 的 Marker 最好使用同一个 BitmapDescriptor 对象以节省内存空间。
 * @param icons Marker的动画帧列表。多张图片模拟gif的效果。
 * @param infoWindowOffset Marker覆盖物的InfoWindow相对Marker的偏移。
 * 坐标系原点为marker的中上点，InfoWindow相对此原点的像素偏移，向左和向上为负，向右和向下为正。InfoWindow的初始位置为marker上边线与InfoWindow下边线重合，并且两者的中线在一条线上。
 * @param rotation Marker覆盖物的图片旋转角度，从正北开始，逆时针计算。
 * @param visible Marker覆盖物是否可见。
 * @param zIndex Marker覆盖物 zIndex。
 * @param period 帧数， 刷新周期，值越小速度越快。默认为20，最小为1。
 * @param onClick Marker点击监听
 * @param infoWindow 可选的,infoWindow窗口的compose表达式
 */
@Composable
@BaiduMapComposable
fun BaiduMapScope.Marker(
    state: MarkerState,
    alpha: Float = 1.0f,
    anchor: Offset = Offset(0.5f, 1.0f),
    draggable: Boolean = false,
    animatorType: MarkerAnimateType = MarkerAnimateType.none,
    flat: Boolean = false,
    icon: BitmapDescriptor? = null,
    icons: List<BitmapDescriptor>? = null,
    infoWindowOffset: Int = -50,
    rotation: Float = 0.0f,
    visible: Boolean = true,
    zIndex: Int = 0,
    period: Int = 20,
    onClick: (Marker) -> Boolean = { false },
    infoWindow: (@Composable () -> Unit)? = null,
) {

    val mapApplier = currentComposer.applier as? MapApplier
    val compositionContext = rememberCompositionContext()
    val ctx = LocalContext.current
    val density = LocalDensity.current
    val defaultIcon = remember {
        BitmapDescriptorFactory.fromBitmap(
            BitmapFactory.decodeResource(ctx.resources, R.drawable.ic_place)
        )
    }
    ComposeNode<MarkerNode, MapApplier>(
        factory = {
            val marker = mapApplier?.map?.addMarker {
                flat(flat)
                alpha(alpha)
                anchor(anchor.x, anchor.y)
                draggable(draggable)
                animateType(animatorType)
                if (icons == null && icon == null) {
                    icon(defaultIcon)
                } else {
                    if (icon != null) {
                        icon(icon)
                    }
                    if (icons != null) {
                        icons(ArrayList(icons))
                    }
                }
                infoWindow?.run {
                    this@addMarker.infoWindow(
                        InfoWindow(
                            ComposeView(ctx).apply { setContent { invoke() } },
                            state.position,
                            infoWindowOffset,
                            true,
                            density.density.roundToInt()
                        ).also { state.infoWindowContent = it }
                    )
                }
                position(state.position)
                rotate(rotation)
                visible(visible)
                zIndex(zIndex)

                period(period)
            } ?: error("Error adding marker")
            MarkerNode(
                compositionContext = compositionContext,
                marker = marker,
                markerState = state,
                onMarkerClick = onClick,
            )
        },
        update = {
            update(onClick) { this.onMarkerClick = it }
            update(infoWindow) {
                if (it == null) this.marker.hideInfoWindow()
                else this.marker.updateInfoWindowView(ComposeView(ctx).apply {
                    setContent { it.invoke() }
                })
            }
            update(infoWindowOffset) { this.marker.updateInfoWindowYOffset(it) }

            set(alpha) { this.marker.alpha = it }
            set(icons) { it?.let { this.marker.icons = ArrayList(it) } }
            set(period) { this.marker.period = it }
            set(anchor) { this.marker.setAnchor(it.x, it.y) }
            set(draggable) { this.marker.isDraggable = it }
            set(animatorType) { this.marker.setAnimateType(it.ordinal) }
            set(flat) { this.marker.isFlat = it }
            set(icon) { this.marker.icon = it ?: defaultIcon }
            set(state.position) { this.marker.position = it }
            set(rotation) { this.marker.rotate = it }
            set(visible) { this.marker.isVisible = it }
            set(zIndex) { this.marker.zIndex = it }
        }
    )
}
