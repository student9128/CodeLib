package com.kevin.codelib.activity

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.kevin.codelib.R
import com.kevin.codelib.base.BaseActivity
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
    var showStatusBarAndNavBar = false
    override fun getLayoutResID(): Int {
        return R.layout.activity_album_preview
    }

    override fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
           hideStatusBarAndNavBar()
        }
        val albumPath = intent.getStringExtra("albumPath")
        Glide.with(this)
            .load(albumPath)
            .into(ivImageViewPreview)
        llBack.setOnClickListener { onBackPressed() }
        clContainer.setOnClickListener {
            if (showStatusBarAndNavBar) {
                hideStatusBarAndNavBar()
            } else {
                showStatusBarAndNavBar()
            }
        }
    }

    fun hideStatusBarAndNavBar() {
        printD("Hide======")
        ToastUtils.showShort("Hide")
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
        ToastUtils.showShort("Show")
        printD("Show======")
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        showStatusBarAndNavBar = true
    }
}