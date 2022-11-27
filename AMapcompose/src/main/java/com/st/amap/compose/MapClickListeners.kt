package com.st.amap.compose

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.amap.api.maps.model.IndoorBuildingInfo
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Poi

/**
 * Holder class for top-level click listeners.
 */
internal class MapClickListeners {
    var onIndoorBuildingActive: (IndoorBuildingInfo) -> Unit by mutableStateOf({})
    var onMapClick: (LatLng) -> Unit by mutableStateOf({})
    var onMapLongClick: (LatLng) -> Unit by mutableStateOf({})
    var onMapLoaded: () -> Unit by mutableStateOf({})
    var onMyLocationClick: (Location) -> Unit by mutableStateOf({})
    var onPOIClick: (Poi) -> Unit by mutableStateOf({})
}
