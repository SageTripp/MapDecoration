package com.st.bmap.compose

import android.annotation.SuppressLint
import android.graphics.Point
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener
import com.baidu.mapapi.map.MapPoi
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.model.LatLng

internal class MapPropertiesNode(
    val map: BaiduMap,
    mapState: BaiduMapState,
    var clickListeners: MapClickListeners,
    var density: Density,
    var layoutDirection: LayoutDirection,
) : MapNode {

    init {
        mapState.setMap(map)
    }

    var mapState = mapState
        set(value) {
            if (value == field) return
            field.setMap(null)
            field = value
            value.setMap(map)
        }

    override fun onAttached() {
        map.setOnMapStatusChangeListener(object : OnMapStatusChangeListener {
            override fun onMapStatusChangeStart(status: MapStatus) {
                mapState.isMoving = true
                mapState.rawStatus = status
            }

            override fun onMapStatusChangeStart(status: MapStatus, reason: Int) {
                mapState.mapStatusChangeStartedReason = MapStatusChangeStartedReason.fromInt(reason)
                mapState.isMoving = true
                mapState.rawStatus = status
            }

            override fun onMapStatusChange(status: MapStatus) {
                mapState.rawStatus = status
            }

            override fun onMapStatusChangeFinish(status: MapStatus) {
                mapState.isMoving = false
                mapState.rawStatus = status
            }

        })
        map.setOnMapClickListener(object : OnMapClickListener {
            override fun onMapClick(latLng: LatLng) {
                clickListeners.onMapClick(latLng)
            }

            override fun onMapPoiClick(poi: MapPoi) {
                clickListeners.onPOIClick(poi)
            }

        })
        map.setOnMapLongClickListener { clickListeners.onMapLongClick(it) }
        map.setOnMapLoadedCallback { clickListeners.onMapLoaded() }
        map.setOnBaseIndoorMapListener { success, indoorMapInfo ->
            clickListeners.onIndoorBuildingActive(indoorMapInfo)
        }
        map.setOnMyLocationClickListener { clickListeners.onMyLocationClick(map.locationData) }
    }

    override fun onRemoved() {
        mapState.setMap(null)
    }

    override fun onCleared() {
        mapState.setMap(null)
    }
}

/**
 * Used to keep the primary map properties up to date. This should never leave the map composition.
 */
@SuppressLint("MissingPermission")
@Suppress("NOTHING_TO_INLINE")
@Composable
internal inline fun MapUpdater(
    mapState: BaiduMapState,
    clickListeners: MapClickListeners,
    mapProperties: MapProperties,
    mapUiSettings: MapUiSettings,
    contentPadding: PaddingValues,
) {
    val map = (currentComposer.applier as MapApplier).map
    val mapView = (currentComposer.applier as MapApplier).mapView
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    ComposeNode<MapPropertiesNode, MapApplier>(
        factory = {
            MapPropertiesNode(
                map = map,
                mapState = mapState,
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

        set(contentPadding) {
            with(density) {
                contentPadding.run {
                    map.setViewPadding(
                        calculateLeftPadding(layoutDirection).roundToPx(),
                        calculateTopPadding().roundToPx(),
                        calculateRightPadding(layoutDirection).roundToPx(),
                        calculateBottomPadding().roundToPx()
                    )
                }
            }
        }

        set(mapProperties.logoPosition) { mapView.logoPosition = it }
        set(mapProperties.zoomControllerEnabled) { mapView.showZoomControls(it) }
        set(mapProperties.scaleControllerEnabled) { mapView.showScaleControl(it) }

        set(mapProperties.baiduHeatMapEnabled) { map.isBaiduHeatMapEnabled = it }
        set(mapProperties.isIndoorEnabled) { map.setIndoorEnable(it) }
        if (mapProperties.compassPosition != DpOffset.Unspecified) {
            set(mapProperties.compassPosition) {
                with(density) {
                    map.compassPosition = Point(it.x.roundToPx(), it.y.roundToPx())
                }
            }
        }
        set(mapProperties.compassIcon) { it?.also { map.setCompassIcon(it) } }
        set(mapProperties.compassEnabled) { map.setCompassEnable(it) }
        set(mapProperties.isTrafficEnabled) { map.isTrafficEnabled = it }
        set(mapProperties.showMapPoi) { map.showMapPoi(it) }
        set(mapProperties.showMapIndoorPoi) { map.showMapIndoorPoi(it) }
        set(mapProperties.pixelFormatTransparent) { map.setPixelFormatTransparent(it) }
        set(mapProperties.overlayUnderPoi) { map.setOverlayUnderPoi(it) }
        set(mapProperties.operateLayerEnabled) { map.showOperateLayer(it) }
        set(mapProperties.fontSizeLevel) { map.fontSizeLevel = it.value }
        set(mapProperties.mapLanguage) { map.mapLanguage = it }
        set(mapProperties.mapType) { map.mapType = it.value }
        set(mapProperties.isBuildingEnabled) { map.isBuildingsEnabled = it }
        set(mapProperties.isMyLocationEnabled) { map.isMyLocationEnabled = it }
        set(mapProperties.maxZoomLevel to mapProperties.minZoomLevel) {
            map.setMaxAndMinZoomLevel(it.first, it.second)
        }

        set(mapUiSettings.allGesturesEnabled) { map.uiSettings.setAllGesturesEnabled(it) }
        set(mapUiSettings.compassEnabled) { map.uiSettings.isCompassEnabled = it }
        set(mapUiSettings.zoomGesturesEnabled) { map.uiSettings.isZoomGesturesEnabled = it }
        set(mapUiSettings.doubleClickZoomEnabled) { map.uiSettings.setDoubleClickZoomEnabled(it) }
        set(mapUiSettings.twoTouchClickZoomEnabled) { map.uiSettings.setTwoTouchClickZoomEnabled(it) }
        set(mapUiSettings.enlargeCenterWithDoubleClickEnabled) {
            map.uiSettings.setEnlargeCenterWithDoubleClickEnable(it)
        }
        set(mapUiSettings.overlookingGesturesEnabled) {
            map.uiSettings.isOverlookingGesturesEnabled = it
        }
        set(mapUiSettings.scrollGesturesEnabled) { map.uiSettings.isScrollGesturesEnabled = it }
        set(mapUiSettings.rotationGesturesEnabled) { map.uiSettings.isRotateGesturesEnabled = it }

        update(mapState) { this.mapState = it }
        update(clickListeners) { this.clickListeners = it }
    }
}
