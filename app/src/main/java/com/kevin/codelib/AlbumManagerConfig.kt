package com.kevin.codelib

import com.kevin.codelib.constant.AlbumConstant

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

    companion object {
        val albumManagerConfig: AlbumManagerConfig by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AlbumManagerConfig()
        }

        fun albumManagerConfigWithDefault(): AlbumManagerConfig {
            albumManagerConfig.reset()
            return albumManagerConfig
        }
    }

    private fun reset() {
        var mimeType = AlbumConstant.TYPE_ALL
        var camera = false
        var outputCameraPath = ""
        var minSelectedNum = 1
        var maxSelectedNum = 9
        var spanCount = 4
        var showGif = false
        var enablePreview = false
    }
}