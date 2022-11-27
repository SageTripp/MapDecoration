package com.st.amap.ktx.model

import com.amap.api.maps.model.PolygonOptions

/**
 * Builds a new [PolygonOptions] using the provided [optionsActions].
 *
 * @return the [PolygonOptions]
 */
public inline fun polygonOptions(optionsActions: PolygonOptions.() -> Unit): PolygonOptions =
    PolygonOptions().apply(
        optionsActions
    )