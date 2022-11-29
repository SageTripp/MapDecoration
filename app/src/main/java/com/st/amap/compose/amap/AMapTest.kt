package com.st.amap.compose.amap

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.st.amap.compose.AMap
import com.st.amap.compose.Circle
import com.st.amap.compose.HoleCreator
import com.st.amap.compose.MapProperties
import com.st.amap.compose.Marker
import com.st.amap.compose.MarkerInfoWindow
import com.st.amap.compose.MarkerInfoWindowContent
import com.st.amap.compose.Polyline
import com.st.amap.compose.rememberCameraPositionState
import com.st.amap.compose.rememberMarkerState
import kotlinx.coroutines.launch

@Composable
fun AMapTest(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        val scope = rememberCoroutineScope()
        val markers = remember { mutableStateListOf<LatLng>() }
        val circles = remember { mutableStateListOf<Pair<LatLng, Double>>() }
        val cameraState = rememberCameraPositionState()

        var showText by remember { mutableStateOf(true) }
        val properties by remember(showText) { mutableStateOf(MapProperties(isMapTextEnabled = showText)) }
        Box {
            AMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraState,
                properties = properties,
                onMapClick = {
                    markers.add(it)
//                        val distance = circles.lastOrNull()?.run { (first - it).toDouble() } ?: 45.0
                    circles.add(it to 25.0)
                },
                onMapLongClick = {
                    markers.clear()
                    circles.clear()
                }
            ) {
                circles.forEach {
                    Circle(
                        center = it.first, radius = it.second,
                        strokeColor = Color.Cyan.copy(0.4f),
                        fillColor = Color.Magenta.copy(0.15f),
                        holes = listOf(HoleCreator.create(it.first, it.second / 2))
                    )
                }
                Polyline(points = markers.toList())
//                    Polygon(
//                        points = markers.toList(),
//                        fillColor = Color.Red.copy(0.2f),
//                        holes = circles.map { HoleCreator.create(it.first, it.second) },
//                    )
                markers.forEachIndexed { index, latlng ->
                    val markerState = rememberMarkerState(position = latlng)
                    val visible = index == 0 || index == markers.lastIndex
                    when ((index % 3)) {
                        0 -> Marker(
                            markerState, title = latlng.toString(), visible = visible,
                            onClick = { marker ->
                                scope.launch {
                                    cameraState.animate(CameraUpdateFactory.changeLatLng(marker.position))
                                }
                                marker.showInfoWindow()
                                true
                            },
                        )

                        1 -> MarkerInfoWindow(
                            markerState, visible = visible,
                            onClick = { marker ->
                                marker.showInfoWindow()
                                true
                            },
                            title = latlng.toString()
                        ) { marker ->
                            Card {
                                Text(text = marker.position.latitude.toString())
                            }
                        }

                        2 -> MarkerInfoWindowContent(
                            markerState, visible = visible,
                            onClick = { marker ->
                                marker.showInfoWindow()
                                true
                            },
                            onInfoWindowClick = { marker -> marker.hideInfoWindow() },
                            title = latlng.toString()
                        ) { marker ->
                            Card(backgroundColor = Color.Cyan) {
                                Text(text = marker.position.latitude.toString())
                            }
                        }
                    }
                }
            }

            Button(onClick = { showText = !showText }) {
                if (showText) Text(text = "隐藏文字") else Text(text = "显示文字")
            }
        }
    }
}