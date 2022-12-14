package com.st.bmap.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentComposer
import com.baidu.mapapi.map.BaiduMap
import kotlinx.coroutines.CoroutineScope

/**
 * A side-effect backed by a [LaunchedEffect] which will launch [block] and provide the underlying
 * managed [BaiduMap] object into the composition's [CoroutineContext]. This effect will be
 * re-launched when a different [key1] is provided.
 *
 * Note: This effect should be used with caution as the [BaiduMap]'s properties is managed by the
 * [_root_ide_package_.com.google.maps.android.compose.BaiduMap()] composable function. However,
 * there are use cases when obtaining a raw reference to the map is desirable for extensibility
 * (e.g. using the utility library for clustering).
 */
@Composable
@BaiduMapComposable
public fun BaiduMapScope.MapEffect(key1: Any?, block: suspend CoroutineScope.(BaiduMap) -> Unit) {
    val map = (currentComposer.applier as MapApplier).map
    LaunchedEffect(key1 = key1) {
        block(map)
    }
}

/**
 * A side-effect backed by a [LaunchedEffect] which will launch [block] and provide the underlying
 * managed [BaiduMap] object into the composition's [CoroutineContext]. This effect will be
 * re-launched when a different [key1] or [key2] is provided.
 *
 * Note: This effect should be used with caution as the [BaiduMap]'s properties is managed by the
 * [_root_ide_package_.com.google.maps.android.compose.BaiduMap()] composable function. However,
 * there are use cases when obtaining a raw reference to the map is desirable for extensibility
 * (e.g. using the utility library for clustering).
 */
@Composable
@BaiduMapComposable
public fun MapEffect(key1: Any?, key2: Any?, block: suspend CoroutineScope.(BaiduMap) -> Unit) {
    val map = (currentComposer.applier as MapApplier).map
    LaunchedEffect(key1 = key1, key2 = key2) {
        block(map)
    }
}

/**
 * A side-effect backed by a [LaunchedEffect] which will launch [block] and provide the underlying
 * managed [BaiduMap] object into the composition's [CoroutineContext]. This effect will be
 * re-launched when a different [key1], [key2], or [key3] is provided.
 *
 * Note: This effect should be used with caution as the [BaiduMap]'s properties is managed by the
 * [_root_ide_package_.com.google.maps.android.compose.BaiduMap()] composable function. However,
 * there are use cases when obtaining a raw reference to the map is desirable for extensibility
 * (e.g. using the utility library for clustering).
 */
@Composable
@BaiduMapComposable
public fun MapEffect(
    key1: Any?,
    key2: Any?,
    key3: Any?,
    block: suspend CoroutineScope.(BaiduMap) -> Unit
) {
    val map = (currentComposer.applier as MapApplier).map
    LaunchedEffect(key1 = key1, key2 = key2, key3 = key3) {
        block(map)
    }
}

/**
 * A side-effect backed by a [LaunchedEffect] which will launch [block] and provide the underlying
 * managed [BaiduMap] object into the composition's [CoroutineContext]. This effect will be
 * re-launched with any different [keys].
 *
 * Note: This effect should be used with caution as the [BaiduMap]'s properties is managed by the
 * [_root_ide_package_.com.google.maps.android.compose.BaiduMap()] composable function. However,
 * there are use cases when obtaining a raw reference to the map is desirable for extensibility
 * (e.g. using the utility library for clustering).
 */
@Composable
@BaiduMapComposable
public fun MapEffect(
    vararg keys: Any?,
    block: suspend CoroutineScope.(BaiduMap) -> Unit
) {
    val map = (currentComposer.applier as MapApplier).map
    LaunchedEffect(keys = keys) {
        block(map)
    }
}
