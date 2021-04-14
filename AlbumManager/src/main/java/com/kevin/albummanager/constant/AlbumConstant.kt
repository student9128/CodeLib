package com.kevin.albummanager.constant

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
 *
 * 及一些常用参数量
 */


/**
 *@property SINGLE,//预览选中的
 *
 *@property MULTIPLE//预览全部
 */
enum class AlbumPreviewMethod {
    SINGLE,//预览选中的
    MULTIPLE//预览全部
}
enum class AlbumTheme{
    Default,
    Red,
    Pink,
    Purple,
    DeepPurple,
    Indigo,
    LightBlue,
    Cyan,
    Teal,
    Green,
    Amber,
    Orange,
    BlueGrey
}

object AlbumConstant {
    /**
     * 跳转到预览页面
     */
    const val REQUEST_CODE_ALBUM_RESULT=10097 //获取相册图片
    const val REQUEST_CODE_ALBUM_PREVIEW_ITEM = 10099//全部预览
    const val REQUEST_CODE_ALBUM_PREVIEW_SELECTED = 10098//选中的图片预览
    const val REQUEST_CODE_ALBUM_CAMERA_SHOT = 10096//打开相机
    const val SET_RESULT_FOR_SELECTION = "setResultForSelection"
    const val PREVIEW_METHOD = "preview_method"
    const val ALBUM_FOLDER_TYPE_DEFAULT = "全部"
    var ALBUM_FOLDER_TYPE = ""

    const val TYPE_ALL = "all"
    const val TYPE_IMAGE = "image"
    const val TYPE_IMAGE_NO_GIF = "noGif"
    const val TYPE_GIF = "gif"
    const val TYPE_VIDEO = "video"


    //=================================查询相册相关========================================
    val QUERY_URI = MediaStore.Files.getContentUri("external")
    val ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC"

    private const val AND = " AND "
    private const val OR = " OR "
    private const val MEDIA_TYPE = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
    private const val VALID_SIZE = "${MediaStore.MediaColumns.SIZE}>0"
    private const val MIME_TYPE = MediaStore.MediaColumns.MIME_TYPE
    private const val MEDIA_DURATION = MediaStore.Video.Media.DURATION
    private const val BUCKET_ID = "bucket_id"
    private const val BUCKET_DISPLAY_NAME = "bucket_display_name"

    /**
     * 所有的媒体文件
     */
    const val SELECTION = MEDIA_TYPE + OR + MEDIA_TYPE + AND + VALID_SIZE

    const val SELECTION_IMAGE_OR_VIDEO = MEDIA_TYPE + AND + VALID_SIZE

    /**
     * 没有GIF的图片
     */
    const val SELECTION_IMAGE_NO_GIF =
        MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" +
                AND + MediaStore.MediaColumns.SIZE + ">0" +
                AND + MediaStore.MediaColumns.MIME_TYPE + "!='image/gif'"

    /**
     * 只含有GIF
     */
    const val SELECTION_IMAGE_ONLY_GIF = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" +
            " AND " + MediaStore.MediaColumns.SIZE + ">0" +
            " AND " + MediaStore.MediaColumns.MIME_TYPE + "='image/gif'"

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
     *
     * 微信有两个bucket_id,但是display_name同名
     */
    const val SELECTION_IMAGE_WITH_DISPLAY_NAME = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
            "AND ${MediaStore.MediaColumns.SIZE}>0 " +
//            "AND ${MediaStore.MediaColumns.BUCKET_DISPLAY_NAME}=? "+
            "AND ${MediaStore.MediaColumns.BUCKET_ID}=?"
    const val SELECTION_ONLY_GIF_WITH_DISPLAY_NAME =
        "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
                "AND ${MediaStore.MediaColumns.SIZE}>0 " +
                "AND ${MediaStore.MediaColumns.MIME_TYPE}='image/gif' " +
                "AND ${MediaStore.MediaColumns.BUCKET_ID}=?"
    const val SELECTION_NO_GIF_WITH_DISPLAY_NAME =
        "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
                "AND ${MediaStore.MediaColumns.SIZE}>0 " +
                "AND ${MediaStore.MediaColumns.MIME_TYPE}!='image/gif' " +
                "AND ${MediaStore.MediaColumns.BUCKET_ID}=?"

    const val SELECTION_DISPLAY_NAME = ("(${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
            "OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?)"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0"
            + ") GROUP BY (bucket_display_name")

    //    @RequiresApi(Build.VERSION_CODES.Q)
    const val SELECTION_DISPLAY_NAME_Q = ("(${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
            "OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?)"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0")
    const val SELECTION_DISPLAY_NAME_Q_GIF = ("(${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
            "OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?)"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0" +
            AND + "${MediaStore.MediaColumns.MIME_TYPE}='image/gif'")
    const val SELECTION_DISPLAY_NAME_Q_NO_GIF = ("(${MediaStore.Files.FileColumns.MEDIA_TYPE}=? " +
            "OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?)"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0" +
            AND + "${MediaStore.MediaColumns.MIME_TYPE}!='image/gif'")

    //==============PROJECTION=============
    val PROJECTION_DISPLAY_NAME = arrayOf(
        MediaStore.Files.FileColumns._ID,
        "bucket_id",
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

    fun selectMediaWithDisplayName(mimeType: String, displayName: String): Array<String> {
        return if (com.kevin.albummanager.util.AlbumUtils.isVideo(mimeType)) {
            arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(), displayName)
        } else {
            arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), displayName)
        }
    }

}