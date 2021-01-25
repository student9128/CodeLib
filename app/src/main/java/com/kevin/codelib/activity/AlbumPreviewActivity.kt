package com.kevin.codelib.activity

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.MediaController
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.kevin.codelib.R
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.util.AlbumUtils
import com.kevin.codelib.util.AppUtils
import com.kevin.codelib.util.LogUtils
import kotlinx.android.synthetic.main.activity_album_preview.*

/**
 * Created by Kevin on 2021/1/20<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
class AlbumPreviewActivity : BaseActivity() {
    var showStatusBarAndNavBar = true
    override fun getLayoutResID(): Int {
        return R.layout.activity_album_preview
    }

    override fun initView() {
        val mimeType = intent.getStringExtra("mimeType")
        val albumPath = intent.getStringExtra("albumPath")
        printD("albumPath=$albumPath,mimeType=$mimeType")
        if (AlbumUtils.isVideo(mimeType!!)) {
            hideStatusBarAndNavBar()
            ivImageViewPreview.visibility = View.GONE
            videoViewPreview.visibility = View.VISIBLE
            videoViewPreview.setVideoPath(albumPath)
            val mediaController = MediaController(this)
            videoViewPreview.setMediaController(mediaController)
            videoViewPreview.start()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
            } else {
//           hideStatusBarAndNavBar()
                showStatusBarAndNavBar()
            }
            ivImageViewPreview.visibility = View.VISIBLE
            videoViewPreview.visibility = View.GONE
            Glide.with(this)
                .applyDefaultRequestOptions(RequestOptions().skipMemoryCache(false)
                    .error(R.drawable.ic_image_error)
                    .placeholder(R.drawable.ic_image_placehodler))
                .load(albumPath)
                .into(ivImageViewPreview)
//            if (AlbumUtils.isGif(mimeType)) {
//                var gif: GifDrawable = ivImageViewPreview.drawable as GifDrawable
//                gif.start()
//
//            }
            clContainer.setOnClickListener {
                if (showStatusBarAndNavBar) {
                    hideStatusBarAndNavBar()
                } else {
                    showStatusBarAndNavBar()
                }
            }
        }
//        llContainer.setBackgroundColor(AppUtils.addAlphaForColor(0.5f,ContextCompat.getColor(this,R.color.colorAccent)))
        llBack.setOnClickListener { onBackPressed() }
    }

    fun hideStatusBarAndNavBar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
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
    }
}