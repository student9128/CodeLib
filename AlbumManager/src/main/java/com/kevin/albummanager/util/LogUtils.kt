package com.kevin.albummanager.util

import android.util.Log

/**
 * Created by Kevin on 2020/9/6<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
object LogUtils {
    fun logI(tag: String, msg: String) {
        if (AppUtils.isDebug()) {
            Log.i(tag, msg)
        }
    }

    fun logD(tag: String, msg: String) {
        if (AppUtils.isDebug()) {
            Log.d(tag, msg)
        }
    }

    fun logW(tag: String, msg: String) {
        if (AppUtils.isDebug()) {
            Log.w(tag, msg)
        }
    }

    fun logE(tag: String, msg: String) {
        if (AppUtils.isDebug()) {
            Log.e(tag, msg)
        }
    }

    fun logV(tag: String, msg: String) {
        if (AppUtils.isDebug()) {
            Log.v(tag, msg)
        }
    }

    fun log(priority: Int, tag: String, msg: String) {
        if (AppUtils.isDebug()) {
            when (priority) {
                Log.VERBOSE -> Log.v(tag, msg)
                Log.DEBUG -> Log.d(tag, msg)
                Log.INFO -> Log.i(tag, msg)
                Log.ERROR -> Log.e(tag, msg)
                Log.WARN -> Log.w(tag, msg)
            }
        }
    }


}