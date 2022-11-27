package com.st.amap.ktx

import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng

operator fun LatLng.minus(distanceOf: LatLng) = AMapUtils.calculateLineDistance(this, distanceOf)