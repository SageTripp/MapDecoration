package com.st.amap.ktx.model

import com.amap.api.maps.model.PolylineOptions

/**
 * Builds a new [PolylineOptions] using the provided [optionsActions].
 *
 * @return the constructed [PolylineOptions]
 */
public inline fun polylineOptions(optionsActions: PolylineOptions.() -> Unit): PolylineOptions =
    PolylineOptions().apply(
        optionsActions
    )