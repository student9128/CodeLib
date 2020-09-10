package com.kevin.codelib.activity

import com.bumptech.glide.Glide
import com.kevin.codelib.R
import com.kevin.codelib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File

/**
 * 显示图片
 */
class ImageActivity : BaseActivity() {
    override fun getLayoutResID(): Int {
        return R.layout.activity_image
    }

    override fun initView() {
        var filePath = intent.getStringExtra("filePath")
        var file: File = File(filePath)
        Glide.with(this).load(file).into(imageView)

    }

}
