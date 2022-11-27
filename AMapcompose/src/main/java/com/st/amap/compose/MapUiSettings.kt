package com.st.amap.compose

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val DefaultMapUiSettings = MapUiSettings()

/**
 * Data class for UI-related settings on the map.
 *
 * @param compassEnabled 设置指南针是否可见
 * @param indoorSwitchEnabled 设置室内地图楼层切换控件是否可见
 * @param myLocationButtonEnabled 设置定位按钮是否可见
 * @param allGesturesEnabled 设置所有手势是否可用
 * @param rotationGesturesEnabled 设置旋转手势是否可用
 * @param scrollGesturesEnabled 设置拖拽手势是否可用
 * @param scaleControlsEnabled 设置比例尺控件是否可见
 * @param tiltGesturesEnabled 设置倾斜手势是否可用
 * @param zoomControlsEnabled 设置缩放按钮是否可见
 * @param zoomGesturesEnabled 设置双指缩放手势是否可用
 * @param zoomPosition 设置缩放按钮的位置
 * @param logoBottomMargin 设置LOGO下边界距离屏幕底部的边距
 * @param logoLeftMargin 设置LOGO左边界距离屏幕左侧的边距
 * @param logoPosition 设置“高德地图”LOGO的位置
 */
data class MapUiSettings(
    val compassEnabled: Boolean = false,
    val indoorSwitchEnabled: Boolean = true,
    val myLocationButtonEnabled: Boolean = true,
    val allGesturesEnabled: Boolean = true,
    val rotationGesturesEnabled: Boolean = true,
    val scrollGesturesEnabled: Boolean = true,
    val scaleControlsEnabled: Boolean = false,
    val tiltGesturesEnabled: Boolean = true,
    val zoomControlsEnabled: Boolean = true,
    val zoomGesturesEnabled: Boolean = true,
    val zoomPosition: ZoomPosition = ZoomPosition.RightBottom,
    val logoBottomMargin: Dp = 32.dp,
    val logoLeftMargin: Dp = 32.dp,
    val logoPosition: LogoPosition = LogoPosition.BottomLeft,
)