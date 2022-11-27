package com.st.amap.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import com.amap.api.maps.model.TileOverlay
import com.amap.api.maps.model.TileProvider
import com.st.amap.ktx.addTileOverlay

private class TileOverlayNode(
    var tileOverlay: TileOverlay
) : MapNode {
    override fun onRemoved() {
        tileOverlay.remove()
    }
}

/**
 * A composable for a tile overlay on the map.
 *
 * @param tileProvider the tile provider to use for this tile overlay
 * @param visible the visibility of the tile overlay
 * @param zIndex the z-index of the tile overlay
 */
@Composable
@AMapComposable
fun TileOverlay(
    tileProvider: TileProvider,
    visible: Boolean = true,
    zIndex: Float = 0f,
) {
    val mapApplier = currentComposer.applier as MapApplier?
    ComposeNode<TileOverlayNode, MapApplier>(
        factory = {
            val tileOverlay = mapApplier?.map?.addTileOverlay {
                tileProvider(tileProvider)
                visible(visible)
                zIndex(zIndex)
            } ?: error("Error adding tile overlay")
            TileOverlayNode(tileOverlay)
        },
        update = {
            set(tileProvider) {
                this.tileOverlay.remove()
                this.tileOverlay = mapApplier?.map?.addTileOverlay {
                    tileProvider(it)
                    visible(visible)
                    zIndex(zIndex)
                } ?: error("Error adding tile overlay")
            }
            set(visible) { this.tileOverlay.isVisible = it }
            set(zIndex) { this.tileOverlay.zIndex = it }
        }
    )
}