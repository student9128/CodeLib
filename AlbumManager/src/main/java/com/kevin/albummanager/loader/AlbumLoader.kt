package com.kevin.albummanager.loader

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.kevin.albummanager.bean.AlbumData
import com.kevin.albummanager.bean.AlbumFolder
import com.kevin.albummanager.constant.AlbumConstant
import com.kevin.albummanager.util.AlbumUtils
import com.kevin.albummanager.util.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Created by Kevin on 2021/1/22<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：零點壹度ideality
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
        val albumManagerConfig = com.kevin.albummanager.AlbumManagerConfig.albumManagerConfig
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

    fun loadAlbumDataX(): ArrayList<AlbumData> {
        var dataList: ArrayList<AlbumData> = ArrayList<AlbumData>()
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
                    var picType =
                        it.getString(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[2]))
                    val uriString = albumContentUri(id, picType).toString()
                    val path =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) uriString else data.getString(
                            data.getColumnIndexOrThrow(AlbumConstant.PROJECTION[1])
                        )
                    var width =
                        it.getInt(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[3]))
                    var height =
                        it.getInt(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[4]))
                    var duration = 0L
                    if (AlbumUtils.isVideo(picType)) {
                        duration =
                            data.getLong(data.getColumnIndexOrThrow(AlbumConstant.PROJECTION[5]))
                    }
                    val size =
                        it.getLong(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[8]))

                    dataList.add(AlbumData(
                        id = id,
                        path = path,
                        width = width,
                        height = height,
                        mimeType = picType,
                        duration = duration,
                        size = size,
                        uriString = uriString
                    ))
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
            SELECTION_ARGS,
            AlbumConstant.ORDER_BY
        )
        data?.let {
            var count = it.count
            var countMap: MutableMap<Long, Long> = HashMap()
            val bucketIdSet: MutableSet<Long> = HashSet()
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
            Log.d("AlbumLoader", "loadFolderX: count=$count")
            if (count > 0) {
                it.moveToFirst()
                val allAlbumCover = getUri(it)
                do {
                    val bucketId = data.getLong(
                        data.getColumnIndexOrThrow(AlbumConstant.PROJECTION_DISPLAY_NAME[1])
                    )
                    val displayName =
                        data.getString(data.getColumnIndexOrThrow(AlbumConstant.PROJECTION_DISPLAY_NAME[2]))
                    if (bucketIdSet.contains(bucketId)) {
                        continue
                    }
                    val id = data.getLong(
                        data.getColumnIndexOrThrow(AlbumConstant.PROJECTION_DISPLAY_NAME[0])
                    )
                    val mimeType =
                        data.getString(data.getColumnIndexOrThrow(AlbumConstant.PROJECTION_DISPLAY_NAME[3]))

                    val withAppendedId = getUri(it)
                    val l = countMap[bucketId]
                    bucketIdSet.add(bucketId)
                    dataList.add(AlbumFolder(
                        id = id,
                        mimeType = mimeType,
                        displayName = displayName,
                        bucketId = bucketId,
                        count = l ?: 0,
                        coverUriString = withAppendedId.toString(),
                        checked = false
                    ))
                } while (it.moveToNext())
                dataList.add(0, AlbumFolder(
                    id = -1,
                    displayName = "全部",
                    bucketId = -1,
                    count = count.toLong(),
                    coverUriString = allAlbumCover.toString(),
                    checked = true
                ))
            }
        }
        Log.d("AlbumLoader", "loadFolderX: $dataList")
        return dataList
    }

    fun loadImageByBucketIdX(mimeType: String, bucketId: String): ArrayList<AlbumData> {
        var dataList: ArrayList<AlbumData> = ArrayList()
        val data = mContext?.contentResolver?.query(
            AlbumConstant.QUERY_URI,
            AlbumConstant.PROJECTION,
            SELECTION_BY_BUCKET_ID,
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
                    val mimeTypeX =
                        data.getString(data.getColumnIndexOrThrow(AlbumConstant.PROJECTION[2]))
                    val uriString = albumContentUri(id, mimeTypeX).toString()
                    val path =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) uriString else data.getString(
                            data.getColumnIndexOrThrow(AlbumConstant.PROJECTION[1])
                        )
                    var width = it.getInt(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[3]))
                    var height = it.getInt(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[4]))
                    
                    var duration = 0L
                    if (AlbumUtils.isVideo(mimeTypeX)) {
                        duration =
                            data.getLong(data.getColumnIndexOrThrow(AlbumConstant.PROJECTION[5]))
                    }
                    val size =
                        it.getLong(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[8]))
                    
                    dataList.add(AlbumData(
                        id = id,
                        path = path,
                        width = width,
                        height = height,
                        mimeType = mimeTypeX,
                        duration = duration,
                        size = size,
                        uriString = uriString
                    ))
                } while (it.moveToNext())
            }
        }
        return dataList
    }

    private fun getUri(
        cursor: Cursor
    ): Uri {
        val string = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
        val id = cursor.getLong(
            cursor.getColumnIndexOrThrow(AlbumConstant.PROJECTION_DISPLAY_NAME[0])
        )
        return albumContentUri(id, string)
    }

    fun getAlbumData(): ArrayList<AlbumData> {
        return mAllAlbumDataList
    }

    fun getAlbumFolder(): List<AlbumFolder> {
        return mAlbumFolderList
    }
}
