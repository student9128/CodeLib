package com.kevin.albummanager

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.layout_tool_bar.*

/**
 * Created by Kevin on 2020/9/6<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
abstract class BaseActivity : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        doSomethingBeforeOnCreate()
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResID())
        setSupportActionBar(toolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
        initView()
    }

   open fun doSomethingBeforeOnCreate() {
    }

    abstract fun getLayoutResID(): Int
    abstract fun initView()

    fun startNewActivity(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}