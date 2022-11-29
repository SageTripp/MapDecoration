package com.st.bmap.ktx.model

import com.baidu.mapapi.map.PolylineOptions

/**
 * Builds a new [PolylineOptions] using the provided [optionsActions].
 *
 * @return the constructed [PolylineOptions]
 */
public inline fun polylineOptions(optionsActions: PolylineOptions.() -> Unit): PolylineOptions =
    PolylineOptions().apply(
        optionsActions
    )