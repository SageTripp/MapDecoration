package com.st.amap.ktx.model

import com.amap.api.maps.model.CircleOptions

/**
 * Builds a new [CircleOptions] using the provided [optionsActions].
 *
 * @return the constructed [CircleOptions]
 */
public inline fun circleOptions(optionsActions: CircleOptions.() -> Unit): CircleOptions =
    CircleOptions().apply(
        optionsActions
    )