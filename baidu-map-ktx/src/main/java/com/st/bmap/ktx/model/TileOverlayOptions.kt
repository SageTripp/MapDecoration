package com.st.bmap.ktx.model

import com.baidu.mapapi.map.TileOverlayOptions

/**
 * Builds a new [TileOverlayOptions] using the provided [optionsActions].
 *
 * @return the constructed [TileOverlayOptions]
 */
public inline fun tileOverlayOptions(optionsActions: TileOverlayOptions.() -> Unit): TileOverlayOptions =
    TileOverlayOptions().apply(
        optionsActions
    )