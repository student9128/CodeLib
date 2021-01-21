package com.kevin.codelib.activity

import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns
import android.provider.MediaStore.Images.ImageColumns
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.kevin.codelib.R
import com.kevin.codelib.adapter.AlbumAdapter
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.bean.AlbumData
import com.kevin.codelib.interfaces.ItemClickLisenter
import com.kevin.codelib.widget.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_photo.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Created by Kevin on 2021/1/12<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
class PhotoActivity : BaseActivity() {

    private val QUERY_URI = MediaStore.Files.getContentUri("external")
    private val ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC"
    private val NOT_GIF = "!='image/gif'"
    private val PROJECTION = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.MediaColumns.DATA,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.WIDTH,
        MediaStore.MediaColumns.HEIGHT,
        MediaStore.MediaColumns.DURATION
    )
    var mAlbumDataList = mutableListOf<AlbumData>()
    var coroutineScope = CoroutineScope(Dispatchers.Main)
    override fun getLayoutResID(): Int {
        return R.layout.activity_photo
    }

    override fun initView() {

        rvRecyclerView.addItemDecoration(GridSpacingItemDecoration(4, 10, false))
        rvRecyclerView.layoutManager = GridLayoutManager(this, 4)
        coroutineScope.launch {
            loadAlbum()
            loadX()
            var adapter = AlbumAdapter(this@PhotoActivity, mAlbumDataList)
            rvRecyclerView.adapter = adapter
            adapter.setOnItemClickListener(object : ItemClickLisenter {
                override fun onItemClick(position: Int, view: View?) {
                    var intent = Intent(this@PhotoActivity, AlbumPreviewActivity::class.java)
                    intent.putExtra("albumPath", mAlbumDataList[position].path)
                    var option = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@PhotoActivity,
                        view!!, getString(R.string.share_anim)
                    )
                    startActivity(intent, option.toBundle())
                }

            })
        }


    }

    private fun getSelectionArgsForSingleMediaType(mediaType: Int): Array<String>? {
        return arrayOf(mediaType.toString())
    }

    val SELECTION_IMAGE =
        MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + " AND " + MediaStore.MediaColumns.SIZE + ">0"

    private val PROJECTION_BUCKET = arrayOf(
        ImageColumns.BUCKET_ID,
        FileColumns.MEDIA_TYPE,
        ImageColumns.BUCKET_DISPLAY_NAME
    )
    private fun loadX(){
        val data = contentResolver.query(QUERY_URI, PROJECTION_BUCKET, null, null, null)
        data?.let {
            var count = it.count
            if (count > 0) {
                it.moveToFirst()
                do {
                    val id = data.getLong(
                        data.getColumnIndexOrThrow(PROJECTION_BUCKET[0])
                    )
                    val path =
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) getRealPathAndroid_Q(id) else data.getString(
                            data.getColumnIndexOrThrow(PROJECTION_BUCKET[1])
//                        )

                    val displayName=data.getColumnIndexOrThrow(PROJECTION_BUCKET[2])
                    printD("id=$id,path=$path,displayName=$displayName")

                } while (it.moveToNext())
            }
        }
    }
    private fun loadAlbum() {
        mAlbumDataList.clear()
        var selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0")
        val data: Cursor? = contentResolver.query(
            QUERY_URI,
            PROJECTION,
            selection,
            arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString()),
            ORDER_BY
        )
        data?.let {
            var count = it.count
            if (count > 0) {
                it.moveToFirst()
                do {
                    val id = data.getLong(
                        data.getColumnIndexOrThrow(PROJECTION[0])
                    )
                    val path =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) getRealPathAndroid_Q(id) else data.getString(
                            data.getColumnIndexOrThrow(PROJECTION[1])
                        )
                    var picType = it.getString(it.getColumnIndexOrThrow(PROJECTION[2]))
                    var width = it.getInt(it.getColumnIndexOrThrow(PROJECTION[3]))
                    var height = it.getInt(it.getColumnIndexOrThrow(PROJECTION[4]))
                    var albumData = AlbumData()
                    albumData.id = id
                    albumData.path = path
                    albumData.width = width
                    albumData.height = height
                    mAlbumDataList.add(albumData)
                } while (it.moveToNext())
            }
        }
    }

    private fun getRealPathAndroid_Q(id: Long): String? {
        return QUERY_URI.buildUpon()
            .appendPath(id.toString()).build().toString()
    }
}