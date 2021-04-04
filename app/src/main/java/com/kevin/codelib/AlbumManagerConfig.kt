package com.kevin.codelib

import com.kevin.codelib.constant.AlbumConstant
import com.kevin.codelib.constant.AlbumTheme

/**
 * Created by Kevin on 2021/2/4<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
class AlbumManagerConfig {
    var mimeType = AlbumConstant.TYPE_ALL
    var camera = false
    var outputCameraPath = ""
    var minSelectedNum = 1
    var maxSelectedNum = 9
    var spanCount = 4
    var showGif = false
    var enablePreview = false
    var themeId = R.style.AppTheme
    var theme = AlbumTheme.Default
    var showNum = true
    var canSelected=true
    var showOriginOption=false//是否显示原图选项

    companion object {
        val albumManagerConfig: AlbumManagerConfig by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AlbumManagerConfig()
        }

        fun albumManagerConfigWithDefault(): AlbumManagerConfig {
            albumManagerConfig.reset()
            return albumManagerConfig
        }
    }

    fun reset() {
        mimeType = AlbumConstant.TYPE_ALL
        camera = false
        outputCameraPath = ""
        minSelectedNum = 1
        maxSelectedNum = 9
        spanCount = 4
        showGif = false
        enablePreview = false
        themeId = R.style.AppTheme
        theme = AlbumTheme.Default
        showNum = true
        canSelected=true
    }
}