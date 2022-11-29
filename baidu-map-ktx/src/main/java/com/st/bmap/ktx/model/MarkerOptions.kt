package com.st.bmap.ktx.model

import com.baidu.mapapi.map.MarkerOptions

/**
 * Builds a new [MarkerOptions] using the provided [optionsActions].
 *
 * @return the constructed [MarkerOptions]
 */
public inline fun markerOptions(optionsActions: MarkerOptions.() -> Unit): MarkerOptions =
    MarkerOptions().apply(
        optionsActions
    )