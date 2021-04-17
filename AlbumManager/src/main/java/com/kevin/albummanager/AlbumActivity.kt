package com.kevin.albummanager

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.kevin.albummanager.adapter.AlbumAdapter
import com.kevin.albummanager.adapter.AlbumFolderAdapter
import com.kevin.albummanager.bean.AlbumData
import com.kevin.albummanager.bean.AlbumFolder
import com.kevin.albummanager.constant.AlbumConstant
import com.kevin.albummanager.constant.AlbumPreviewMethod
import com.kevin.albummanager.loader.AlbumLoader
import com.kevin.albummanager.util.AlbumUtils
import com.kevin.albummanager.util.AppUtils
import com.kevin.albummanager.util.DisplayUtils
import com.kevin.albummanager.widget.DividerItemDecoration
import com.kevin.albummanager.widget.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_album.*
import kotlinx.android.synthetic.main.layout_album_folder_popup_window.view.*
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Kevin on 2021/1/24<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
class AlbumActivity : AlbumBaseActivity(), OnRecyclerItemClickListener, View.OnClickListener {

    var mAllAlbumDataList: ArrayList<AlbumData> = ArrayList<AlbumData>()
    var mOtherAlbumDataList: ArrayList<AlbumData> = ArrayList<AlbumData>()
    var mSelectedAlbumDataList: ArrayList<AlbumData> = ArrayList<AlbumData>()
    var mAlbumFolderList = mutableListOf<AlbumFolder>()
    var coroutineScope = CoroutineScope(Dispatchers.Main)
    var mPopupWindow: PopupWindow? = null
    var mFolderAdapter: AlbumFolderAdapter? = null
    var mAlbumDataAdapter: AlbumAdapter? = null
    private var popLastPosition = 0
    private var popLastOffset = 0
    private var currentSelectedAllAlbum = true//当前选择的相册
    var mSelectList: MutableList<MutableMap<Int, AlbumData>> = mutableListOf()
    private val albumLoaderInstance = AlbumLoader.albumLoaderInstance
    private val albumManagerCollectionInstance =
        AlbumManagerCollection.albumManagerCollectionInstance

    var loadAlbumJob: Job? = null
    var refreshAlbumJob: Job? = null
    var refreshFolderJob: Job? = null
    val attrArray = intArrayOf(android.R.attr.colorAccent)
    private var fileName = ""
    private var mFilePath = ""

    override fun getLayoutResID(): Int {
        return R.layout.activity_album
    }

    override fun initView() {
        ll_expand_container.background = AlbumUtils.expandBackground(albumManagerConfig.theme, this)
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
        rvRecyclerView.setHasFixedSize(true)

        albumLoaderInstance.setParams(this)

        loadAlbumJob = coroutineScope.launch {
            mAllAlbumDataList =
                async(Dispatchers.IO) { albumLoaderInstance.loadAlbumDataX() }.await()
            mAlbumFolderList = async(Dispatchers.IO) { albumLoaderInstance.loadFolderX() }.await()
            if (albumManagerConfig.camera) {
                var ad = AlbumData()
                ad.showCameraPlaceholder = true
                mAllAlbumDataList.add(0, ad)
            }
            mAlbumDataAdapter = AlbumAdapter(this@AlbumActivity, mAllAlbumDataList)
            rvRecyclerView.adapter = mAlbumDataAdapter
            mAlbumDataAdapter?.setOnItemClickListener(this@AlbumActivity)
            if (mAlbumFolderList.size > 0) {
                showAlbumData()
            } else {
                showEmpty()
            }

        }
        rlMenu.setOnClickListener { }
        tv_preview.setOnClickListener {
            showPreview(
                AlbumPreviewMethod.SINGLE,
                0,
                AlbumConstant.REQUEST_CODE_ALBUM_PREVIEW_SELECTED
            )
        }
        tv_origin.setOnClickListener { }
        tv_send.setOnClickListener(this)
//        window.navigationBarColor = AppUtils.addAlphaForColor(0.99f,ContextCompat.getColor(this,R.color.colorPrimary))
        blurLayout.viewBehind = rvRecyclerView
    }

