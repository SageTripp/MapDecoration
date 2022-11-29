package com.st.bmap.ktx.model

import com.baidu.mapapi.map.CircleHoleOptions

/**
 * Builds a new [CircleHoleOptions] using the provided [optionsActions].
 *
 * @return the constructed [CircleHoleOptions]
 */
public inline fun circleHoleOptions(optionsActions: CircleHoleOptions.() -> Unit): CircleHoleOptions =
    CircleHoleOptions().apply(
        optionsActions
    )