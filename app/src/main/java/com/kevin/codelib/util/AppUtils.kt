package com.kevin.codelib.util

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo

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
    }
}