package com.st.amap.ktx.model

import com.amap.api.maps.model.MarkerOptions

/**
 * Builds a new [MarkerOptions] using the provided [optionsActions].
 *
 * @return the constructed [MarkerOptions]
 */
public inline fun markerOptions(optionsActions: MarkerOptions.() -> Unit): MarkerOptions =
    MarkerOptions().apply(
        optionsActions
    )