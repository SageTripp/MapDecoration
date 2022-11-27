package com.st.amap.compose

import com.amap.api.maps.model.AMapPara

/**
 * 边框虚线形状
 */
enum class StrokeDottedLineType(val value: Int) {
    /**
     * 不绘制虚线
     */
    Default(AMapPara.DOTTEDLINE_TYPE_DEFAULT),

    /**
     * 圆形
     */
    Circle(AMapPara.DOTTEDLINE_TYPE_CIRCLE),

    /**
     * 方形
     */
    Square(AMapPara.DOTTEDLINE_TYPE_SQUARE),
}
