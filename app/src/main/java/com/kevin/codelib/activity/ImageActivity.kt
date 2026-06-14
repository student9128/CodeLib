package com.kevin.codelib.activity

import android.view.View
import coil.load
import com.kevin.codelib.R
import com.kevin.albummanager.BaseActivity
import com.kevin.codelib.util.AppUtils
import com.kevin.codelib.databinding.ActivityImageBinding
import java.io.File

/**
 * 显示图片
 */
class ImageActivity : com.kevin.albummanager.BaseActivity() {
    private lateinit var binding: ActivityImageBinding

    override fun getLayoutView(): View {
        binding = ActivityImageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        AppUtils.changeStatusBar(this,android.R.color.black)
        var filePath = intent.getStringExtra("filePath")
        var file: File = File(filePath)
        binding.imageView.load(file)

    }

}
