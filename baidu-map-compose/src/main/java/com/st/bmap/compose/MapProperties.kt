package com.st.bmap.compose

import android.graphics.Bitmap
import androidx.annotation.FloatRange
import androidx.compose.ui.unit.DpOffset
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.LogoPosition
import com.baidu.mapapi.map.MapLanguage

internal val DefaultMapProperties = MapProperties()

/**
 * Data class for properties that can be modified on the map.
 *
 * @param logoPosition 百度地图logo的位置
 * @param zoomControllerEnabled 是否显示缩放控件
 * @param scaleControllerEnabled 是否显示比例尺
 * @param baiduHeatMapEnabled 是否打开百度热力图图层(百度自有数据图层) 注：地图层级大于11时，可显示热力图
 * @param compassPosition 指南针的位置
 * @param compassIcon 指南针自定义图标
 * @param compassEnabled 指南针是否显示
 * @param isBuildingEnabled 设置是否显示3D建筑物，默认显示。
 * @param isIndoorEnabled 设置是否显示室内图, 默认室内图不显示 室内图只有在缩放级别[17， 22]范围才生效，但是在18级之上（包含18级）才会有楼层边条显示。
 * @param isMyLocationEnabled 设置是否打开定位图层（myLocationOverlay）。
 * @param isTrafficEnabled 是否打开交通图层
 * @param showMapPoi 控制是否显示底图默认标注, 默认显示
 * @param showMapIndoorPoi 设置是否显示室内图标注, 默认显示
 * @param pixelFormatTransparent 设置地图背景透明
 * @param overlayUnderPoi 设置覆盖物图层相对于Poi图层的图层顺序 默认覆盖物图层在Poi图层之上 通过调用该接口，设置设置覆盖物图层在Poi图层之下，可以解决覆盖物遮盖Poi名称的问题，比如Polyline遮挡路线名称 V5.4.0版本新增接口
 * true——覆盖物图层位于Poi图层之下；false——覆盖物图层位于Poi图层之上，默认值为false默认 false
 * @param operateLayerEnabled 在建道路图层开关 V7.4.0版本新增接口
 * @param fontSizeLevel 设置地图显示大小等级
 * @param mapLanguage 设置地图语言类型 7.4.0新增接口。
 * @param mapType 设置地图模式。
 * @param maxZoomLevel 设置地图最大缩放级别 缩放级别范围为[3, 20],超出范围将按最大级别计算 。
 * @param minZoomLevel 设置最小缩放级别 缩放级别范围为[3, 20],超出范围将按最小级别计算
 */
data class MapProperties(
    val logoPosition: LogoPosition = LogoPosition.logoPostionleftBottom,
    val zoomControllerEnabled: Boolean = true,
    val scaleControllerEnabled: Boolean = true,
    val baiduHeatMapEnabled: Boolean = false,
    val isIndoorEnabled: Boolean = false,
    val compassPosition: DpOffset = DpOffset.Unspecified,
    val compassIcon: Bitmap? = null,
    val compassEnabled: Boolean = true,
    val isTrafficEnabled: Boolean = false,
    val showMapPoi: Boolean = true,
    val showMapIndoorPoi: Boolean = true,
    val pixelFormatTransparent: Boolean = false,
    val overlayUnderPoi: Boolean = false,
    val operateLayerEnabled: Boolean = true,
    val fontSizeLevel: FontSizeLevel = FontSizeLevel.Normal,
    val mapLanguage: MapLanguage = MapLanguage.CHINESE,
    val mapType: MapType = MapType.NORMAL,
    val isBuildingEnabled: Boolean = false,
    val isMyLocationEnabled: Boolean = false,
    @FloatRange(
        from = BaiduMap.REAL_MIN_ZOOM_LEVEL.toDouble(),
        to = BaiduMap.REAL_MAX_ZOOM_LEVEL.toDouble()
    )
    val maxZoomLevel: Float = BaiduMap.REAL_MAX_ZOOM_LEVEL,
    @FloatRange(
        from = BaiduMap.REAL_MIN_ZOOM_LEVEL.toDouble(),
        to = BaiduMap.REAL_MAX_ZOOM_LEVEL.toDouble()
    )
    val minZoomLevel: Float = BaiduMap.REAL_MIN_ZOOM_LEVEL,
)

