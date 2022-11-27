package com.st.amap.compose

import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.amap.api.maps.AMap
import com.amap.api.maps.MapView
import com.amap.api.maps.model.Marker

/**
 * An InfoWindowAdapter that returns a [ComposeView] for drawing a marker's
 * info window.
 *
 * Note: As of version 18.0.2 of the Maps SDK, info windows are drawn by
 * creating a bitmap of the [View]s returned in the [AMap.InfoWindowAdapter]
 * interface methods. The returned views are never attached to a window,
 * instead, they are drawn to a bitmap canvas. This breaks the assumption
 * [ComposeView] makes where it must eventually be attached to a window. As a
 * workaround, the contained window is temporarily attached to the MapView so
 * that the contents of the ComposeViews are rendered.
 *
 * Eventually when info windows are no longer implemented this way, this
 * implementation should be updated.
 */
internal class ComposeInfoWindowAdapter(
    private val mapView: MapView,
    private val markerNodeFinder: (Marker) -> MarkerNode?
) : AMap.InfoWindowAdapter {

    private val infoWindowView: ComposeView
        get() = ComposeView(mapView.context).apply {
            mapView.addView(
                this,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

    private val defaultInfoWindow: @Composable (marker: Marker) -> Unit = @Composable {
        Column(modifier = Modifier.padding(8.dp)) {
            if (it.title != null) {
                BasicText(text = it.title)
            }
            if (it.snippet != null) {
                Spacer(modifier = Modifier.height(8.dp))
                BasicText(text = it.snippet)
            }
        }
    }

    override fun getInfoContents(marker: Marker): View {
        val markerNode = markerNodeFinder(marker) ?: return infoWindowView
        val content = markerNode.infoContent ?: defaultInfoWindow
        return infoWindowView.applyAndRemove(markerNode.compositionContext) {
            content(marker)
        }
    }

    override fun getInfoWindow(marker: Marker): View {
        val markerNode = markerNodeFinder(marker) ?: return infoWindowView
        val infoWindow = markerNode.infoWindow ?: defaultInfoWindow
        return infoWindowView.applyAndRemove(markerNode.compositionContext) {
            infoWindow(marker)
        }
    }

    private fun ComposeView.applyAndRemove(
        parentContext: CompositionContext,
        content: @Composable () -> Unit
    ): ComposeView {
        val result = this.apply {
            setParentCompositionContext(parentContext)
            setContent(content)
        }
        (this.parent as? MapView)?.removeView(this)
        return result
    }
}
