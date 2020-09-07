package com.kevin.codelib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.util.LogUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun getLayoutResID(): Int =R.layout.activity_main

    override fun initView() {
        btn_custom_view.setOnClickListener {
            startNewActivity(CustomViewActivity::class.java)
        }
    }
}