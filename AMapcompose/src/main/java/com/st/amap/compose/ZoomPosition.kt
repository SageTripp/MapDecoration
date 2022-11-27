package com.st.amap.compose

import com.amap.api.maps.AMapOptions

enum class ZoomPosition(val value:Int) {
    RightCenter(AMapOptions.ZOOM_POSITION_RIGHT_CENTER),
    RightBottom(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM)
}