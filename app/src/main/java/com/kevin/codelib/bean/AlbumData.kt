package com.kevin.codelib.bean

import android.net.Uri
import android.util.Log

/**
 * Created by Kevin on 2021/1/20<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
/**
 * 图片数据
 */
data class AlbumData(
    var id: Long = 0,
    var path: String? = "",
    var width: Int = 0,
    var height: Int = 0,
    var mimeType: String = "",
    var duration: Long = 0
)

/**
 * 相册文件夹
 */
data class AlbumFolder(
    var id: Long = 0,
    var mimeType: String = "",
    var displayName: String = "",
    var coverUri: Uri? = null,
    var bucketId: Long = 0,
    var count:Long=0,
    var checked:Boolean=false
)
