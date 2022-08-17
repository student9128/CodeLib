package com.kevin.albummanager

import android.graphics.Color
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.MediaController
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kevin.albummanager.constant.AlbumConstant
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
class PhotoPreviewActivity : com.kevin.albummanager.BaseActivity() {
    var showStatusBarAndNavBar = true
    var handler: Handler? = null
    var runnable: Runnable? = null
    val albumManagerCollectionInstance = com.kevin.albummanager.AlbumManagerCollection.albumManagerCollectionInstance
    override fun getLayoutResID(): Int {
        return R.layout.activity_album_preview
    }

    override fun initView() {
        val previewMethod = intent.getSerializableExtra(AlbumConstant.PREVIEW_METHOD)
        val position = intent.getIntExtra("position", 0)
        val data = albumManagerCollectionInstance.getCurrentAlbumData()
        val albumPreviewAdapter = com.kevin.albummanager.adapter.AlbumPreviewAdapter(
            data, supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        vpViewPagerPreview.adapter = albumPreviewAdapter
        vpViewPagerPreview.currentItem = position
        val albumData = data[position]
        val selectedIndex = albumData.selectedIndex
        if (albumData.selected) {
            tv_select_view.isEnabled = albumData.selected
            tv_select_view.text = selectedIndex.toString()
        }
//        val mimeType = intent.getStringExtra("mimeType")
//        val albumPath = intent.getStringExtra("albumPath")
//        printD("albumPath=$albumPath,mimeType=$mimeType")
        transparentStatusBar()
//        blurLayout.updateForMilliSeconds(100)
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
                val albumData = data[position]
                val selectedIndex = albumData.selectedIndex
                if (albumData.selected) {
                    tv_select_view.isEnabled = albumData.selected
                    tv_select_view.text = selectedIndex.toString()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
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
        super.onBackPressed()
        overridePendingTransition(0,R.anim.photo_fade_out)
    }
}