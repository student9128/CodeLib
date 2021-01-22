package com.kevin.codelib.constant

import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi

/**
 * Created by Kevin on 2021/1/20<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 *
 * 获取手机媒体文件所需要的SQL语句
 */
object AlbumConstant {
    private const val AND = " AND "
    private const val MEDIA_TYPE = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
    private const val VALID_SIZE = "${MediaStore.MediaColumns.SIZE}>0"
    private const val MIME_TYPE = MediaStore.MediaColumns.MIME_TYPE
    private const val MEDIA_DURATION = MediaStore.Video.Media.DURATION
    private const val BUCKET_ID = "bucket_id"
    private const val BUCKET_DISPLAY_NAME = "bucket_display_name"

    /**
     * 所有的媒体文件
     */
    const val SELECTION = MEDIA_TYPE + AND + VALID_SIZE

    /**
     * 没有GIF的图片
     */
    const val SELECTION_IMAGE_NO_GIF =
        MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" +
                " AND " + MediaStore.MediaColumns.SIZE + ">0" +
                "AND" + MediaStore.MediaColumns.MIME_TYPE + "!=image/gif"

    /**
     * 只含有GIF
     */
    const val SELECTION_IMAGE_ONLY_GIF = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" +
            " AND " + MediaStore.MediaColumns.SIZE + ">0" +
            "AND" + MediaStore.MediaColumns.MIME_TYPE + "=image/gif"

    /**
     * 视频长度有下限值
     */
    const val SELECTION_VIDEO_WITH_LOWER_LIMIT = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
            "AND ${MediaStore.MediaColumns.SIZE}>0 " +
            "AND ${MediaStore.Video.Media.DURATION}>=?"

    /**
     * 视频长度有上限值
     */
    const val SELECTION_VIDEO_WITH_UPPER_LIMIT = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
            "AND ${MediaStore.MediaColumns.SIZE}>0 " +
            "AND ${MediaStore.Video.Media.DURATION}<=?"

    /**
     * 视频长度有上下限值
     */
    const val SELECTION_VIDEO_WITH_RANGE = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
            "AND ${MediaStore.MediaColumns.SIZE}>0 " +
            "AND ${MediaStore.Video.Media.DURATION}>=? " +
            "AND ${MediaStore.Video.Media.DURATION}<=?"

    /**
     * 根据相册文件夹名字查询相应的图片或者视频
     */
    const val SELECTION_IMAGE_WITH_DISPLAY_NAME = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
            "AND ${MediaStore.MediaColumns.SIZE}>0 " +
            "AND ${MediaStore.MediaColumns.BUCKET_DISPLAY_NAME}=?"

    const val SELECTION_DISPLAY_NAME = ("(${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
            "OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?)"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0"
            + ") GROUP BY (bucket_display_name")

//    @RequiresApi(Build.VERSION_CODES.Q)
    const val SELECTION_DISPLAY_NAME_Q = ("(${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
            "OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?)"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0")

    //==============PROJECTION=============
    val PROJECTION_DISPLAY_NAME = arrayOf(
        MediaStore.Files.FileColumns._ID, "bucket_id",
        "bucket_display_name",
        MediaStore.MediaColumns.MIME_TYPE,
        "COUNT(*) AS count"
    )
    val PROJECTION_DISPLAY_NAME_Q = arrayOf(
        MediaStore.Files.FileColumns._ID,
        "bucket_id",
        "bucket_display_name",
        MediaStore.MediaColumns.MIME_TYPE
    )
    val SELECTION_ARGS = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
    )
    val SELECTION_ARGS_IMAGE = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
    val SELECTION_ARGS_VIDEO = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())

    //
    val PROJECTION = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.MediaColumns.DATA,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.WIDTH,
        MediaStore.MediaColumns.HEIGHT,
        MediaStore.MediaColumns.DURATION,
        MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
        MediaStore.MediaColumns.BUCKET_ID
    )

}