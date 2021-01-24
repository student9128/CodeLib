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

}