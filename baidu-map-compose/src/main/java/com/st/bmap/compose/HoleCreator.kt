package com.st.bmap.compose

import com.baidu.mapapi.map.CircleHoleOptions
import com.baidu.mapapi.map.HoleOptions
import com.baidu.mapapi.map.PolygonHoleOptions
import com.baidu.mapapi.model.LatLng

sealed class Hole private constructor(
    open val center: LatLng? = null,
    open val radius: Int? = null,
    open val points: List<LatLng>? = null,
) {
    abstract fun mapToHoleOptions(): HoleOptions

    /**
     * 创建圆洞
     *
     * @param center 洞圆心经纬度坐标。
     * @param radius 圆洞的半径，单位米。
     */
    data class CircleHole(override val center: LatLng, override val radius: Int) :
        Hole(center, radius) {
        override fun mapToHoleOptions(): CircleHoleOptions =
            CircleHoleOptions().center(center).radius(radius)
    }

    data class PolygonHole(override val points: List<LatLng>) : Hole(points = points) {
        override fun mapToHoleOptions(): PolygonHoleOptions = PolygonHoleOptions().addPoints(points)
    }
}


object HoleCreator {
    fun create(center: LatLng, radius: Int): Hole.CircleHole {
        return Hole.CircleHole(center, radius)
    }

    fun create(points: List<LatLng>): Hole.PolygonHole {
        return Hole.PolygonHole(points)
    }
}