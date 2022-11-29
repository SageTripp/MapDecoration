package com.st.bmap.ktx.model

import android.graphics.Point
import android.util.Size
import androidx.annotation.FloatRange
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatusUpdate
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.model.LatLngBounds


public inline fun BaiduMap.mapStatus(update: MapStatusGenerator.() -> MapStatusUpdate) {
    setMapStatus(MapStatusGenerator.update())
    MapStatusGenerator.newLatLngBounds(LatLngBounds.Builder().build())
}

object MapStatusGenerator {
    /**
     * 设置地图新中心点
     *
     * @param latLng 地图新中心点
     */
    fun newLatLng(latLng: LatLng): MapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng)

    /**
     * 设置地图中心点以及缩放级别
     *
     * @param latLng 地图新中心点
     * @param zoom 缩放级别 [4, 21]
     */
    fun newLatLngZoom(
        latLng: LatLng,
        @FloatRange(from = 4.0, to = 21.0) zoom: Float,
    ): MapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, zoom)

    /**
     * 根据Padding设置地理范围的合适缩放级别
     *
     * @param latLngBounds 地图显示地理范围
     * @param padding 相对于地图View的边距，单位为像素
     */
    fun newLatLngZoom(
        latLngBounds: LatLngBounds,
        padding: Padding,
    ): MapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(
        latLngBounds,
        padding.left,
        padding.top,
        padding.right,
        padding.bottom
    )

    /**
     * 设置显示在规定宽高中的地图地理范围
     *
     * @param latLngBounds 地图显示地理范围
     * @param size 大于零
     */
    fun newLatLngBounds(latLngBounds: LatLngBounds, size: Size? = null): MapStatusUpdate =
        if (size == null) MapStatusUpdateFactory.newLatLngBounds(latLngBounds)
        else MapStatusUpdateFactory.newLatLngBounds(latLngBounds, size.width, size.height)


    /**
     * 设置显示在指定相对于MapView的padding中的地图地理范围
     *
     * @param latLngBounds 地图显示地理范围
     * @param padding 相对于地图View的边距，单位为像素
     */
    fun newLatLngBounds(
        latLngBounds: LatLngBounds,
        padding: Padding,
    ): MapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(
        latLngBounds,
        padding.left,
        padding.top,
        padding.right,
        padding.bottom,
    )

    /**
     * 放大地图缩放级别
     */
    fun zoomIn(): MapStatusUpdate = MapStatusUpdateFactory.zoomIn()

    /**
     * 缩小地图缩放级别
     */
    fun zoomOut(): MapStatusUpdate = MapStatusUpdateFactory.zoomOut()

    /**
     * 根据给定增量缩放地图级别
     * @param amount 地图缩放级别增量
     * @param point 地图缩放中心点屏幕坐标
     */
    fun zoomBy(amount: Float, point: Point? = null): MapStatusUpdate =
        if (null == point) MapStatusUpdateFactory.zoomBy(amount)
        else MapStatusUpdateFactory.zoomBy(amount, point)

    /**
     * 设置地图缩放级别
     *
     * @param zoom 地图缩放级别
     */
    fun zoomTo(zoom: Float): MapStatusUpdate = MapStatusUpdateFactory.zoomTo(zoom)

    /**
     * 按像素移动地图中心点
     *
     * @param xPixel 水平方向移动像素数
     * @param yPixel 垂直方向移动像素数
     */
    fun scrollBy(xPixel: Int, yPixel: Int): MapStatusUpdate =
        MapStatusUpdateFactory.scrollBy(xPixel, yPixel)
}

data class Padding(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
)

fun Padding(horizontal: Int, vertical: Int) = Padding(horizontal, vertical, horizontal, vertical)