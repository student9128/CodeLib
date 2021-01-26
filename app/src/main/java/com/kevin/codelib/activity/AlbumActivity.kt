package com.kevin.codelib.activity

import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.kevin.codelib.R
import com.kevin.codelib.adapter.AlbumAdapter
import com.kevin.codelib.adapter.AlbumFolderAdapter
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.bean.AlbumData
import com.kevin.codelib.bean.AlbumFolder
import com.kevin.codelib.constant.AlbumConstant
import com.kevin.codelib.interfaces.OnRecyclerItemClickListener
import com.kevin.codelib.util.AlbumUtils
import com.kevin.codelib.util.DisplayUtils
import com.kevin.codelib.widget.DividerItemDecoration
import com.kevin.codelib.widget.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_album.*
import kotlinx.android.synthetic.main.activity_photo.*
import kotlinx.android.synthetic.main.layout_album_folder_popup_window.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

/**
 * Created by Kevin on 2021/1/24<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
class AlbumActivity : BaseActivity(), OnRecyclerItemClickListener {

    private val QUERY_URI = MediaStore.Files.getContentUri("external")
    private val ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC"
    private val NOT_GIF = "!='image/gif'"
    private val PROJECTION = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.MediaColumns.DATA,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.WIDTH,
        MediaStore.MediaColumns.HEIGHT,
        MediaStore.MediaColumns.DURATION,
        MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
        MediaStore.MediaColumns.BUCKET_ID
    )
    var mAllAlbumDataList = mutableListOf<AlbumData>()
    var mOtherAlbumDataList = mutableListOf<AlbumData>()
    var mAlbumFolderList = mutableListOf<AlbumFolder>()
    var coroutineScope = CoroutineScope(Dispatchers.Main)
    var mPopupWindow: PopupWindow? = null
    var mFolderAdapter: AlbumFolderAdapter? = null
    var mAlbumDataAdapter: AlbumAdapter? = null
    var SELECTION = ""
    var SELECTION_ARGS = emptyArray<String>()
    private var popLastPosition = 0
    private var popLastOffset = 0
    private var currentSelectedAllAlbum = true//当前选择的相册
    var mMimeType = "all"
    override fun getLayoutResID(): Int {
        return R.layout.activity_album
    }

    override fun initView() {
        mMimeType = intent.getStringExtra("type")!!
        when (mMimeType) {
            "all" -> {
                SELECTION = AlbumConstant.SELECTION
                SELECTION_ARGS = AlbumConstant.SELECTION_ARGS
            }
            "gif" -> {
                SELECTION = AlbumConstant.SELECTION_IMAGE_ONLY_GIF
                SELECTION_ARGS = AlbumConstant.SELECTION_ARGS_IMAGE
            }
            "noGif" -> {
                SELECTION = AlbumConstant.SELECTION_IMAGE_NO_GIF
                SELECTION_ARGS = AlbumConstant.SELECTION_ARGS_IMAGE
            }
            "video" -> {
                SELECTION = AlbumConstant.SELECTION_IMAGE_OR_VIDEO
                SELECTION_ARGS = AlbumConstant.SELECTION_ARGS_VIDEO

            }
        }
        tvTitle.text = "全部"
        ivBack.setOnClickListener { onBackPressed() }
        llTitle.setOnClickListener {
            if (mAlbumFolderList.size < 1) {
                ToastUtils.showShort("暂无其他内容可选")
            } else {
                showSelectableWindow()
            }
        }
        rvRecyclerView.addItemDecoration(GridSpacingItemDecoration(4, 10, false))
        rvRecyclerView.layoutManager = GridLayoutManager(this, 4)
        val launch = coroutineScope.launch {
            loadAlbum()
            loadX()
            mAlbumDataAdapter = AlbumAdapter(this@AlbumActivity, mAllAlbumDataList)
            rvRecyclerView.adapter = mAlbumDataAdapter
            mAlbumDataAdapter?.setOnItemClickListener(this@AlbumActivity)

        }


    }

    private fun showSelectableWindow() {
        val view = View.inflate(this, R.layout.layout_album_folder_popup_window, null)
        val identifier = resources.getIdentifier("status_bar_height", "dimen", "android")
        val dimensionPixelSize = resources.getDimensionPixelSize(identifier)
        val dp2px = DisplayUtils.dp2px(this, 24f)
        val dp2px56 = DisplayUtils.dp2px(this, 56f)
        val screenHeight = DisplayUtils.getScreenHeight(this)
        val realScreenHeight = DisplayUtils.getRealScreenHeight(this)
        val i = screenHeight - DisplayUtils.dp2px(this, 80f)
        printD(
            "statusBarHeight=$dimensionPixelSize,dpV=$dp2px,dp2px56=$dp2px56," +
                    "screenHeight=$screenHeight,realScreenHeight=$realScreenHeight,i=$i"
        )
        val rvAlbumFolderRecyclerView = view.rvAlbumFolder
        val layoutParams = rvAlbumFolderRecyclerView.layoutParams
        val i1 = screenHeight - DisplayUtils.dp2px(this, 80f) + 1
        layoutParams.height = i1 / 3 * 2
        rvAlbumFolderRecyclerView.layoutParams = layoutParams
        mPopupWindow =
            PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, i1)
        mPopupWindow?.isOutsideTouchable = true
        mPopupWindow?.isFocusable = true
        mPopupWindow?.setBackgroundDrawable(BitmapDrawable())
        mPopupWindow?.update()
        mPopupWindow?.showAsDropDown(llTitle)

        mPopupWindow?.setOnDismissListener { }
        rvAlbumFolderRecyclerView.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDivider(R.drawable.bg_divider_recycler);
        rvAlbumFolderRecyclerView.addItemDecoration(dividerItemDecoration)
        mFolderAdapter = AlbumFolderAdapter(this, mAlbumFolderList)
        rvAlbumFolderRecyclerView.adapter = mFolderAdapter
        mFolderAdapter?.setOnFolderItemClickListener(this)
        val popLayoutManager: LinearLayoutManager =
            rvAlbumFolderRecyclerView.layoutManager as LinearLayoutManager
        if (popLastPosition != 0 && popLastOffset != 0) {
            popLayoutManager.scrollToPositionWithOffset(popLastPosition, popLastOffset)
        }
        view.clContainer.setOnClickListener { mPopupWindow?.dismiss() }
        rvAlbumFolderRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                    popLastPosition = popLayoutManager.findFirstVisibleItemPosition()
                    val findViewByPosition =
                        popLayoutManager.findViewByPosition(popLastPosition)
                    popLastOffset = findViewByPosition!!.top
                }
            }
        })

    }

    private fun getSelectionArgsForSingleMediaType(mediaType: Int): Array<String>? {
        return arrayOf(mediaType.toString())
    }

    val SELECTION_IMAGE =
        MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + " AND " + MediaStore.MediaColumns.SIZE + ">0"

    private val PROJECTION_BUCKET = arrayOf(
        MediaStore.Images.ImageColumns.BUCKET_ID,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
    )

    private fun loadImageByBucketId(mimeType: String, bucketId: String) {
        mOtherAlbumDataList.clear()

        val data = contentResolver.query(
            QUERY_URI,
            AlbumConstant.PROJECTION,
            when (mMimeType) {
                "gif" -> AlbumConstant.SELECTION_ONLY_GIF_WITH_DISPLAY_NAME
                "noGif" -> AlbumConstant.SELECTION_NO_GIF_WITH_DISPLAY_NAME
                else -> AlbumConstant.SELECTION_IMAGE_WITH_DISPLAY_NAME
            },
            AlbumConstant.selectMediaWithDisplayName(mimeType, bucketId),
            ORDER_BY
        )
        data?.let {
            var count = it.count
            printD("loadImageByDisplayName count=$count")
            if (count > 0) {
                it.moveToFirst()
                do {
                    val id = data.getLong(
                        data.getColumnIndexOrThrow(AlbumConstant.PROJECTION[0])
                    )
                    val path =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) getRealPathAndroid_Q(id) else data.getString(
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
                    mOtherAlbumDataList.add(albumData)
                } while (it.moveToNext())
            }
        }
    }

    private fun loadX() {
        mAlbumFolderList.clear()

        val data = contentResolver.query(
            QUERY_URI,
            AlbumConstant.PROJECTION_DISPLAY_NAME_Q,
            when (mMimeType) {
                "gif" -> AlbumConstant.SELECTION_DISPLAY_NAME_Q_GIF
                "noGif" -> AlbumConstant.SELECTION_DISPLAY_NAME_Q_NO_GIF
                else -> AlbumConstant.SELECTION_DISPLAY_NAME_Q
            },
            SELECTION_ARGS,
            ORDER_BY
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
                    printD("DisplayName=$displayName,bucketId=$bucketId,count=$count")
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
                    printD("id=$id,displayName=$displayName,count=$l,mimeType=$mimeType,withAppendedId=$withAppendedId")
                    var albumFolder = AlbumFolder()
                    albumFolder.bucketId = bucketId
                    albumFolder.id = id
                    albumFolder.count = l ?: 0
                    albumFolder.coverUri = withAppendedId
                    albumFolder.displayName = displayName
                    albumFolder.mimeType = mimeType
                    albumFolder.checked = false
                    mAlbumFolderList.add(albumFolder)
                } while (it.moveToNext())
                var albumFolder = AlbumFolder()
                albumFolder.bucketId = -1
                albumFolder.id = -1
                albumFolder.count = count.toLong()
                albumFolder.coverUri = allAlbumCover
                albumFolder.displayName = "全部"
                albumFolder.mimeType = ""
                albumFolder.checked = true
                mAlbumFolderList.add(0, albumFolder)
            }

//            }
        }
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
        val withAppendedId = ContentUris.withAppendedId(contentUri1, id)
        return withAppendedId
    }

    private fun loadAlbum() {
        mAllAlbumDataList.clear()
        var selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0")
        val data: Cursor? = contentResolver.query(
            QUERY_URI,
            AlbumConstant.PROJECTION,
//            AlbumConstant.SELECTION_IMAGE_ONLY_GIF,
//            AlbumConstant.SELECTION_ARGS_IMAGE,
            SELECTION,
            SELECTION_ARGS,
            ORDER_BY
        )
        data?.let {
            var count = it.count
            printD("this count= $count")
            if (count > 0) {
                it.moveToFirst()
                do {
                    val id = data.getLong(
                        data.getColumnIndexOrThrow(AlbumConstant.PROJECTION[0])
                    )
                    val path =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) getRealPathAndroid_Q(id) else data.getString(
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

    private fun getRealPathAndroid_Q(id: Long): String? {
        return QUERY_URI.buildUpon()
            .appendPath(id.toString()).build().toString()
    }

    override fun onItemClick(position: Int, view: View, type: String) {
        if (type == "albumFolder") {
            val albumFolder = mAlbumFolderList[position]
//            albumFolder.displayName=="全部"
            if (albumFolder.checked) {
                mPopupWindow?.dismiss()
            } else {
                for (i in 0 until mAlbumFolderList.size) {
                    mAlbumFolderList[i].checked = false
                }
                albumFolder.checked = true
                mAlbumFolderList[position] = albumFolder
                mFolderAdapter?.refreshData(mAlbumFolderList)
                mPopupWindow?.dismiss()
                if (albumFolder.displayName == "全部") {
                    currentSelectedAllAlbum = true
                    tvTitle.text = "全部"
                    mAlbumDataAdapter?.refreshData(mAllAlbumDataList)
                } else {
                    currentSelectedAllAlbum = false
                    tvTitle.text = albumFolder.displayName
                    val mimeType = albumFolder.mimeType
                    printD("Album mimeType=$mimeType")
                    when {
                        AlbumUtils.isImage(mimeType) -> {
                            coroutineScope.launch {
                                loadImageByBucketId(mimeType, albumFolder.bucketId.toString())
                                mAlbumDataAdapter?.refreshData(mOtherAlbumDataList)
                            }

                        }
                        AlbumUtils.isVideo(mimeType) -> {
                            coroutineScope.launch {
                                loadImageByBucketId(mimeType, albumFolder.bucketId.toString())
                                mAlbumDataAdapter?.refreshData(mOtherAlbumDataList)
                            }
                        }
                        AlbumUtils.isGif(mimeType) -> {

                        }
                    }
                }
            }

        } else if (type == "albumData") {
            var intent = Intent(this@AlbumActivity, AlbumPreviewActivity::class.java)
            intent.putExtra(
                "mimeType",
                if (currentSelectedAllAlbum) mAllAlbumDataList[position].mimeType else mOtherAlbumDataList[position].mimeType
            )
            intent.putExtra(
                "albumPath",
                if (currentSelectedAllAlbum) mAllAlbumDataList[position].path else mOtherAlbumDataList[position].path
            )
            var option = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@AlbumActivity,
                view!!, getString(R.string.share_anim)
            )
            startActivity(intent, option.toBundle())
        }
    }

}