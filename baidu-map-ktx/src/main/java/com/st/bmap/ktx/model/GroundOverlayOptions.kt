package com.st.bmap.ktx.model

import com.baidu.mapapi.map.GroundOverlayOptions

/**
 * Builds a new [GroundOverlayOptions] using the provided [optionsActions].
 *
 * @return the constructed [GroundOverlayOptions]
 */
public inline fun groundOverlayOptions(optionsActions: GroundOverlayOptions.() -> Unit): GroundOverlayOptions =
    GroundOverlayOptions().apply(
        optionsActions
    )