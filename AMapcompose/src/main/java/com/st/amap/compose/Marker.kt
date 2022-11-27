package com.st.amap.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.st.amap.ktx.addMarker

internal class MarkerNode(
    val compositionContext: CompositionContext,
    val marker: Marker,
    val markerState: MarkerState,
    var onMarkerClick: (Marker) -> Boolean,
    var onInfoWindowClick: (Marker) -> Unit,
    var infoWindow: (@Composable (Marker) -> Unit)?,
    var infoContent: (@Composable (Marker) -> Unit)?,
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

    /**
     * Shows the info window for the underlying marker
     */
    fun showInfoWindow() {
        marker?.showInfoWindow()
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
 * 地图上[Marker]的可组合项。
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
 * @param infoWindowEnabled 设置Marker覆盖物的InfoWindow是否允许显示,默认为true
 * @param rotation Marker覆盖物的图片旋转角度，从正北开始，逆时针计算。
 * @param snippet Marker上的文字描述
 * @param title Marker 的标题
 * @param visible Marker覆盖物是否可见。
 * @param zIndex Marker覆盖物 zIndex。
 * @param period 帧数， 刷新周期，值越小速度越快。默认为20，最小为1。
 * @param gps 设置Marker覆盖物的坐标是否是Gps，默认为false。
 * @param onClick Marker点击监听
 * @param onInfoWindowClick InfoWindow点击监听
 */
@Composable
@AMapComposable
fun Marker(
    state: MarkerState,
    alpha: Float = 1.0f,
    anchor: Offset = Offset(0.5f, 1.0f),
    draggable: Boolean = false,
    flat: Boolean = false,
    icon: BitmapDescriptor? = null,
    icons: List<BitmapDescriptor>? = null,
    infoWindowOffset: IntOffset = IntOffset(0, 0),
    infoWindowEnabled: Boolean = true,
    rotation: Float = 0.0f,
    snippet: String? = null,
    title: String? = null,
    visible: Boolean = true,
    zIndex: Float = 0.0f,
    period: Int = 20,
    gps: Boolean = false,
    onClick: (Marker) -> Boolean = { false },
    onInfoWindowClick: (Marker) -> Unit = {},
) {
    MarkerImpl(
        state = state,
        alpha = alpha,
        anchor = anchor,
        draggable = draggable,
        flat = flat,
        icon = icon,
        icons = icons,
        infoWindowOffset = infoWindowOffset,
        infoWindowEnabled = infoWindowEnabled,
        rotation = rotation,
        snippet = snippet,
        title = title,
        visible = visible,
        zIndex = zIndex,
        period = period,
        gps = gps,
        onClick = onClick,
        onInfoWindowClick = onInfoWindowClick,
    )
}

/**
 * 地图上Marker的可组合项，其中可以自定义其InfoWindow窗口。如果不需要此自定义，请使用 [com.st.amap.compose.Marker]。
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
 * @param infoWindowEnabled 设置Marker覆盖物的InfoWindow是否允许显示,默认为true
 * @param rotation Marker覆盖物的图片旋转角度，从正北开始，逆时针计算。
 * @param snippet Marker上的文字描述
 * @param title Marker 的标题
 * @param visible Marker覆盖物是否可见。
 * @param zIndex Marker覆盖物 zIndex。
 * @param period 帧数， 刷新周期，值越小速度越快。默认为20，最小为1。
 * @param gps 设置Marker覆盖物的坐标是否是Gps，默认为false。
 * @param onClick Marker点击监听
 * @param onInfoWindowClick InfoWindow点击监听
 * @param content 可选的,infoWindow窗口的compose表达式
 */
@Composable
@AMapComposable
fun MarkerInfoWindow(
    state: MarkerState,
    alpha: Float = 1.0f,
    anchor: Offset = Offset(0.5f, 1.0f),
    draggable: Boolean = false,
    flat: Boolean = false,
    icon: BitmapDescriptor? = null,
    icons: List<BitmapDescriptor>? = null,
    infoWindowOffset: IntOffset = IntOffset(0, 0),
    infoWindowEnabled: Boolean = true,
    rotation: Float = 0.0f,
    snippet: String? = null,
    title: String? = null,
    visible: Boolean = true,
    zIndex: Float = 0.0f,
    period: Int = 20,
    gps: Boolean = false,
    onClick: (Marker) -> Boolean = { false },
    onInfoWindowClick: (Marker) -> Unit = {},
    content: (@Composable (Marker) -> Unit)? = null,
) {
    MarkerImpl(
        state = state,
        alpha = alpha,
        anchor = anchor,
        draggable = draggable,
        flat = flat,
        icon = icon,
        icons = icons,
        infoWindowOffset = infoWindowOffset,
        infoWindowEnabled = infoWindowEnabled,
        rotation = rotation,
        snippet = snippet,
        title = title,
        visible = visible,
        zIndex = zIndex,
        period = period,
        gps = gps,
        onClick = onClick,
        onInfoWindowClick = onInfoWindowClick,
        infoWindow = content,
    )
}

/**
 * 地图上Marker的可组合项，其中可以自定义其InfoWindow内容。如果不需要此自定义，请使用 [com.st.amap.compose.Marker]。
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
 * @param infoWindowEnabled 设置Marker覆盖物的InfoWindow是否允许显示,默认为true
 * @param rotation Marker覆盖物的图片旋转角度，从正北开始，逆时针计算。
 * @param snippet Marker上的文字描述
 * @param title Marker 的标题
 * @param visible Marker覆盖物是否可见。
 * @param zIndex Marker覆盖物 zIndex。
 * @param period 帧数， 刷新周期，值越小速度越快。默认为20，最小为1。
 * @param gps 设置Marker覆盖物的坐标是否是Gps，默认为false。
 * @param onClick Marker点击监听
 * @param onInfoWindowClick InfoWindow点击监听
 * @param content 可选的,infoWindow内容的compose表达式
 */
@Composable
@AMapComposable
fun MarkerInfoWindowContent(
    state: MarkerState,
    alpha: Float = 1.0f,
    anchor: Offset = Offset(0.5f, 1.0f),
    draggable: Boolean = false,
    flat: Boolean = false,
    icon: BitmapDescriptor? = null,
    icons: List<BitmapDescriptor>? = null,
    infoWindowOffset: IntOffset = IntOffset(0, 0),
    infoWindowEnabled: Boolean = true,
    rotation: Float = 0.0f,
    snippet: String? = null,
    title: String? = null,
    visible: Boolean = true,
    zIndex: Float = 0.0f,
    period: Int = 20,
    gps: Boolean = false,
    onClick: (Marker) -> Boolean = { false },
    onInfoWindowClick: (Marker) -> Unit = {},
    content: (@Composable (Marker) -> Unit)? = null,
) {
    MarkerImpl(
        state = state,
        alpha = alpha,
        anchor = anchor,
        draggable = draggable,
        flat = flat,
        icon = icon,
        icons = icons,
        infoWindowOffset = infoWindowOffset,
        infoWindowEnabled = infoWindowEnabled,
        rotation = rotation,
        snippet = snippet,
        title = title,
        visible = visible,
        zIndex = zIndex,
        period = period,
        gps = gps,
        onClick = onClick,
        onInfoWindowClick = onInfoWindowClick,
        infoContent = content,
    )
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
 * @param infoWindowEnabled 设置Marker覆盖物的InfoWindow是否允许显示,默认为true
 * @param rotation Marker覆盖物的图片旋转角度，从正北开始，逆时针计算。
 * @param snippet Marker上的文字描述
 * @param title Marker 的标题
 * @param visible Marker覆盖物是否可见。
 * @param zIndex Marker覆盖物 zIndex。
 * @param period 帧数， 刷新周期，值越小速度越快。默认为20，最小为1。
 * @param gps 设置Marker覆盖物的坐标是否是Gps，默认为false。
 * @param onClick Marker点击监听
 * @param onInfoWindowClick InfoWindow点击监听
 * @param infoWindow 可选的,infoWindow窗口的compose表达式
 * @param infoContent 可选的,infoWindow内容的compose表达式
 */
@Composable
@AMapComposable
private fun MarkerImpl(
    state: MarkerState,
    alpha: Float = 1.0f,
    anchor: Offset = Offset(0.5f, 1.0f),
    draggable: Boolean = false,
    flat: Boolean = false,
    icon: BitmapDescriptor? = null,
    icons: List<BitmapDescriptor>? = null,
    infoWindowOffset: IntOffset = IntOffset(0, 0),
    infoWindowEnabled: Boolean = true,
    rotation: Float = 0.0f,
    snippet: String? = null,
    title: String? = null,
    visible: Boolean = true,
    zIndex: Float = 0.0f,
    period: Int = 20,
    gps: Boolean = false,
    onClick: (Marker) -> Boolean = { false },
    onInfoWindowClick: (Marker) -> Unit = {},
    infoWindow: (@Composable (Marker) -> Unit)? = null,
    infoContent: (@Composable (Marker) -> Unit)? = null,
) {
    val mapApplier = currentComposer.applier as? MapApplier
    val compositionContext = rememberCompositionContext()
    ComposeNode<MarkerNode, MapApplier>(
        factory = {
            val marker = mapApplier?.map?.addMarker {
                isFlat = flat
                isGps = gps
                alpha(alpha)
                anchor(anchor.x, anchor.y)
                draggable(draggable)
                icon(icon)
                setInfoWindowOffset(infoWindowOffset.x, infoWindowOffset.y)
                infoWindowEnable(infoWindowEnabled)
                position(state.position)
                rotateAngle(rotation)
                snippet(snippet)
                title(title)
                visible(visible)
                zIndex(zIndex)
                icons(icons?.let { ArrayList(it) })
                period(period)
            } ?: error("Error adding marker")
            MarkerNode(
                compositionContext = compositionContext,
                marker = marker,
                markerState = state,
                onMarkerClick = onClick,
                onInfoWindowClick = onInfoWindowClick,
                infoContent = infoContent,
                infoWindow = infoWindow,
            )
        },
        update = {
            update(onClick) { this.onMarkerClick = it }
            update(onInfoWindowClick) { this.onInfoWindowClick = it }
            update(infoContent) { this.infoContent = it }
            update(infoWindow) { this.infoWindow = it }

            set(alpha) { this.marker.alpha = it }
            set(icons) { this.marker.icons = it?.let { ArrayList(it) } }
            set(period) { this.marker.period = it }
            set(anchor) { this.marker.setAnchor(it.x, it.y) }
            set(draggable) { this.marker.isDraggable = it }
            set(flat) { this.marker.isFlat = it }
            set(icon) { this.marker.setIcon(it) }
            set(state.position) { this.marker.position = it }
            set(rotation) { this.marker.rotateAngle = it }
            set(snippet) {
                this.marker.snippet = it
                if (this.marker.isInfoWindowShown) {
                    this.marker.showInfoWindow()
                }
            }
            set(title) {
                this.marker.title = it
                if (this.marker.isInfoWindowShown) {
                    this.marker.showInfoWindow()
                }
            }
            set(visible) { this.marker.isVisible = it }
            set(zIndex) { this.marker.zIndex = it }
        }
    )
}