    private fun showPreview(
        previewMethod: AlbumPreviewMethod,
        currentPosition: Int,
        requestCode: Int
    ) {
        if (previewMethod == AlbumPreviewMethod.SINGLE) {
            albumManagerCollectionInstance.saveCurrentAlbumData(mSelectedAlbumDataList)
            val albumFolderType = albumManagerCollectionInstance.getAlbumFolderType()
            if (albumFolderType == AlbumConstant.ALBUM_FOLDER_TYPE_DEFAULT) {
                albumManagerCollectionInstance.saveAllAlbumData(mAllAlbumDataList)
            } else {
                albumManagerCollectionInstance.saveAllAlbumData(mOtherAlbumDataList)
            }
        } else {
            val albumFolderType = albumManagerCollectionInstance.getAlbumFolderType()
            if (albumFolderType == AlbumConstant.ALBUM_FOLDER_TYPE_DEFAULT) {
                albumManagerCollectionInstance.saveCurrentAlbumData(mAllAlbumDataList)
            } else {
                albumManagerCollectionInstance.saveCurrentAlbumData(mOtherAlbumDataList)
            }
        }
        var intent = Intent(this, AlbumPreviewActivity::class.java)
        intent.putExtra(AlbumConstant.PREVIEW_METHOD, previewMethod.name)
        intent.putExtra("position", currentPosition)
        val customAnimation = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.photo_fade_in,
            R.anim.photo_fade_out_nothing
        )
        startActivityForResult( intent, requestCode, customAnimation.toBundle())
//        ActivityCompat.startActivityForResult(this, intent, requestCode, customAnimation.toBundle())
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
//        printD(
//            "statusBarHeight=$dimensionPixelSize,dpV=$dp2px,dp2px56=$dp2px56," +
//                    "screenHeight=$screenHeight,realScreenHeight=$realScreenHeight,i=$i"
//        )
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


