package com.st.amap.compose

import androidx.compose.runtime.Immutable
import com.amap.api.maps.AMap

/**
 * Enumerates the different types of map tiles.
 */
@Immutable
enum class MapType(val value: Int) {
    NORMAL(AMap.MAP_TYPE_NORMAL),
    SATELLITE(AMap.MAP_TYPE_SATELLITE),
    NIGHT(AMap.MAP_TYPE_NIGHT),
    NAVI(AMap.MAP_TYPE_NAVI),
    BUS(AMap.MAP_TYPE_BUS);
}
