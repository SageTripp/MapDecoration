package com.st.amap.compose

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.amap.api.maps.AMap
import com.amap.api.maps.AMap.OnCameraChangeListener
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.CameraPosition

internal class MapPropertiesNode(
    val map: AMap,
    cameraPositionState: CameraPositionState,
    var clickListeners: MapClickListeners,
    var density: Density,
    var layoutDirection: LayoutDirection,
) : MapNode {

    init {
        cameraPositionState.setMap(map)
    }

    var cameraPositionState = cameraPositionState
        set(value) {
            if (value == field) return
            field.setMap(null)
            field = value
            value.setMap(map)
        }

    override fun onAttached() {
        map.setOnCameraChangeListener(object : OnCameraChangeListener {
            override fun onCameraChange(position: CameraPosition) {
                cameraPositionState.isMoving = true
                cameraPositionState.rawPosition = position
            }

            override fun onCameraChangeFinish(position: CameraPosition) {
                cameraPositionState.isMoving = false
                cameraPositionState.rawPosition = position
            }

        })
        map.setOnMapClickListener { clickListeners.onMapClick(it) }
        map.setOnMapLongClickListener { clickListeners.onMapLongClick(it) }
        map.setOnMapLoadedListener { clickListeners.onMapLoaded() }
        map.addOnPOIClickListener { clickListeners.onPOIClick(it) }
        map.setOnIndoorBuildingActiveListener { clickListeners.onIndoorBuildingActive(it) }
        map.setOnMyLocationChangeListener { clickListeners.onMyLocationClick(it) }
    }

    override fun onRemoved() {
        cameraPositionState.setMap(null)
    }

    override fun onCleared() {
        cameraPositionState.setMap(null)
    }
}

/**
 * Used to keep the primary map properties up to date. This should never leave the map composition.
 */
@SuppressLint("MissingPermission")
@Suppress("NOTHING_TO_INLINE")
@Composable
internal inline fun MapUpdater(
    cameraPositionState: CameraPositionState,
    clickListeners: MapClickListeners,
    locationSource: LocationSource?,
    mapProperties: MapProperties,
    mapUiSettings: MapUiSettings,
) {
    val map = (currentComposer.applier as MapApplier).map
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    ComposeNode<MapPropertiesNode, MapApplier>(
        factory = {
            MapPropertiesNode(
                map = map,
                cameraPositionState = cameraPositionState,
                clickListeners = clickListeners,
                density = density,
                layoutDirection = layoutDirection,
            )
        }
    ) {
        // The node holds density and layoutDirection so that the updater blocks can be
        // non-capturing, allowing the compiler to turn them into singletons
        update(density) { this.density = it }
        update(layoutDirection) { this.layoutDirection = it }

        set(locationSource) { map.setLocationSource(it) }
        set(mapProperties.isConstructingRoadEnabled) { map.setConstructingRoadEnable(it) }
        set(mapProperties.isMapTextEnabled) { map.showMapText(it) }
        set(mapProperties.mapTextZIndex) { map.mapTextZIndex = it }
        set(mapProperties.isBuildingEnabled) { map.showBuildings(it) }
        set(mapProperties.isIndoorEnabled) { map.showIndoorMap(it) }
        set(mapProperties.isMyLocationEnabled) { map.isMyLocationEnabled = it }
        set(mapProperties.isTrafficEnabled) { map.isTrafficEnabled = it }
        set(mapProperties.isTouchPoiEnabled) { map.isTouchPoiEnable = it }
        set(mapProperties.mapType) { map.mapType = it.value }
        set(mapProperties.maxZoomLevel) { map.maxZoomLevel = it }
        set(mapProperties.minZoomLevel) { map.minZoomLevel = it }

        set(mapUiSettings.compassEnabled) { map.uiSettings.isCompassEnabled = it }
        set(mapUiSettings.indoorSwitchEnabled) { map.uiSettings.isIndoorSwitchEnabled = it }
        set(mapUiSettings.myLocationButtonEnabled) { map.uiSettings.isMyLocationButtonEnabled = it }
        set(mapUiSettings.allGesturesEnabled) { map.uiSettings.setAllGesturesEnabled(it) }
        set(mapUiSettings.rotationGesturesEnabled) { map.uiSettings.isRotateGesturesEnabled = it }
        set(mapUiSettings.scrollGesturesEnabled) { map.uiSettings.isScrollGesturesEnabled = it }
        set(mapUiSettings.scaleControlsEnabled) { map.uiSettings.isScaleControlsEnabled = it }
        set(mapUiSettings.tiltGesturesEnabled) { map.uiSettings.isTiltGesturesEnabled = it }
        set(mapUiSettings.zoomControlsEnabled) { map.uiSettings.isZoomControlsEnabled = it }
        set(mapUiSettings.zoomGesturesEnabled) { map.uiSettings.isZoomGesturesEnabled = it }
        set(mapUiSettings.zoomPosition) { map.uiSettings.zoomPosition = it.value }
        set(mapUiSettings.logoBottomMargin) { map.uiSettings.setLogoBottomMargin(with(density) { it.roundToPx() }) }
        set(mapUiSettings.logoLeftMargin) { map.uiSettings.setLogoLeftMargin(with(density) { it.roundToPx() }) }
        set(mapUiSettings.logoPosition) { map.uiSettings.logoPosition = it.value }

        update(cameraPositionState) { this.cameraPositionState = it }
        update(clickListeners) { this.clickListeners = it }
    }
}