    override fun onItemClick(position: Int, view: View, type: String) {
        if (type == "albumFolder") {
            val albumFolder = mAlbumFolderList[position]
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

                albumManagerCollectionInstance.saveAlbumFolderType(albumFolder.displayName)

                if (albumFolder.displayName == AlbumConstant.ALBUM_FOLDER_TYPE_DEFAULT) {
                    currentSelectedAllAlbum = true
                    tvTitle.text = AlbumConstant.ALBUM_FOLDER_TYPE_DEFAULT
                    if (mAllAlbumDataList.size > 0) {
                        showAlbumData()
                        mAlbumDataAdapter?.refreshData(mAllAlbumDataList)
                    } else {
                        showEmpty()
                    }
                } else {
                    currentSelectedAllAlbum = false
                    tvTitle.text = albumFolder.displayName
                    val mimeType = albumFolder.mimeType
//                    printD("mimeType=$mimeType,displayName=${albumFolder.displayName},bucketId=${albumFolder.bucketId}")
                    coroutineScope.launch {
                        mOtherAlbumDataList = async(Dispatchers.IO) {
                            albumLoaderInstance.loadImageByBucketIdX(
                                mimeType,
                                albumFolder.bucketId.toString()
                            )
                        }.await()
                        mAlbumDataAdapter?.refreshData(mOtherAlbumDataList)
                    }
                }
            }

        } else if (type == "albumData") {
            showPreview(
                AlbumPreviewMethod.MULTIPLE,
                position, AlbumConstant.REQUEST_CODE_ALBUM_PREVIEW_ITEM
            )
        } else if (type == "camera") {
            if (albumManagerConfig.mimeType == AlbumConstant.TYPE_VIDEO) {
                captureIV(AlbumConstant.TYPE_VIDEO)
            } else {
                captureIV(AlbumConstant.TYPE_IMAGE)
            }
        }
    }

    private fun captureIV(type: String) {
        val fileDir = File(Environment.getExternalStorageDirectory(), "${AppUtils.getAppName()}")
//        printD("fileDir=${fileDir.absolutePath},name=${fileDir.name},exists=${fileDir.exists()}")
        if (!fileDir.exists()) {
            fileDir.mkdir()
        }
        if (type == AlbumConstant.TYPE_VIDEO) {
            fileName = "VIDEO_" + System.currentTimeMillis() + ".mp4"
            mFilePath = fileDir.absolutePath + "/" + fileName
            var uri: Uri? = null
            val contentValues = ContentValues()
            //设置文件名
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(
                    MediaStore.Video.Media.RELATIVE_PATH,
                    "DCIM/${AppUtils.getAppName()}"
                )
            } else {
                contentValues.put(MediaStore.Video.Media.DATA, mFilePath)
            }
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE) // 启动系统相机
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, albumManagerConfig.shotVideoDuration)
            intent.putExtra(
                MediaStore.EXTRA_SIZE_LIMIT,
                (1024 * 1024 * albumManagerConfig.shotVideoSize).toLong()
            )
            intent.putExtra(
                MediaStore.EXTRA_VIDEO_QUALITY,
                if (albumManagerConfig.shotVideoQuality == AlbumManagerConfig.VideoQuality.LOW) 0 else 1
            )
            startActivityForResult(intent, AlbumConstant.REQUEST_CODE_ALBUM_CAMERA_SHOT)
        } else {
            fileName = "IMG_" + System.currentTimeMillis() + ".jpg"
            mFilePath = fileDir.absolutePath + "/" + fileName
            var uri: Uri? = null
            val contentValues = ContentValues()
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "DCIM/${AppUtils.getAppName()}"
                )
            } else {
                contentValues.put(MediaStore.Images.Media.DATA, mFilePath)
            }
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            uri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 启动系统相机
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(intent, AlbumConstant.REQUEST_CODE_ALBUM_CAMERA_SHOT)
        }
    }


    private fun showEmpty() {
        if (llEmpty.visibility != View.VISIBLE) {
            clContainer.visibility = View.GONE
            llEmpty.visibility = View.VISIBLE
        }
    }

    private fun showAlbumData() {
        if (clContainer.visibility != View.VISIBLE) {
            clContainer.visibility = View.VISIBLE
            llEmpty.visibility = View.GONE
        }
    }

    override fun onChildItemClick(position: Int, view: View, type: String) {
        super.onChildItemClick(position, view, type)
        if (currentSelectedAllAlbum) {//当前为全部相册内容
            handleSelectEvent(mAllAlbumDataList,position)
        } else {//其他文件夹相册内容
            handleSelectEvent(mOtherAlbumDataList,position)
        }

    }

    private fun handleSelectEvent(dataList: MutableList<AlbumData>,position: Int) {
        handleBottomButton()
//        mAlbumDataAdapter?.refreshDataItem(position)
        mAlbumDataAdapter?.notifyDataSetChanged()
    }

    private fun handleBottomButton() {
        val selectedAlbumDataSize = albumManagerCollectionInstance.getSelectedAlbumDataSize()
        if (albumManagerCollectionInstance.hasSelectedAlbumData()) {
            tv_send.isEnabled = true
            tv_preview.setTextColor(Color.BLACK)
            tv_preview.isClickable = true
            tv_preview.text = "预览($selectedAlbumDataSize)"
        } else {
            tv_send.isEnabled = false
            tv_preview.setTextColor(ContextCompat.getColor(this, R.color.gray))
            tv_preview.isClickable = false
            tv_preview.text = "预览"
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_send -> {
                setResultAlbum()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadAlbumJob?.let { if (it.isActive) it.cancel() }
        refreshAlbumJob?.let { if (it.isActive) it.cancel() }
        refreshFolderJob?.let { if (it.isActive) it.cancel() }
        albumManagerCollectionInstance.reset()
        albumManagerConfig.reset()
        btnSendClick = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        printD("resultCode=$resultCode,requestCode=$requestCode")
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                AlbumConstant.REQUEST_CODE_ALBUM_PREVIEW_ITEM -> {
                    val selectionData = albumManagerCollectionInstance.getSelectionData()
                    val selectionList = albumManagerCollectionInstance.getSelectionList()
                    mSelectedAlbumDataList = selectionData
                    mSelectList = selectionList
                    val currentAlbumData = albumManagerCollectionInstance.getCurrentAlbumData()
                    val allAlbumData = albumManagerCollectionInstance.getAllAlbumData()
//                    mAlbumDataAdapter?.refreshData(currentAlbumData)
                    mAlbumDataAdapter?.notifyDataSetChanged()
                    if (mSelectList.size > 0) {
                        tv_preview.setTextColor(Color.BLACK)
                        tv_preview.isClickable = true
                    }
                }
                AlbumConstant.REQUEST_CODE_ALBUM_PREVIEW_SELECTED -> {
                    val selectionData = albumManagerCollectionInstance.getSelectionData()
                    val selectionList = albumManagerCollectionInstance.getSelectionList()
                    mSelectedAlbumDataList = selectionData
                    mSelectList = selectionList
                    val currentAlbumData = albumManagerCollectionInstance.getCurrentAlbumData()
                    val allAlbumData = albumManagerCollectionInstance.getAllAlbumData()
                    mAlbumDataAdapter?.refreshData(allAlbumData)
                    if (mSelectList.size > 0) {
                        tv_preview.setTextColor(Color.BLACK)
                        tv_preview.isClickable = true
                    }
                }

                AlbumConstant.REQUEST_CODE_ALBUM_CAMERA_SHOT -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//                        printD("mFilePath=$mFilePath")
                        val file = File(mFilePath)
                        MediaScannerConnection.scanFile(this@AlbumActivity,
                            arrayOf(file.toString()),
                            null,
                            object : MediaScannerConnection.OnScanCompletedListener {
                                override fun onScanCompleted(path: String?, uri: Uri?) {
                                    refreshAlbumData()
                                }
                            }
                        )
                    } else {
                        refreshAlbumData()
                    }
                }
            }
            if (btnSendClick) {
                setResultAlbum()
            }
        }
    }

    private fun refreshAlbumData() {
        refreshAlbumJob = coroutineScope.launch {
            mAllAlbumDataList =
                async(Dispatchers.IO) { albumLoaderInstance.loadAlbumDataX() }.await()
            albumManagerCollectionInstance.addSelectedAlbumData(mAllAlbumDataList[0])
            var ad = AlbumData()
            ad.showCameraPlaceholder = true
            mAllAlbumDataList.add(0, ad)
            mAlbumDataAdapter?.refreshData(mAllAlbumDataList)
            mAlbumFolderList =
                async(Dispatchers.IO) { albumLoaderInstance.loadFolderX() }.await()
            mFolderAdapter?.refreshData(mAlbumFolderList)
            handleBottomButton()
        }
    }

    private fun setResultAlbum() {
        val selectionData = albumManagerCollectionInstance.getSelectionData()
//        for (data in selectionData) {
////            printW("xxx====$data")
//            Luban.with(this)
//                .load(data.path)
//                .ignoreBy(100)
////                .filter { path->return@filter false }
//                .setCompressListener(object : OnCompressListener {
//                    override fun onStart() {
//                    }
//
//                    override fun onSuccess(file: File?) {
//                        printD("file=${file?.absolutePath}")
//                    }
//
//                    override fun onError(e: Throwable?) {
//                    }
//
//                })
//                .launch()
//        }
        val intent = Intent()
        intent.putParcelableArrayListExtra(
            AlbumConstant.SET_RESULT_FOR_SELECTION,
            selectionData
        )
        setResult(RESULT_OK, intent)
        finish()
    }

}