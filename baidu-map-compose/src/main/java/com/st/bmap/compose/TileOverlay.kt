package com.st.bmap.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import com.baidu.mapapi.map.TileOverlay
import com.baidu.mapapi.map.TileProvider
import com.baidu.mapapi.model.LatLngBounds
import com.st.bmap.ktx.addTileOverlay

private class TileOverlayNode(
    var tileOverlay: TileOverlay
) : MapNode {
    override fun onRemoved() {
        tileOverlay.removeTileOverlay()
    }
}

/**
 * A composable for a tile overlay on the map.
 *
 * @param tileProvider 瓦片图层的Provider
 * @param positionFromBounds TileOverlay的显示区域，瓦片图会以多个瓦片图连接并覆盖该区域 默认值为世界范围显示瓦片图
 * @param maxTileTmp 在线瓦片图的内存缓存大小,默认值为 200MB
 */
@Composable
@BaiduMapComposable
fun TileOverlay(
    tileProvider: TileProvider,
    positionFromBounds: LatLngBounds,
    maxTileTmp: Int = 209715200,
) {
    val mapApplier = currentComposer.applier as MapApplier?
    ComposeNode<TileOverlayNode, MapApplier>(
        factory = {
            val tileOverlay = mapApplier?.map?.addTileOverlay {
                tileProvider(tileProvider)
                setPositionFromBounds(positionFromBounds)
                setMaxTileTmp(maxTileTmp)
            } ?: error("Error adding tile overlay")
            TileOverlayNode(tileOverlay)
        },
        update = {
            set(tileProvider) {
                this.tileOverlay.removeTileOverlay()
                this.tileOverlay = mapApplier?.map?.addTileOverlay {
                    tileProvider(it)
                    setPositionFromBounds(positionFromBounds)
                    setMaxTileTmp(maxTileTmp)
                } ?: error("Error adding tile overlay")
            }
        }
    )
}