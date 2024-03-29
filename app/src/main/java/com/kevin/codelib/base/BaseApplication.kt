package com.kevin.codelib.base

import android.app.Application
import com.kevin.codelib.util.AppUtils

/**
 * Created by Kevin on 2020/9/6<br/>
 * Blog:http://student9128.top/
 * 公众号：炽热的孤独心
 * Describe:<br/>
 */
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppUtils.initAppUtils(this)
    }
}