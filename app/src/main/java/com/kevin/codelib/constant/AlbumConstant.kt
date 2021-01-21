package com.kevin.codelib.constant

import android.provider.MediaStore

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
    /**
     * 所有的媒体文件
     */
    val SELECTION =
        MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + " AND " + MediaStore.MediaColumns.SIZE + ">0"

    /**
     * 没有GIF的图片
     */
    val SELECTION_IMAGE_NO_GIF =
        MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" +
                " AND " + MediaStore.MediaColumns.SIZE + ">0" +
                "AND" + MediaStore.MediaColumns.MIME_TYPE + "!=image/gif"

    /**
     * 只含有GIF
     */
    val SELECTION_IMAGE_ONLY_GIF = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" +
            " AND " + MediaStore.MediaColumns.SIZE + ">0" +
            "AND" + MediaStore.MediaColumns.MIME_TYPE + "=image/gif"

    /**
     * 视频长度有下限值
     */
    val SELECTION_VIDEO_WITH_LOWER_LIMIT = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
            "AND ${MediaStore.MediaColumns.SIZE}>0 " +
            "AND ${MediaStore.Video.Media.DURATION}>=?"

    /**
     * 视频长度有上限值
     */
    val SELECTION_VIDEO_WITH_UPPER_LIMIT = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
            "AND ${MediaStore.MediaColumns.SIZE}>0 " +
            "AND ${MediaStore.Video.Media.DURATION}<=?"

    /**
     * 视频长度有上下限值
     */
    val SELECTION_VIDEO_WITH_RANGE = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
            "AND ${MediaStore.MediaColumns.SIZE}>0 " +
            "AND ${MediaStore.Video.Media.DURATION}>=? " +
            "AND ${MediaStore.Video.Media.DURATION}<=?"


}