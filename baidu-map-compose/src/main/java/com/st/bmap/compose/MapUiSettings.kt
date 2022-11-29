package com.st.bmap.compose

internal val DefaultMapUiSettings = MapUiSettings()

/**
 * Data class for UI-related settings on the map.
 *
 * @param allGesturesEnabled 设置所有手势是否可用
 * @param compassEnabled 设置指南针是否可见
 * @param zoomGesturesEnabled 是否允许缩放手势
 * @param doubleClickZoomEnabled 是否允许双击放大地图手势
 * @param twoTouchClickZoomEnabled 是否允许双指同时点击缩小地图手势
 * @param enlargeCenterWithDoubleClickEnabled 双击地图按照当前地图中心点放大
 * @param overlookingGesturesEnabled 是否允许俯视手势
 * @param scrollGesturesEnabled 设置拖拽手势是否可用
 * @param rotationGesturesEnabled 设置旋转手势是否可用
 */
data class MapUiSettings(
    val allGesturesEnabled: Boolean = true,
    val compassEnabled: Boolean = false,
    val zoomGesturesEnabled: Boolean = true,
    val doubleClickZoomEnabled: Boolean = true,
    val twoTouchClickZoomEnabled: Boolean = true,
    val enlargeCenterWithDoubleClickEnabled: Boolean = false,
    val overlookingGesturesEnabled: Boolean = true,
    val scrollGesturesEnabled: Boolean = true,
    val rotationGesturesEnabled: Boolean = true,
)