package com.st.amap.ktx.model

import com.amap.api.maps.model.CameraPosition

/**
 * Builds a new [CameraPosition] using the provided [optionsActions]. Using this removes the need
 * to construct a [CameraPosition.Builder] object.
 *
 * @return the constructed [CameraPosition]
 */
public inline fun cameraPosition(optionsActions: CameraPosition.Builder.() -> Unit): CameraPosition =
    CameraPosition.Builder().apply(optionsActions).build()