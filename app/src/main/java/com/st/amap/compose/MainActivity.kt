package com.st.amap.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.amap.api.maps.MapsInitializer
import com.st.amap.compose.baidu.BMapTest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(this)
        MapsInitializer.updatePrivacyShow(this, true, true)
        MapsInitializer.updatePrivacyAgree(this, true)
        setContent {
            // A surface container using the 'background' color from the theme
            BMapTest()
        }
    }
}