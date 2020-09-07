package com.kevin.codelib.base

import android.content.Intent
import android.os.Bundle

/**
 * Created by Kevin on 2020/9/6<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
abstract class BaseActivity : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResID())
        initView()
    }

    abstract fun getLayoutResID(): Int
    abstract fun initView()

    fun startNewActivity(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
    }
}