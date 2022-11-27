package com.st.amap.ktx.model

import com.amap.api.maps.model.CircleHoleOptions

/**
 * Builds a new [CircleHoleOptions] using the provided [optionsActions].
 *
 * @return the constructed [CircleHoleOptions]
 */
public inline fun circleHoleOptions(optionsActions: CircleHoleOptions.() -> Unit): CircleHoleOptions =
    CircleHoleOptions().apply(
        optionsActions
    )