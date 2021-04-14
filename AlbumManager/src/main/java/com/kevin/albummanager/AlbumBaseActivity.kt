package com.kevin.albummanager

import android.os.Bundle
import com.kevin.albummanager.util.AlbumUtils

/**
 * Created by Kevin on 2021/2/17<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
abstract class AlbumBaseActivity : BaseActivity() {
    companion object {
        var btnSendClick = false
        var albumManagerConfig: AlbumManagerConfig = AlbumManagerConfig.albumManagerConfig
    }

    override fun doSomethingBeforeOnCreate() {
        if (albumManagerConfig.themeId != R.style.AppTheme) {
            setTheme(albumManagerConfig.themeId)
        } else {
            setTheme(AlbumUtils.getTheme(albumManagerConfig.theme))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    fun formatExpandBackground() {

    }

    fun closeActivity() {
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
//        albumManagerConfig.reset()
    }
}