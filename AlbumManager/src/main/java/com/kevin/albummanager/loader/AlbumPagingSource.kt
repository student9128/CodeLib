package com.kevin.albummanager.loader

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.content.ContentResolver
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kevin.albummanager.bean.AlbumData
import com.kevin.albummanager.constant.AlbumConstant
import com.kevin.albummanager.util.AlbumUtils
import com.kevin.albummanager.util.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlbumPagingSource(
    private val context: Context,
    private val mimeType: String,
    private val bucketId: Long
) : PagingSource<Int, AlbumData>() {

    override fun getRefreshKey(state: PagingState<Int, AlbumData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AlbumData> {
        return withContext(Dispatchers.IO) {
            try {
                val position = params.key ?: 0
                val limit = params.loadSize
                val offset = position

                val selection: String
                val selectionArgs: Array<String>

                if (bucketId == -1L) {
                    selection = getGeneralSelection(mimeType)
                    selectionArgs = getGeneralSelectionArgs(mimeType)
                } else {
                    selection = getBucketSelection(mimeType)
                    selectionArgs = AlbumConstant.selectMediaWithDisplayName(mimeType, bucketId.toString())
                }

                val dataList = mutableListOf<AlbumData>()
                val cursor: Cursor? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val queryArgs = Bundle().apply {
                        putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                        putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
                        putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, AlbumConstant.ORDER_BY)
                        putInt(ContentResolver.QUERY_ARG_SQL_LIMIT, limit)
                        putInt("android.content.query_arg_sql_offset", offset)
                    }
                    context.contentResolver.query(
                        AlbumConstant.QUERY_URI,
                        AlbumConstant.PROJECTION,
                        queryArgs,
                        null
                    )
                } else {
                    context.contentResolver.query(
                        AlbumConstant.QUERY_URI,
                        AlbumConstant.PROJECTION,
                        selection,
                        selectionArgs,
                        "${AlbumConstant.ORDER_BY} LIMIT $limit OFFSET $offset"
                    )
                }
                LogUtils.logD("AlbumPagingSource", "load:cursor=${cursor==null}==limit${limit}")

                cursor?.use {
                    if (it.moveToFirst()) {
                        do {
                            val id = it.getLong(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[0]))
                            val picType = it.getString(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[2]))
                            val uriString = AlbumUtils.albumContentUri(id, picType).toString()
                            val path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                uriString
                            } else {
                                it.getString(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[1]))
                            }
                            val width = it.getInt(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[3]))
                            val height = it.getInt(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[4]))
                            var duration = 0L
                            if (AlbumUtils.isVideo(picType)) {
                                duration = it.getLong(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[5]))
                            }
                            val size = it.getLong(it.getColumnIndexOrThrow(AlbumConstant.PROJECTION[8]))

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
                LogUtils.logD("AlbumPagingSource", "load:dataList=${dataList.size}")
                LoadResult.Page(
                    data = dataList,
                    prevKey = if (position == 0) null else position - limit,
                    nextKey = if (dataList.isEmpty() || dataList.size < limit) null else position + limit
                )
            } catch (e: Exception) {
                LogUtils.logE("AlbumPagingSource", "load:e=${e.message}")
                LoadResult.Error(e)
            }
        }
    }

    private fun getGeneralSelection(mimeType: String): String {
        return when (mimeType) {
            AlbumConstant.TYPE_ALL -> AlbumConstant.SELECTION
            AlbumConstant.TYPE_IMAGE -> AlbumConstant.SELECTION
            AlbumConstant.TYPE_GIF -> AlbumConstant.SELECTION_IMAGE_ONLY_GIF
            AlbumConstant.TYPE_IMAGE_NO_GIF -> AlbumConstant.SELECTION_IMAGE_NO_GIF
            AlbumConstant.TYPE_VIDEO -> AlbumConstant.SELECTION_IMAGE_OR_VIDEO
            else -> AlbumConstant.SELECTION
        }
    }

    private fun getGeneralSelectionArgs(mimeType: String): Array<String> {
        return when (mimeType) {
            AlbumConstant.TYPE_ALL -> AlbumConstant.SELECTION_ARGS
            AlbumConstant.TYPE_IMAGE -> AlbumConstant.SELECTION_ARGS_IMAGE
            AlbumConstant.TYPE_GIF -> AlbumConstant.SELECTION_ARGS_IMAGE
            AlbumConstant.TYPE_IMAGE_NO_GIF -> AlbumConstant.SELECTION_ARGS_IMAGE
            AlbumConstant.TYPE_VIDEO -> AlbumConstant.SELECTION_ARGS_VIDEO
            else -> AlbumConstant.SELECTION_ARGS
        }
    }

    private fun getBucketSelection(mimeType: String): String {
        return when (mimeType) {
            AlbumConstant.TYPE_GIF -> AlbumConstant.SELECTION_ONLY_GIF_WITH_DISPLAY_NAME
            AlbumConstant.TYPE_IMAGE_NO_GIF -> AlbumConstant.SELECTION_NO_GIF_WITH_DISPLAY_NAME
            else -> AlbumConstant.SELECTION_IMAGE_WITH_DISPLAY_NAME
        }
    }
}
