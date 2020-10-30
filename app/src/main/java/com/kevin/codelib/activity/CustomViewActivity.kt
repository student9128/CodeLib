package com.kevin.codelib.activity

import com.kevin.codelib.R
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.activity.customviewshow.RegularHexagonActivity
import com.kevin.codelib.activity.customviewshow.ToggleViewActivity
import kotlinx.android.synthetic.main.activity_custom_view.*

/**
 * Created by Kevin on 2020/9/7<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
class CustomViewActivity : BaseActivity() {
    override fun getLayoutResID(): Int =
        R.layout.activity_custom_view

    override fun initView() {
        btn_toggle_view.setOnClickListener { startNewActivity(ToggleViewActivity::class.java) }

        btn_custom_view_regularHexagon.setOnClickListener {
            startNewActivity(RegularHexagonActivity::class.java)
        }
        btnReverseProgress.setOnClickListener {
            startNewActivity(RegularHexagonActivity::class.java)
        }
    }

}