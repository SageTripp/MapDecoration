package com.st.amap.ktx.model

import com.amap.api.maps.model.TileOverlayOptions

/**
 * Builds a new [TileOverlayOptions] using the provided [optionsActions].
 *
 * @return the constructed [TileOverlayOptions]
 */
public inline fun tileOverlayOptions(optionsActions: TileOverlayOptions.() -> Unit): TileOverlayOptions =
    TileOverlayOptions().apply(
        optionsActions
    )