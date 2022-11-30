package com.st.bmap.compose

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.baidu.mapapi.map.BaiduMapOptions
import com.baidu.mapapi.map.MapBaseIndoorMapInfo
import com.baidu.mapapi.map.MapPoi
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng
import kotlinx.coroutines.awaitCancellation

/**
 * A compose container for a [MapView].
 *
 * @param modifier Modifier to be applied to the AMap
 * @param mapState the [BaiduMapState] to be used to control or observe the map's
 * camera state
 * @param mapOptionsFactory the block for creating the [BaiduMapOptions] provided when the
 * map is created
 * @param properties the properties for the map
 * @param uiSettings the [MapUiSettings] to be used for UI-specific settings on the map
 * @param onIndoorBuildingActive listener for indoor building state changes
 * @param onMapClick lambda invoked when the map is clicked
 * @param onMapLoaded lambda invoked when the map is finished loading
 * @param onMyLocationClick lambda invoked when the my location dot is clicked
 * @param onPOIClick lambda invoked when a POI is clicked
 * @param content the content of the map
 */
@Composable
fun BaiduMap(
    modifier: Modifier = Modifier,
    mapState: BaiduMapState = rememberBaiduMapState(),
    mapOptionsFactory: () -> BaiduMapOptions = { BaiduMapOptions() },
    properties: MapProperties = DefaultMapProperties,
    uiSettings: MapUiSettings = DefaultMapUiSettings,
    contentPadding: PaddingValues = PaddingValues(),
    onIndoorBuildingActive: (MapBaseIndoorMapInfo) -> Unit = {},
    onMapClick: (LatLng) -> Unit = {},
    onMapLongClick: (LatLng) -> Unit = {},
    onMapLoaded: () -> Unit = {},
    onMyLocationClick: (MyLocationData) -> Boolean = { false },
    onPOIClick: (MapPoi) -> Unit = {},
    content: (@Composable @BaiduMapComposable BaiduMapScope.() -> Unit)? = null,
) {
    // When in preview, early return a Box with the received modifier preserving layout
    if (LocalInspectionMode.current) {
        Box(modifier = modifier)
        return
    }

    val context = LocalContext.current
    val mapView = remember { MapView(context, mapOptionsFactory()) }
    val mapScope = remember(mapView) { BaiduMapScope(mapView) }

    AndroidView(modifier = modifier, factory = { mapView })
    MapLifecycle(mapView)


    // rememberUpdatedState and friends are used here to make these values observable to
    // the subcomposition without providing a new content function each recomposition
    val mapClickListeners = remember { MapClickListeners() }.also {
        it.onIndoorBuildingActive = onIndoorBuildingActive
        it.onMapClick = onMapClick
        it.onMapLongClick = onMapLongClick
        it.onMapLoaded = onMapLoaded
        it.onMyLocationClick = onMyLocationClick
        it.onPOIClick = onPOIClick
    }
    val currentMapState by rememberUpdatedState(mapState)
    val currentUiSettings by rememberUpdatedState(uiSettings)
    val currentMapProperties by rememberUpdatedState(properties)

    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)

    LaunchedEffect(Unit) {
        disposingComposition {
            mapView.newComposition(parentComposition) {
                MapUpdater(
                    mapState = currentMapState,
                    clickListeners = mapClickListeners,
                    mapProperties = currentMapProperties,
                    mapUiSettings = currentUiSettings,
                    contentPadding = contentPadding,
                )
                currentContent?.invoke(mapScope)
            }
        }
    }
}

private suspend inline fun disposingComposition(factory: () -> Composition) {
    val composition = factory()
    try {
        awaitCancellation()
    } finally {
        composition.dispose()
    }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun MapView.newComposition(
    parent: CompositionContext,
    noinline content: @Composable () -> Unit
): Composition {
    val map = map
    return Composition(
        MapApplier(map, this), parent
    ).apply {
        setContent(content)
    }
}

/**
 * Registers lifecycle observers to the local [MapView].
 */
@Composable
private fun MapLifecycle(mapView: MapView) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val previousState = remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    DisposableEffect(context, lifecycle, mapView) {
        val mapLifecycleObserver = mapView.lifecycleObserver(context, previousState)

        lifecycle.addObserver(mapLifecycleObserver)

        onDispose {
            lifecycle.removeObserver(mapLifecycleObserver)
            mapView.onDestroy()
            mapView.removeAllViews()
        }
    }
}

private fun MapView.lifecycleObserver(
    context: Context,
    previousState: MutableState<Lifecycle.Event>
): LifecycleEventObserver =
    LifecycleEventObserver { _, event ->
        event.targetState
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                // Skip calling mapView.onCreate if the lifecycle did not go through onDestroy - in
                // this case the AMap composable also doesn't leave the composition. So,
                // recreating the map does not restore state properly which must be avoided.
                if (previousState.value != Lifecycle.Event.ON_STOP) {
                    this.onCreate(context, Bundle.EMPTY)
                }
            }

            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> this.onResume()
            Lifecycle.Event.ON_PAUSE -> this.onPause()
            Lifecycle.Event.ON_STOP -> {}
            Lifecycle.Event.ON_DESTROY -> {}

            else -> throw IllegalStateException()
        }
        previousState.value = event
    }
