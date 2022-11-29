package com.st.amap.compose

import android.app.Application
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer

class MapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SDKInitializer.setAgreePrivacy(this, true)
        SDKInitializer.initialize(this)
        SDKInitializer.setCoordType(CoordType.GCJ02)
    }
}