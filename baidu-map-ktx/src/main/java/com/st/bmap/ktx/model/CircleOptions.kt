package com.st.bmap.ktx.model

import com.baidu.mapapi.map.CircleOptions

/**
 * Builds a new [CircleOptions] using the provided [optionsActions].
 *
 * @return the constructed [CircleOptions]
 */
public inline fun circleOptions(optionsActions: CircleOptions.() -> Unit): CircleOptions =
    CircleOptions().apply(
        optionsActions
    )