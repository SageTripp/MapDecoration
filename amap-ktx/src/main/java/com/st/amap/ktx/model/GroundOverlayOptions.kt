package com.st.amap.ktx.model

import com.amap.api.maps.model.GroundOverlayOptions

/**
 * Builds a new [GroundOverlayOptions] using the provided [optionsActions].
 *
 * @return the constructed [GroundOverlayOptions]
 */
public inline fun groundOverlayOptions(optionsActions: GroundOverlayOptions.() -> Unit): GroundOverlayOptions =
    GroundOverlayOptions().apply(
        optionsActions
    )