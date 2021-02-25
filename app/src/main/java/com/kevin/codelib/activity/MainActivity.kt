package com.kevin.codelib.activity

import com.kevin.codelib.R
import com.kevin.codelib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun getLayoutResID(): Int = R.layout.activity_main

    override fun initView() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        btn_custom_view.setOnClickListener {
            startNewActivity(CustomViewActivity::class.java)
        }
        btn_share_anim.setOnClickListener {
            startNewActivity(ShareActivity::class.java)
        }
        btn_function.setOnClickListener {
            startNewActivity(FunctionActivity::class.java)
        }
        btn_animation.setOnClickListener({
            startNewActivity(AnimationActivity::class.java)
        })
    }
}