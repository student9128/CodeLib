package com.kevin.codelib.loader

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.kevin.codelib.AlbumManagerConfig
import com.kevin.codelib.bean.AlbumData
import com.kevin.codelib.bean.AlbumFolder
import com.kevin.codelib.constant.AlbumConstant
import com.kevin.codelib.util.AlbumUtils
import com.kevin.codelib.util.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Created by Kevin on 2021/1/22<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
class AlbumLoader {
    var mAllAlbumDataList: ArrayList<AlbumData> = ArrayList<AlbumData>()
    var mOtherAlbumDataList: ArrayList<AlbumData> = ArrayList<AlbumData>()
    var mSelectedAlbumDataList: ArrayList<AlbumData> = ArrayList<AlbumData>()
    var mAlbumFolderList = mutableListOf<AlbumFolder>()
    var SELECTION = ""
    var SELECTION_FOR_FOLDER = ""//查找文件夹
    var SELECTION_BY_BUCKET_ID = ""//根据bucket_id查找对应文件夹的图片或者视频
    var SELECTION_ARGS = emptyArray<String>()
    var mMimeType = AlbumConstant.TYPE_ALL
    var coroutineScope = CoroutineScope(Dispatchers.Main)
    var mContext: Context? = null

    companion object {
        val albumLoaderInstance: AlbumLoader by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AlbumLoader()
        }
    }

    fun setParams(context: Context) {
        mContext = context
        val albumManagerConfig = AlbumManagerConfig.albumManagerConfig
        mMimeType = albumManagerConfig.mimeType
        when (mMimeType) {
            AlbumConstant.TYPE_ALL -> {
                SELECTION = AlbumConstant.SELECTION
                SELECTION_ARGS = AlbumConstant.SELECTION_ARGS
                SELECTION_FOR_FOLDER = AlbumConstant.SELECTION_DISPLAY_NAME_Q
                SELECTION_BY_BUCKET_ID = AlbumConstant.SELECTION_IMAGE_WITH_DISPLAY_NAME
            }
            AlbumConstant.TYPE_IMAGE -> {
                SELECTION = AlbumConstant.SELECTION
                SELECTION_ARGS = AlbumConstant.SELECTION_ARGS_IMAGE
                SELECTION_FOR_FOLDER = AlbumConstant.SELECTION_DISPLAY_NAME_Q
                SELECTION_BY_BUCKET_ID = AlbumConstant.SELECTION_IMAGE_WITH_DISPLAY_NAME
            }
            AlbumConstant.TYPE_GIF -> {
                SELECTION = AlbumConstant.SELECTION_IMAGE_ONLY_GIF
                SELECTION_ARGS = AlbumConstant.SELECTION_ARGS_IMAGE
                SELECTION_FOR_FOLDER = AlbumConstant.SELECTION_DISPLAY_NAME_Q_GIF
                SELECTION_BY_BUCKET_ID = AlbumConstant.SELECTION_ONLY_GIF_WITH_DISPLAY_NAME
            }
            AlbumConstant.TYPE_IMAGE_NO_GIF -> {
                SELECTION = AlbumConstant.SELECTION_IMAGE_NO_GIF
                SELECTION_ARGS = AlbumConstant.SELECTION_ARGS_IMAGE
                SELECTION_FOR_FOLDER = AlbumConstant.SELECTION_DISPLAY_NAME_Q_NO_GIF
                SELECTION_BY_BUCKET_ID = AlbumConstant.SELECTION_NO_GIF_WITH_DISPLAY_NAME
            }
            AlbumConstant.TYPE_VIDEO -> {
                SELECTION = AlbumConstant.SELECTION_IMAGE_OR_VIDEO
                SELECTION_ARGS = AlbumConstant.SELECTION_ARGS_VIDEO
                SELECTION_FOR_FOLDER = AlbumConstant.SELECTION_DISPLAY_NAME_Q
                SELECTION_BY_BUCKET_ID = AlbumConstant.SELECTION_IMAGE_WITH_DISPLAY_NAME
            }
        }
    }

    fun loadAlbumData() {
        val launch = coroutineScope.launch {
            val x = async {
                loadAlbumDataX()
            }
            val await = x.await()
//            LogUtils.logD("AlbumLoader", "${await}")
        }
    }

    fun loadAlbumFolder() {
        val launch = coroutineScope.launch(Dispatchers.IO) {
            val f = async { loadFolderX() }
            val await = f.await()
//            LogUtils.logD("AlbumLoader", "${await}")
        }
    }

    fun loadAlbumDataX(): ArrayList<AlbumData> {
        var dataList: ArrayList<AlbumData> = ArrayList<AlbumData>()
        //                mAllAlbumDataList.clear()
        val data: Cursor? = mContext!!.contentResolver.query(
            AlbumConstant.QUERY_URI,
            AlbumConstant.PROJECTION,
            SELECTION,
            SELECTION_ARGS,
            AlbumConstant.ORDER_BY
        )
        data?.let {
            var count = it.count
            if (count > 0) {
                it.moveToFirst()
                do {
                    val id = data.getLong(
                        data.getColumnIndexOrThrow(AlbumConstant.PROJECTION[0])
                    )
                    val path =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) getRealPathAndroidQ(
                            id
                        ) else data.getString(
                            data.getColumnIndexOrThrow(AlbumConstant.PROJECTION[1])
                        )
                    var picType =
                        it.getString(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[2]))
                    var width =
                        it.getInt(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[3]))
                    var height =
                        it.getInt(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[4]))
                    var duration = 0L
                    if (AlbumUtils.isVideo(picType)) {
                        duration =
                            data.getLong(data.getColumnIndexOrThrow(AlbumConstant.PROJECTION[5]))
                    }
                    val displayName =
                        it.getString(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[6]))
                    //                    printD("path=$duration")
                    AlbumUtils.parseTime(duration)
                    var albumData = AlbumData()
                    albumData.id = id
                    albumData.path = path
                    albumData.width = width
                    albumData.height = height
                    albumData.mimeType = picType
                    albumData.duration = duration
                    //                            mAllAlbumDataList.add(albumData)
                    dataList.add(albumData)
                } while (it.moveToNext())
            }
        }
        return dataList
    }

    fun loadFolderX(): ArrayList<AlbumFolder> {
        var dataList: ArrayList<AlbumFolder> = ArrayList()
        val data = mContext?.contentResolver?.query(
            AlbumConstant.QUERY_URI,
            AlbumConstant.PROJECTION_DISPLAY_NAME_Q,
            SELECTION_FOR_FOLDER,
//            when (mMimeType) {
//                "gif" -> AlbumConstant.SELECTION_DISPLAY_NAME_Q_GIF
//                "noGif" -> AlbumConstant.SELECTION_DISPLAY_NAME_Q_NO_GIF
//                else -> AlbumConstant.SELECTION_DISPLAY_NAME_Q
//            },
            SELECTION_ARGS,
            AlbumConstant.ORDER_BY
        )
//        if (AppUtils.beforeAndroidQ()) {
//            data?.let {
//                var count = it.count
//                printD("count1=$count")
//
//                if (count > 0) {
//                    it.moveToFirst()
//                    do {
//                        val id = data.getLong(
//                            data.getColumnIndexOrThrow(AlbumConstant.PROJECTION_DISPLAY_NAME[0])
//                        )
////                    val path =
//////                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) getRealPathAndroid_Q(id) else data.getString(
////                            data.getColumnIndexOrThrow(AlbumConstant.PROJECTION_DISPLAY_NAME[2])
//////                        )
//
//                        val displayName =
//                            data.getString(data.getColumnIndexOrThrow(AlbumConstant.PROJECTION_DISPLAY_NAME[2]))
//                        var countX = 0
//                        if (AppUtils.beforeAndroidQ()) {
//                            countX = data.getInt(data.getColumnIndexOrThrow("count"))
//                        }
//                        val mimeType =
//                            data.getString(data.getColumnIndexOrThrow(AlbumConstant.PROJECTION_DISPLAY_NAME[3]))
//                        val long = it.getLong(it.getColumnIndex(MediaStore.Files.FileColumns._ID))
//                        val string =
//                            it.getString(it.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
//                        if (string.startsWith("image")) {
//                            var contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                            val withAppendedId = ContentUris.withAppendedId(contentUri, id)
////                        printD("long=$long,string=$string,withAppendedId=$withAppendedId")
//                        } else if (string.startsWith("video")) {
//                            var contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//
//                        } else {
//                            var contentUri = MediaStore.Files.getContentUri("external")
//
//                        }
//
//                        printD("id=$id,displayName=$displayName,count=$countX,mimeType=$mimeType")
//
//
//                    } while (it.moveToNext())
//                }
//            }
//        } else {
        data?.let {
            var count = it.count
            var contentUri: Uri? = null
            var countMap: MutableMap<Long, Long> = HashMap()
            val bucketIdSet: MutableSet<Long> = HashSet()
            var countW = 0
            while (it.moveToNext()) {
                val bucketId = data.getLong(
                    data.getColumnIndexOrThrow(AlbumConstant.PROJECTION_DISPLAY_NAME[1])
                )
                var countL: Long? = countMap[bucketId]
                if (countL == null) {
                    countL = 1L
                } else {
                    countL++
                }
                countMap[bucketId] = countL
            }
            if (count > 0) {
                it.moveToFirst()
                val allAlbumCover = getUri(it)
                do {
                    val bucketId = data.getLong(
                        data.getColumnIndexOrThrow(AlbumConstant.PROJECTION_DISPLAY_NAME[1])
                    )
                    val displayName =
                        data.getString(data.getColumnIndexOrThrow(AlbumConstant.PROJECTION_DISPLAY_NAME[2]))
//                    printD("DisplayName=$displayName,bucketId=$bucketId,count=$count")
                    if (bucketIdSet.contains(bucketId)) {
                        continue
                    }
                    val id = data.getLong(
                        data.getColumnIndexOrThrow(AlbumConstant.PROJECTION_DISPLAY_NAME[0])
                    )

                    var countX = 0
//                        if (AppUtils.beforeAndroidQ()) {
//                            countX = data.getInt(data.getColumnIndexOrThrow("count"))
//                        }
                    val mimeType =
                        data.getString(data.getColumnIndexOrThrow(AlbumConstant.PROJECTION_DISPLAY_NAME[3]))
                    val long = it.getLong(it.getColumnIndex(MediaStore.Files.FileColumns._ID))
                    val string =
                        it.getString(it.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                    val withAppendedId = getUri(it)
                    val l = countMap[bucketId]
                    bucketIdSet.add(bucketId)
//                    printD("id=$id,displayName=$displayName,count=$l,mimeType=$mimeType,withAppendedId=$withAppendedId")
                    var albumFolder = AlbumFolder()
                    albumFolder.bucketId = bucketId
                    albumFolder.id = id
                    albumFolder.count = l ?: 0
                    albumFolder.coverUri = withAppendedId
                    albumFolder.displayName = displayName
                    albumFolder.mimeType = mimeType
                    albumFolder.checked = false
                    dataList.add(albumFolder)
                } while (it.moveToNext())
                var albumFolder = AlbumFolder()
                albumFolder.bucketId = -1
                albumFolder.id = -1
                albumFolder.count = count.toLong()
                albumFolder.coverUri = allAlbumCover
                albumFolder.displayName = "全部"
                albumFolder.mimeType = ""
                albumFolder.checked = true
                dataList.add(0, albumFolder)
            }

//            }
        }
        return dataList
    }

    fun loadImageByBucketIdX(mimeType: String, bucketId: String):ArrayList<AlbumData> {
        var dataList: ArrayList<AlbumData> = ArrayList()
        val data = mContext?.contentResolver?.query(
            AlbumConstant.QUERY_URI,
            AlbumConstant.PROJECTION,
            SELECTION_BY_BUCKET_ID,
//            when (mMimeType) {
//                "gif" -> AlbumConstant.SELECTION_ONLY_GIF_WITH_DISPLAY_NAME
//                "noGif" -> AlbumConstant.SELECTION_NO_GIF_WITH_DISPLAY_NAME
//                else -> AlbumConstant.SELECTION_IMAGE_WITH_DISPLAY_NAME
//            },
            AlbumConstant.selectMediaWithDisplayName(mimeType, bucketId),
            AlbumConstant.ORDER_BY
        )
        data?.let {
            var count = it.count
            if (count > 0) {
                it.moveToFirst()
                do {
                    val id = data.getLong(
                        data.getColumnIndexOrThrow(AlbumConstant.PROJECTION[0])
                    )
                    val path =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) getRealPathAndroidQ(id) else data.getString(
                            data.getColumnIndexOrThrow(AlbumConstant.PROJECTION[1])
                        )

                    val displayName =
                        data.getString(data.getColumnIndexOrThrow(AlbumConstant.PROJECTION[6]))
                    var width = it.getInt(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[3]))
                    var height = it.getInt(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[4]))
                    val mimeType =
                        data.getString(data.getColumnIndexOrThrow(AlbumConstant.PROJECTION[2]))
//                    printD("id=$id,displayName=$displayName,count=$countX,mimeType=$mimeType")
                    var albumData = AlbumData()
                    albumData.id = id
                    albumData.path = path
                    albumData.width = width
                    albumData.height = height
                    albumData.mimeType = mimeType
                    dataList.add(albumData)
                } while (it.moveToNext())
            }
        }
        return dataList
    }

    private fun getUri(
        cursor: Cursor
    ): Uri {
        val string = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
        val id = cursor.getLong(
            cursor.getColumnIndexOrThrow(AlbumConstant.PROJECTION_DISPLAY_NAME[0])
        )
        var contentUri1: Uri? = null
        if (string.startsWith("image")) {
            contentUri1 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            //                        printD("long=$long,string=$string,withAppendedId=$withAppendedId")
        } else if (string.startsWith("video")) {
            contentUri1 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        } else {
            contentUri1 = MediaStore.Files.getContentUri("external")

        }
        return ContentUris.withAppendedId(contentUri1, id)
    }

    private fun getRealPathAndroidQ(id: Long): String? {
        return AlbumConstant.QUERY_URI.buildUpon()
            .appendPath(id.toString()).build().toString()
    }

    fun getAlbumData(): ArrayList<AlbumData> {
        return mAllAlbumDataList
    }

    fun getAlbumFolder(): List<AlbumFolder> {
        return mAlbumFolderList
    }
}