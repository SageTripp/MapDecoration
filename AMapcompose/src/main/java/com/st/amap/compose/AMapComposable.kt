package com.st.amap.compose

import androidx.compose.runtime.ComposableTargetMarker

/**
 * An annotation that can be used to mark a composable function as being expected to be use in a
 * composable function that is also marked or inferred to be marked as a [AMapComposable].
 *
 * This will produce build warnings when [AMapComposable] composable functions are used outside
 * of a [AMapComposable] content lambda, and vice versa.
 */
@Retention(AnnotationRetention.BINARY)
@ComposableTargetMarker(description = "AMap Composable")
@Target(
    AnnotationTarget.FILE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER,
)
public annotation class AMapComposable