package com.kevin.codelib.bean

import android.graphics.drawable.Drawable

data class AppInfo(
    var icon: Drawable?,
    var name: String = "",
    var packageName: String = "",
    var versionName: String = "",
    var versionCode:Long=0,
    var firstInstallTime: Long = 0,
    var lastUpdateTime: Long = 0,
    var isSystemApp:Boolean=false
)
