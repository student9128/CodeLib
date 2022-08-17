package com.kevin.codelib.activity

import com.kevin.codelib.R
import com.kevin.albummanager.BaseActivity
import com.kevin.codelib.activity.customviewshow.RegularHexagonActivity
import com.kevin.codelib.activity.customviewshow.ToggleViewActivity
import kotlinx.android.synthetic.main.activity_custom_view.*

/**
 * Created by Kevin on 2020/9/7<br/>
 * Blog:http://student9128.top/
 * 公众号：炽热的孤独心
 * Describe:<br/>
 */
class CustomViewActivity : com.kevin.albummanager.BaseActivity() {
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