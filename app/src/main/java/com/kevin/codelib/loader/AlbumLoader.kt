package com.kevin.codelib.loader

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import com.kevin.codelib.bean.AlbumData
import com.kevin.codelib.bean.AlbumFolder
import com.kevin.codelib.constant.AlbumConstant
import com.kevin.codelib.util.AlbumUtils

/**
 * Created by Kevin on 2021/1/22<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
class AlbumLoader private constructor(val mContext: Context) {
    var mAllAlbumDataList: ArrayList<AlbumData> = ArrayList<AlbumData>()
    var mOtherAlbumDataList: ArrayList<AlbumData> = ArrayList<AlbumData>()
    var mSelectedAlbumDataList: ArrayList<AlbumData> = ArrayList<AlbumData>()
    var mAlbumFolderList = mutableListOf<AlbumFolder>()
    var SELECTION = ""
    var SELECTION_ARGS = emptyArray<String>()

    val albumLoaderInstance: AlbumLoader by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        AlbumLoader(mContext)
    }

    private fun loadAlbum() {
        mAllAlbumDataList.clear()
        var selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0")
        val data: Cursor? = mContext.contentResolver.query(
            AlbumConstant.QUERY_URI,
            AlbumConstant.PROJECTION,
//            AlbumConstant.SELECTION_IMAGE_ONLY_GIF,
//            AlbumConstant.SELECTION_ARGS_IMAGE,
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) getRealPathAndroidQ(id) else data.getString(
                            data.getColumnIndexOrThrow(AlbumConstant.PROJECTION[1])
                        )
                    var picType =
                        it.getString(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[2]))
                    var width = it.getInt(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[3]))
                    var height = it.getInt(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[4]))
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
                    mAllAlbumDataList.add(albumData)
                } while (it.moveToNext())
            }
        }
    }

    private fun getRealPathAndroidQ(id: Long): String? {
        return AlbumConstant.QUERY_URI.buildUpon()
            .appendPath(id.toString()).build().toString()
    }
}