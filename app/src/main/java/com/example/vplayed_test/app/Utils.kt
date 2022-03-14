package com.example.vplayed_test.app

import android.app.Activity
import android.view.View

class Utils {
    companion object{
        fun hideSystemUI(activity: Activity) {
            activity.window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    )
        }
    }

}