package com.st.amap.compose.baidu

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.baidu.mapapi.model.LatLng
import com.st.bmap.compose.BaiduMap
import com.st.bmap.compose.Circle
import com.st.bmap.compose.HoleCreator
import com.st.bmap.compose.MapEffect
import com.st.bmap.compose.MapProperties
import com.st.bmap.compose.Marker
import com.st.bmap.compose.Polygon
import com.st.bmap.compose.Polyline
import com.st.bmap.compose.rememberBaiduMapState
import com.st.bmap.compose.rememberMarkerState

@Composable
fun BMapTest(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        val markers = remember { mutableStateListOf<LatLng>() }
        val mapState = rememberBaiduMapState()
        BaiduMap(
            mapState = mapState,
            properties = MapProperties(
                showMapPoi = false,
                showMapIndoorPoi = false,
                isBuildingEnabled = true,
            ),
            onMapClick = {
                markers.add(it)
            },
            onMapLongClick = {
                markers.clear()
            }
        ) {
            MapEffect(markers.size) {
                println(it.allInfoWindows.size)
            }
            markers.forEachIndexed { index, latLng ->
                val marker = rememberMarkerState(position = latLng)
                Marker(
                    state = marker, visible = index == 0 || index == markers.lastIndex,
                    onClick = {
                        marker.showInfoWindow(true)
                        true
                    },
                    infoWindow = {
                        Button(onClick = { marker.hideInfoWindow() }) {
                            Text(text = latLng.toString())
                        }
                    }
                )
                Circle(
                    center = latLng,
                    radius = 50,
                    fillColor = Color.Cyan.copy(0.2f),
                    strokeColor = Color.Transparent,
                    holes = listOf(HoleCreator.create(latLng, 30))
                )
            }
            Polygon(
                points = markers.toList(),
                fillColor = Color.Magenta.copy(0.3f),
                strokeColor = Color.Transparent
            )
            Polyline(points = markers.toList())
        }
    }
}