package com.kevin.codelib.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
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
import com.kevin.codelib.AlbumManagerCollection
import com.kevin.codelib.AlbumManagerConfig
import com.kevin.codelib.R
import com.kevin.codelib.adapter.AlbumAdapter
import com.kevin.codelib.adapter.AlbumFolderAdapter
import com.kevin.codelib.base.AlbumBaseActivity
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.bean.AlbumData
import com.kevin.codelib.bean.AlbumFolder
import com.kevin.codelib.constant.AlbumConstant
import com.kevin.codelib.constant.AlbumPreviewMethod
import com.kevin.codelib.interfaces.OnRecyclerItemClickListener
import com.kevin.codelib.loader.AlbumLoader
import com.kevin.codelib.util.DisplayUtils
import com.kevin.codelib.widget.DividerItemDecoration
import com.kevin.codelib.widget.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_album.*
import kotlinx.android.synthetic.main.activity_function.*
import kotlinx.android.synthetic.main.layout_album_folder_popup_window.view.*
import kotlinx.coroutines.*
import kotlin.collections.HashMap

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
    val attrArray = intArrayOf(android.R.attr.colorPrimary)
    override fun getLayoutResID(): Int {
        return R.layout.activity_album
    }

    override fun initView() {
//        val typeArray = obtainStyledAttributes(albumManagerConfig.themeId, attrArray)
//        val color = typeArray.getColor(0,R.color.colorPrimary)
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

        albumLoaderInstance.setParams(this)
//        albumLoaderInstance.loadAlbumFolder()

        loadAlbumJob = coroutineScope.launch {
//            loadAlbum()
//            loadX()
            mAllAlbumDataList =
                async(Dispatchers.IO) { albumLoaderInstance.loadAlbumDataX() }.await()
            mAlbumFolderList = async(Dispatchers.IO) { albumLoaderInstance.loadFolderX() }.await()
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
        ActivityCompat.startActivityForResult(this, intent, requestCode, customAnimation.toBundle())
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

                albumManagerCollectionInstance.saveAlbumFolderType(albumFolder.displayName)

                if (albumFolder.displayName == "全部") {
                    currentSelectedAllAlbum = true
                    tvTitle.text = "全部"
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
            handleSelectEvent(position, mAllAlbumDataList)
        } else {//其他文件夹相册内容
            handleSelectEvent(position, mOtherAlbumDataList)
        }

    }

    private fun handleSelectEvent(position: Int, dataList: MutableList<AlbumData>) {
        val albumData = dataList[position]
        if (mSelectList.size == 0) {
            albumData.selected = true
            albumData.selectedIndex = 1
            dataList[position] = albumData
            var map: MutableMap<Int, AlbumData> = HashMap()
            albumData.key = position
            map[position] = albumData
            mSelectList.add(map)
            mSelectedAlbumDataList.add(albumData)
        } else {
            if (albumData.selected) {//选中的都在集合中存储，获取改集合
                val iterator = mSelectList.iterator()
                while (iterator.hasNext()) {
                    val next = iterator.next()

                    if (next[position] == dataList[position]) {
                        mSelectedAlbumDataList.remove(albumData)
                        iterator.remove()
                        albumData.selected = false
                        albumData.selectedIndex = -1
                        dataList[position] = albumData
                    }
                }
                printD("mSelectList.Size=${mSelectList.size},mSelectedAlbumDataList.size=${mSelectedAlbumDataList.size}")
                for (index in 0 until mSelectList.size) {
                    val mutableMap = mSelectList[index]
                    for ((key, value) in mutableMap) {
                        val albumDataX = dataList[key]
                        albumDataX.selectedIndex = index + 1
                        dataList[key] = albumDataX

                    }
                }

            } else {//不在集合中
                albumData.selected = true
                albumData.selectedIndex = mSelectList.size + 1
                dataList[position] = albumData
                var map: MutableMap<Int, AlbumData> = HashMap()
                albumData.key = position
                map[position] = albumData
                mSelectList.add(map)
                mSelectedAlbumDataList.add(albumData)
            }
        }
        if (mSelectList.size > 0) {
            tv_send.isEnabled = true
            tv_preview.setTextColor(Color.BLACK)
            tv_preview.isClickable = true
        } else {
            tv_send.isEnabled = false
            tv_preview.setTextColor(ContextCompat.getColor(this, R.color.gray))
            tv_preview.isClickable = false
        }
        albumManagerCollectionInstance.saveSelectionData(mSelectedAlbumDataList)
        albumManagerCollectionInstance.saveSelectionList(mSelectList)
        mAlbumDataAdapter?.refreshData(dataList)
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
        AlbumManagerCollection.albumManagerCollectionInstance.reset()
        btnSendClick = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        printD("resultCode=$resultCode,requestCode=$requestCode")
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                AlbumConstant.REQUEST_CODE_ALBUM_PREVIEW_ITEM -> {
                    val selectionData = albumManagerCollectionInstance.getSelectionData()
                    val selectionList = albumManagerCollectionInstance.getSelectionList()
                    mSelectedAlbumDataList = selectionData
                    mSelectList = selectionList
                    val currentAlbumData = albumManagerCollectionInstance.getCurrentAlbumData()
                    mAlbumDataAdapter?.refreshData(currentAlbumData)
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
            }
            if (btnSendClick) {
                setResultAlbum()
            }
        }
    }

    private fun setResultAlbum() {
        val intent = Intent()
        intent.putParcelableArrayListExtra(
            AlbumConstant.SET_RESULT_FOR_SELECTION,
            mSelectedAlbumDataList
        )
        setResult(RESULT_OK, intent)
        finish()
    }

}