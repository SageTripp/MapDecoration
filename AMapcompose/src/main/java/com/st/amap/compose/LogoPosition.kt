package com.st.amap.compose

import com.amap.api.maps.AMapOptions

enum class LogoPosition(val value: Int) {
    BottomLeft(AMapOptions.LOGO_POSITION_BOTTOM_LEFT),
    BottomCenter(AMapOptions.LOGO_POSITION_BOTTOM_CENTER),
    BottomRight(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
}