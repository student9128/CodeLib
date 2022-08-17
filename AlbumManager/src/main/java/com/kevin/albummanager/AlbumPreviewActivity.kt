package com.kevin.albummanager

import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.MediaController
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kevin.albummanager.adapter.AlbumPreviewAdapter
import com.kevin.albummanager.bean.AlbumData
import com.kevin.albummanager.constant.AlbumConstant
import com.kevin.albummanager.constant.AlbumPreviewMethod
import com.kevin.albummanager.util.AlbumUtils
import com.kevin.albummanager.util.AppUtils
import kotlinx.android.synthetic.main.activity_album_preview.*


/**
 * Created by Kevin on 2021/1/20<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：炽热的孤独心
 *
 * Describe:<br/>
 */
class AlbumPreviewActivity : com.kevin.albummanager.AlbumBaseActivity(), View.OnClickListener {
    var showStatusBarAndNavBar = true
    var handler: Handler? = null
    var runnable: Runnable? = null
    val albumManagerCollectionInstance = AlbumManagerCollection.albumManagerCollectionInstance
    var mCurrentPosition = 0
    var albumPreviewAdapter: AlbumPreviewAdapter? = null
    private var mDataList: ArrayList<AlbumData> = ArrayList()
    private var mPreviewMethod = AlbumPreviewMethod.MULTIPLE.name
    override fun getLayoutResID(): Int {
        return R.layout.activity_album_preview
    }

    override fun initView() {
        mPreviewMethod = intent.getStringExtra(AlbumConstant.PREVIEW_METHOD)!!
        val position = intent.getIntExtra("position", 0)
        mCurrentPosition = position
        if (mPreviewMethod == AlbumPreviewMethod.MULTIPLE.name) {
            mDataList = albumManagerCollectionInstance.getCurrentAlbumData()
        } else {
            mDataList = albumManagerCollectionInstance.getSelectionData()
        }
        albumPreviewAdapter = AlbumPreviewAdapter(
            mDataList, supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        vpViewPagerPreview.adapter = albumPreviewAdapter
        vpViewPagerPreview.currentItem = position
        if (mDataList.size > 0) {
            val albumData = mDataList[position]
            if (albumManagerCollectionInstance.isSelected(albumData)) {
                printD("AlbumPreviewActivity")
                tv_select_view.isEnabled = true
                if (albumManagerConfig.showNum) {
                    tv_select_view.text =
                        albumManagerCollectionInstance.checkedNum(albumData).toString()
                } else {
                    showSelectedWithTick()
                }
            }
        }
        val selectionList = albumManagerCollectionInstance.getSelectionData()
        checkBtnSendEnabled(selectionList)
//        val mimeType = intent.getStringExtra("mimeType")
//        val albumPath = intent.getStringExtra("albumPath")
//        printD("albumPath=$albumPath,mimeType=$mimeType")
        transparentStatusBar()
//        blurLayout.updateForMilliSeconds(100)
//        blurLayout.viewBehind = vpViewPagerPreview
//        if (AlbumUtils.isVideo(mimeType!!)) {
//            hideStatusBarAndNavBar()
//            showStatusBarAndNavBar()
//            ivImageViewPreview.visibility = View.VISIBLE
//            videoViewPreview.visibility = View.GONE
//            ivImageViewPreview.loadImageIfAvailable(albumPath)
//            ivPlay.visibility=View.VISIBLE
//            ivPlay.setOnClickListener {
//                ivPlay.visibility=View.GONE
//                postLoadVideo(albumPath!!)
//            }
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//            } else {
//           hideStatusBarAndNavBar()
//                showStatusBarAndNavBar()
//            }
//            ivPlay.visibility=View.GONE
//            ivImageViewPreview.visibility = View.VISIBLE
//            videoViewPreview.visibility = View.GONE
//            clContainer.setOnClickListener {
//                if (showStatusBarAndNavBar) {
//                    hideStatusBarAndNavBar()
//                } else {
//                    showStatusBarAndNavBar()
//                }
//            }
//            ivImageViewPreview.loadImageIfAvailable(albumPath)
//            postLoadImageAgain(albumPath)
//        }
//        llContainer.setBackgroundColor(AppUtils.addAlphaForColor(0.5f,ContextCompat.getColor(this,R.color.colorAccent)))
        llBack.setOnClickListener { onBackPressed() }
        vpViewPagerPreview.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                mCurrentPosition = position
//                val dataList = albumManagerCollectionInstance.getCurrentAlbumData()
                val albumData = mDataList[position]
                val selectedIndex = albumData.selectedIndex
                val selected = albumManagerCollectionInstance.isSelected(albumData)
                tv_select_view.isEnabled = selected
                if (selected) {
                    if (albumManagerConfig.showNum) {
                        tv_select_view.text =
                            albumManagerCollectionInstance.checkedNum(albumData).toString()
                    } else {
                        showSelectedWithTick()
                    }
                } else {
                    tv_select_view.text = ""
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
        tv_send.setOnClickListener(this)
        ll_select_view.setOnClickListener(this)
    }

    private fun showSelectedWithTick() {
        AlbumUtils.formatCustomFont(this, tv_select_view)
        tv_select_view.textSize = 10f
        tv_select_view.text = getString(R.string.icon_tick)
    }

    private fun checkBtnSendEnabled(selectionList: ArrayList<AlbumData>) {
        tv_send.isEnabled = selectionList.size > 0
    }

    private fun postLoadVideo(albumPath: String) {
//        ivImageViewPreview.visibility = View.GONE
        videoViewPreview.visibility = View.VISIBLE
        videoViewPreview.setVideoPath(albumPath)
        val mediaController = MediaController(this)
        videoViewPreview.setMediaController(mediaController)
        videoViewPreview.start()
    }

    private fun postLoadImageAgain(albumPath: String?) {
//        handler = Handler()
//        runnable = Runnable { ivImageViewPreview.loadImageIfAvailable(albumPath) }
//        handler!!.postDelayed(runnable!!, 500)
    }

    fun transparentStatusBar() {
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.statusBarColor = AppUtils.addAlphaForColor(0.3f, Color.BLACK)
    }

    fun hideStatusBarAndNavBar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        showStatusBarAndNavBar = false
    }

    fun showStatusBarAndNavBar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        showStatusBarAndNavBar = true
    }


