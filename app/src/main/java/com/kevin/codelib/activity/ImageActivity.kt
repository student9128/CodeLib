package com.kevin.codelib.activity

import com.bumptech.glide.Glide
import com.kevin.codelib.R
import com.kevin.albummanager.BaseActivity
import com.kevin.codelib.util.AppUtils
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File

/**
 * 显示图片
 */
class ImageActivity : com.kevin.albummanager.BaseActivity() {
    override fun getLayoutResID(): Int {
        return R.layout.activity_image
    }

    override fun initView() {
        AppUtils.changeStatusBar(this,android.R.color.black)
        var filePath = intent.getStringExtra("filePath")
        var file: File = File(filePath)
        Glide.with(this).load(file).into(imageView)

    }

}
