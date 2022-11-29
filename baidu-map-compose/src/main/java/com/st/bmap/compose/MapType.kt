package com.st.bmap.compose

import androidx.compose.runtime.Immutable
import com.baidu.mapapi.map.BaiduMap

/**
 * Enumerates the different types of map tiles.
 */
@Immutable
enum class MapType(val value: Int) {
    NORMAL(BaiduMap.MAP_TYPE_NORMAL),
    SATELLITE(BaiduMap.MAP_TYPE_SATELLITE),
    NONE(BaiduMap.MAP_TYPE_NONE),
}
