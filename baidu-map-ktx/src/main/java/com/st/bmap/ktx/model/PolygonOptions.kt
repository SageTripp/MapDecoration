package com.st.bmap.ktx.model

import com.baidu.mapapi.map.PolygonOptions

/**
 * Builds a new [PolygonOptions] using the provided [optionsActions].
 *
 * @return the [PolygonOptions]
 */
public inline fun polygonOptions(optionsActions: PolygonOptions.() -> Unit): PolygonOptions =
    PolygonOptions().apply(
        optionsActions
    )