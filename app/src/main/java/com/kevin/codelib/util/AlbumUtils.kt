package com.kevin.codelib.util

/**
 * Created by Kevin on 2021/1/22<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
object AlbumUtils {
    const val TAG = "AlbumUtils"
    fun isVideo(mimeType: String): Boolean {
        if (mimeType.isNullOrEmpty()) return false
        return mimeType.startsWith("video")
    }

    fun isImage(mimeType: String): Boolean {
        if (mimeType.isNullOrEmpty()) return false
        return mimeType.startsWith("image")
    }

    fun isGif(mimeType: String): Boolean {
        if (mimeType.isNullOrEmpty()) return false
        return mimeType == "image/gif"
    }

    /**
     * 获取的视频毫秒数转化成 mm:ss 格式下显示
     */
    fun parseTime(duration: Long): String {
        var stringBuilder = StringBuilder()
        val seconds = duration / 1000
        val min = seconds / 60
        val sec = seconds % 60
        val l = min / 10
        val l1 = min % 10
        val l2 = sec / 10
        val l3 = sec % 10
//        LogUtils.logD(TAG,"seconds=$seconds,min=$min,sec=$sec,duration=$duration")
        return "$l$l1:$l2$l3"
    }

}