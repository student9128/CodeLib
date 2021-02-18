package com.kevin.codelib.bean

import android.net.Uri
import android.os.Parcelable
import android.util.Log
import kotlinx.android.parcel.Parcelize

/**
 * Created by Kevin on 2021/1/20<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
/**
 * 图片数据
 */
@Parcelize
data class AlbumData(
    var id: Long = 0,
    var path: String? = "",
    var width: Int = 0,
    var height: Int = 0,
    var mimeType: String = "",
    var duration: Long = 0,
    var selected: Boolean = false,
    var selectedIndex: Int = -1,
    var original:Boolean=false,
    var key:Int=-1//再相册数据集合中的位置,必须设置
) : Parcelable

/**
 * 相册文件夹
 */
data class AlbumFolder(
    var id: Long = 0,
    var mimeType: String = "",
    var displayName: String = "",
    var coverUri: Uri? = null,
    var bucketId: Long = 0,
    var count: Long = 0,
    var checked: Boolean = false
)
