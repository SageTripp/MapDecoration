package com.st.bmap.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.baidu.mapapi.map.MapBaseIndoorMapInfo
import com.baidu.mapapi.map.MapPoi
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng

/**
 * Holder class for top-level click listeners.
 */
internal class MapClickListeners {
    var onIndoorBuildingActive: (MapBaseIndoorMapInfo) -> Unit by mutableStateOf({})
    var onMapClick: (LatLng) -> Unit by mutableStateOf({})
    var onMapLongClick: (LatLng) -> Unit by mutableStateOf({})
    var onMapLoaded: () -> Unit by mutableStateOf({})
    var onMyLocationClick: (MyLocationData) -> Boolean by mutableStateOf({ false })
    var onPOIClick: (MapPoi) -> Unit by mutableStateOf({})
}