    override fun onDestroy() {
        super.onDestroy()
        if (videoViewPreview.isPlaying) {
            videoViewPreview.stopPlayback()
            videoViewPreview.suspend()
        }
        runnable?.let { handler?.removeCallbacks(it) }
    }

    private fun ImageView.loadImageIfAvailable(url: String?) {
        url?.let {
            Glide.with(context)
                .applyDefaultRequestOptions(
                    RequestOptions().skipMemoryCache(false)
                        .error(R.drawable.ic_image_error)
                        .placeholder(R.drawable.ic_image_placehodler)
                        .fitCenter()
                )
                .load(url)
                .into(this)
        }
    }

    override fun onBackPressed() {
        setResult()
        super.onBackPressed()
        overridePendingTransition(0, R.anim.photo_fade_out)
    }

    private fun setResult() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_send -> {
                btnSendClick = true
                onBackPressed()
            }
            R.id.ll_select_view -> {
                printD("mDataList.size=${mDataList.size}")
                if(mDataList.size>0){

                }
                val albumData = mDataList[mCurrentPosition]
                albumManagerCollectionInstance.savePreviewSelectionData(albumData)
                if (albumManagerCollectionInstance.isSelected(albumData)) {
                    albumManagerCollectionInstance.removeSelectedAlbumData(albumData)
                    tv_select_view.isEnabled = false
                    tv_select_view.text = ""
                } else {
                    if (albumManagerConfig.maxSelectedNum > albumManagerCollectionInstance.getSelectedAlbumDataSize()) {
                        albumManagerCollectionInstance.addSelectedAlbumData(albumData)
                        tv_select_view.isEnabled = true
                        if (albumManagerConfig.showNum) {
                            tv_select_view.text =
                                albumManagerCollectionInstance.checkedNum(albumData).toString()
                        } else {
                            showSelectedWithTick()
                        }
                    } else {
                        ToastUtils.showShort("最多只能选择${albumManagerConfig.maxSelectedNum}个文件")
                    }
                }
                checkBtnSendEnabled(albumManagerCollectionInstance.getSelectionData())
                albumPreviewAdapter?.refreshData(mDataList)
                if (mPreviewMethod == AlbumPreviewMethod.MULTIPLE.name) {
//                    handleMultiple()
                } else {
//                    handleSingle()
                }
            }
        }
    }

    fun handleMultiple() {
        val dataList = mDataList
//                val allAlbumDataList = albumManagerCollectionInstance.getAllAlbumData()
        val albumData = dataList[mCurrentPosition]
        val selectionData = albumManagerCollectionInstance.getSelectionData()
        val selectionList = albumManagerCollectionInstance.getSelectionList()
        if (selectionList.size == 0) {
            albumData.selected = true
            albumData.selectedIndex = 1
            dataList[mCurrentPosition] = albumData
            var map: MutableMap<Int, AlbumData> = HashMap()
            albumData.key = mCurrentPosition
            map[mCurrentPosition] = albumData
            selectionList.add(map)
            selectionData.add(albumData)
            tv_select_view.text = "1"
            tv_select_view.isEnabled = true
        } else {
            if (albumData.selected) {//选中的都在集合中存储，获取改集合
                tv_select_view.isEnabled = false
                tv_select_view.text = ""
                val iterator = selectionList.iterator()
                while (iterator.hasNext()) {
                    val next = iterator.next()
                    printD("next=${next},next.v=${next[dataList[mCurrentPosition].key]}")
                    if (next[albumData.key] == dataList[mCurrentPosition]) {
                        selectionData.remove(albumData)
                        iterator.remove()
                        albumData.selected = false
                        albumData.selectedIndex = -1
                        dataList[mCurrentPosition] = albumData
                    }
                }
                printD("selectionList.size======${selectionList.size}")
                for (index in 0 until selectionList.size) {
                    val mutableMap = selectionList[index]
                    for ((key, value) in mutableMap) {
                        printD("key=$key,value=$value,dataList.size=${dataList.size}")
                        val albumDataX = dataList[key]
                        albumDataX.selectedIndex = index + 1
                        dataList[key] = albumDataX
                    }
                }

            } else {//不在集合中
                if (albumManagerConfig.showNum) {
                    tv_select_view.text = (selectionList.size + 1).toString()
                } else {
                    showSelectedWithTick()
                }
                tv_select_view.isEnabled = true
                albumData.selected = true
                albumData.selectedIndex = selectionList.size + 1
                dataList[mCurrentPosition] = albumData
                var map: MutableMap<Int, AlbumData> = HashMap()
                if (albumData.key == -1) {
                    albumData.key = mCurrentPosition
                }
                map[mCurrentPosition] = albumData
                selectionList.add(map)
                selectionData.add(albumData)
            }
        }
//        checkBtnSendEnabled(selectionList)
        printD("selectionList.size=${selectionList.size},selectionData.size=${selectionData.size}")
        albumManagerCollectionInstance.saveSelectionData(selectionData)
        albumManagerCollectionInstance.saveSelectionList(selectionList)
        albumManagerCollectionInstance.saveCurrentAlbumData(dataList)
        mDataList = dataList
        albumPreviewAdapter?.refreshData(dataList)
    }

    fun handleSingle() {
        val dataList = mDataList
        val allAlbumDataList = albumManagerCollectionInstance.getAllAlbumData()
        val albumData = dataList[mCurrentPosition]
        val selectionData = albumManagerCollectionInstance.getSelectionData()
        val selectionList = albumManagerCollectionInstance.getSelectionList()
        if (selectionList.size == 0) {
            albumData.selected = true
            albumData.selectedIndex = 1
            dataList[mCurrentPosition] = albumData
            allAlbumDataList[albumData.key] = albumData
            var map: MutableMap<Int, AlbumData> = HashMap()
            albumData.key = mCurrentPosition
            map[mCurrentPosition] = albumData
            selectionList.add(map)
            selectionData.add(albumData)
            tv_select_view.text = "1"
            tv_select_view.isEnabled = true
        } else {
            if (albumData.selected) {//选中的都在集合中存储，获取改集合

                tv_select_view.isEnabled = false
                tv_select_view.text = ""
                val iterator = selectionList.iterator()
                while (iterator.hasNext()) {
                    val next = iterator.next()
                    printD("albumData.key=${albumData.key}")
                    printD("next=${next},next.v=${next[dataList[mCurrentPosition].key]}")
                    if (next[albumData.key] == dataList[mCurrentPosition]) {
//                                    selectionData.remove(albumData)
                        printD("haha~~~~~~~~~~`")
                        iterator.remove()
                        albumData.selected = false
                        albumData.selectedIndex = -1
                        allAlbumDataList[albumData.key] = albumData
                    } else {
                        printD("hwwwwwwwwwwwaha~~~~~~~~~~`")
                    }
                }
                printD("selectionList.size======${selectionList.size},dataList.size=${dataList.size}")
                for (index in 0 until selectionList.size) {
                    val mutableMap = selectionList[index]
                    for ((key, value) in mutableMap) {
                        printD("key=$key,value=$value,dataList.size=${dataList.size}")
                        value.selectedIndex = index + 1
                        allAlbumDataList[key] = value
                        for (i in 0 until dataList.size) {
                            if (dataList[i].key == key) {
                                dataList[i].selectedIndex = index + 1
                            }
                        }
                    }
                }

            } else {//不在集合中
                if (albumManagerConfig.showNum) {
                    tv_select_view.text = (selectionList.size + 1).toString()
                } else {
                    showSelectedWithTick()
                }
                tv_select_view.isEnabled = true
                albumData.selected = true
                albumData.selectedIndex = selectionList.size + 1
                dataList[mCurrentPosition] = albumData
                allAlbumDataList[albumData.key] = albumData
                var map: MutableMap<Int, AlbumData> = HashMap()
                if (albumData.key == -1) {
                    albumData.key = mCurrentPosition
                }
                map[mCurrentPosition] = albumData
                selectionList.add(map)
                selectionData.add(albumData)
            }
        }
//        checkBtnSendEnabled(selectionList)
        printD("selectionList.size=${selectionList.size},selectionData.size=${selectionData.size}")
        albumManagerCollectionInstance.saveSelectionData(selectionData)
        albumManagerCollectionInstance.saveSelectionList(selectionList)
        albumManagerCollectionInstance.saveCurrentAlbumData(dataList)
        albumManagerCollectionInstance.saveAllAlbumData(allAlbumDataList)
        mDataList = dataList
        albumPreviewAdapter?.refreshData(dataList)
    }
}