package com.kevin.codelib.util

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Created by Kevin on 2020/9/6<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
class AppUtils {
    companion object {
        private var isDebugMode = false
        fun isDebug(): Boolean {
            return isDebugMode
        }
        fun initAppUtils(context: Context) {
            isDebugMode =
                context.applicationInfo != null && (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        }

        fun changeStatusBar(activity: Activity, color: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.statusBarColor = darkenColor(
                    ContextCompat.getColor(activity, color)
                )
            }
        }

        private fun darkenColor(color: Int): Int {
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            hsv[2] *= 0.8f
            return Color.HSVToColor(hsv)
        }
    }
}