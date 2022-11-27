package com.st.amap.compose

import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.geometry.Offset
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.GroundOverlay
import com.amap.api.maps.model.GroundOverlayOptions
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.LatLngBounds
import com.st.amap.compose.GroundOverlayPosition.Companion.create
import com.st.amap.ktx.addGroundOverlay

internal class GroundOverlayNode(
    val groundOverlay: GroundOverlay
) : MapNode {
    override fun onRemoved() {
        groundOverlay.remove()
    }
}

/**
 * The position of a [GroundOverlay].
 *
 * Use one of the [create] methods to construct an instance of this class.
 */
class GroundOverlayPosition private constructor(
    val latLngBounds: LatLngBounds? = null,
    val location: LatLng? = null,
    val width: Float? = null,
    val height: Float? = null,
) {
    companion object {
        /**
         * 根据矩形区域设置ground 覆盖物的位置。当设置时忽略旋转的角度，但是画此ground 覆盖物时还是会使用之前的旋转角度。
         *
         * @param latLngBounds 设置ground 覆盖物的矩形区域。
         */
        fun create(latLngBounds: LatLngBounds): GroundOverlayPosition {
            return GroundOverlayPosition(latLngBounds = latLngBounds)
        }

        /**
         * 根据位置和宽高设置ground 覆盖物。在显示时，图片高度根据图片的比例自动匹配。
         *
         * @param location  ground 覆盖物的锚点。
         * @param width  ground 覆盖物的宽，单位：米。
         * @param height  ground 覆盖物的高，单位：米。
         */
        fun create(location: LatLng, width: Float, height: Float? = null): GroundOverlayPosition {
            return GroundOverlayPosition(
                location = location,
                width = width,
                height = height
            )
        }
    }
}

/**
 * 地图上[GroundOverlay]的可组合项。
 *
 * @param position 地面附加层的位置,在显示时，图片高度根据图片的比例自动匹配。
 * @param image ground 覆盖物的图片信息。
 * @param anchor 图片的对齐方式，[0,0]是左上角，[1,1]是右下角 。如果不设置，默认为[0.5,0.5]图片的中心点。
 * @param bearing ground 覆盖物从正北顺时针的角度，相对锚点旋转。范围为[0,360)
 * @param transparency ground 覆盖物的透明度。默认为0，不透明。
 * @param visible ground 覆盖物是否可见。默认为可见 。
 * @param zIndex ground 覆盖物的z轴指数。
 */
@Composable
@AMapComposable
fun GroundOverlay(
    position: GroundOverlayPosition,
    image: BitmapDescriptor,
    anchor: Offset = Offset(0.5f, 0.5f),
    @FloatRange(from = 0.0, to = 360.0, toInclusive = false)
    bearing: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0)
    transparency: Float = 0f,
    visible: Boolean = true,
    zIndex: Float = 0f,
) {
    val mapApplier = currentComposer.applier as? MapApplier
    ComposeNode<GroundOverlayNode, MapApplier>(
        factory = {
            val groundOverlay = mapApplier?.map?.addGroundOverlay {
                anchor(anchor.x, anchor.y)
                bearing(bearing)
                image(image)
                position(position)
                transparency(transparency)
                visible(visible)
                zIndex(zIndex)
            } ?: error("Error adding ground overlay")
            GroundOverlayNode(groundOverlay)
        },
        update = {
            set(bearing) { this.groundOverlay.bearing = it }
            set(image) { this.groundOverlay.setImage(it) }
            set(position) { this.groundOverlay.position(it) }
            set(transparency) { this.groundOverlay.transparency = it }
            set(visible) { this.groundOverlay.isVisible = it }
            set(zIndex) { this.groundOverlay.zIndex = it }
        }
    )
}

private fun GroundOverlay.position(position: GroundOverlayPosition) {
    if (position.latLngBounds != null) {
        setPositionFromBounds(position.latLngBounds)
        return
    }

    if (position.location != null) {
        setPosition(position.location)
    }

    if (position.width != null && position.height == null) {
        setDimensions(position.width)
    } else if (position.width != null && position.height != null) {
        setDimensions(position.width, position.height)
    }
}

private fun GroundOverlayOptions.position(position: GroundOverlayPosition): GroundOverlayOptions {
    if (position.latLngBounds != null) {
        return positionFromBounds(position.latLngBounds)
    }

    if (position.location == null || position.width == null) {
        throw IllegalStateException("Invalid position $position")
    }

    if (position.height == null) {
        return position(position.location, position.width)
    }

    return position(position.location, position.width, position.height)
}