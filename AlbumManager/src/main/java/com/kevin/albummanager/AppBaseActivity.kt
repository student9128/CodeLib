package com.kevin.albummanager

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kevin.albummanager.util.LogUtils

/**
 * Created by Kevin on 2020/9/6<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
open class AppBaseActivity : AppCompatActivity() {
    open var TAG = javaClass.simpleName

    fun printD(msg: String) {
        LogUtils.log(Log.DEBUG, "$TAG=>", msg)
    }

    fun printW(msg: String) {
        LogUtils.log(Log.WARN, "$TAG=>", msg)
    }

    fun printE(msg: String) {
        LogUtils.log(Log.ERROR, "$TAG=>", msg)
    }

    fun printV(msg: String) {
        LogUtils.log(Log.VERBOSE, "$TAG=>", msg)
    }

    fun printI(msg: String) {
        LogUtils.log(Log.INFO, "$TAG=>", msg)
    }
}