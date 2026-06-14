package com.kevin.codelib.activity

import android.view.View
import com.kevin.codelib.R
import com.kevin.albummanager.BaseActivity
import com.kevin.codelib.activity.customviewshow.RegularHexagonActivity
import com.kevin.codelib.activity.customviewshow.ToggleViewActivity
import com.kevin.codelib.databinding.ActivityCustomViewBinding

/**
 * Created by Kevin on 2020/9/7<br/>
 * Blog:http://student9128.top/
 * 公众号：零點壹度ideality
 * Describe:<br/>
 */
class CustomViewActivity : com.kevin.albummanager.BaseActivity() {
    private lateinit var binding: ActivityCustomViewBinding

    override fun getLayoutView(): View {
        binding = ActivityCustomViewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        binding.btnToggleView.setOnClickListener { startNewActivity(ToggleViewActivity::class.java) }

        binding.btnCustomViewRegularHexagon.setOnClickListener {
            startNewActivity(RegularHexagonActivity::class.java)
        }
        binding.btnReverseProgress.setOnClickListener {
            startNewActivity(RegularHexagonActivity::class.java)
        }
    }

}
