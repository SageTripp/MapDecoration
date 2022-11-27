package com.st.amap.compose

import androidx.annotation.FloatRange

internal val DefaultMapProperties = MapProperties()

/**
 * Data class for properties that can be modified on the map.
 *
 * @param isConstructingRoadEnabled 设置在建道路图层是否显示,默认不显示
 * @param isMapTextEnabled 设置是否显示底图文字标注，默认显示。
 * @param mapTextZIndex 设置地图底图文字标注的层级指数，默认为0，用来比较覆盖物（polyline、polygon、circle等）的zIndex。
 * @param isBuildingEnabled 设置是否显示3D建筑物，默认显示。
 * @param isIndoorEnabled 设置是否显示室内地图，默认不显示。
 * @param isMyLocationEnabled 设置是否打开定位图层（myLocationOverlay）。
 * @param isTrafficEnabled 设置是否打开交通路况图层。
 * @param isTouchPoiEnabled 设置地图POI是否允许点击。
 * @param mapType 设置地图模式。
 * @param maxZoomLevel 设置地图最大缩放级别 缩放级别范围为[3, 20],超出范围将按最大级别计算 。
 * @param minZoomLevel 设置最小缩放级别 缩放级别范围为[3, 20],超出范围将按最小级别计算
 */
data class MapProperties(
    val isConstructingRoadEnabled: Boolean = false,
    val isMapTextEnabled: Boolean = true,
    val mapTextZIndex: Int = 0,
    val isBuildingEnabled: Boolean = false,
    val isIndoorEnabled: Boolean = false,
    val isMyLocationEnabled: Boolean = false,
    val isTrafficEnabled: Boolean = false,
    val isTouchPoiEnabled: Boolean = false,
    val mapType: MapType = MapType.NORMAL,
    @FloatRange(from = 3.0, to = 20.0)
    val maxZoomLevel: Float = 20.0f,
    @FloatRange(from = 3.0, to = 20.0)
    val minZoomLevel: Float = 3.0f,
)

