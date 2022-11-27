package com.st.amap.compose

import com.amap.api.maps.model.AMapPara
import com.amap.api.maps.model.PolylineOptions

enum class LineJoinType(
    val polylineType: PolylineOptions.LineJoinType,
    val polygonType: AMapPara.LineJoinType
) {
    Bevel(PolylineOptions.LineJoinType.LineJoinBevel, AMapPara.LineJoinType.LineJoinBevel),
    Miter(PolylineOptions.LineJoinType.LineJoinMiter, AMapPara.LineJoinType.LineJoinMiter),
    Round(PolylineOptions.LineJoinType.LineJoinRound, AMapPara.LineJoinType.LineJoinRound);
}

enum class LineCapType(
    val polylineType: PolylineOptions.LineCapType,
    val polygonType: AMapPara.LineCapType
) {
    Butt(PolylineOptions.LineCapType.LineCapButt, AMapPara.LineCapType.LineCapButt),
    Square(PolylineOptions.LineCapType.LineCapSquare, AMapPara.LineCapType.LineCapSquare),
    Arrow(PolylineOptions.LineCapType.LineCapArrow, AMapPara.LineCapType.LineCapArrow),
    Round(PolylineOptions.LineCapType.LineCapRound, AMapPara.LineCapType.LineCapRound);
}