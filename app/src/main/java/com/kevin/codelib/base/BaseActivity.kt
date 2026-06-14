package com.kevin.codelib.base

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar

/**
 * Created by Kevin on 2020/9/6<br/>
 * Blog:http://student9128.top/
 * 公众号：零點壹度ideality
 * Describe:<br/>
 */
abstract class BaseActivity : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        doSomethingBeforeOnCreate()
        super.onCreate(savedInstanceState)
        val rootView = getLayoutView()
        if (rootView != null) {
            setContentView(rootView)
        } else {
            setContentView(getLayoutResID())
        }
        setupToolbar()
        initView()
    }

    protected open fun setupToolbar() {
        val toolBar = findViewById<Toolbar>(com.kevin.albummanager.R.id.toolBar)
        if (toolBar != null) {
            setSupportActionBar(toolBar)
            supportActionBar?.let {
                it.setDisplayHomeAsUpEnabled(true)
                it.setHomeButtonEnabled(true)
            }
        }
    }

   open fun doSomethingBeforeOnCreate() {
    }

    open fun getLayoutResID(): Int = 0
    open fun getLayoutView(): View? = null
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