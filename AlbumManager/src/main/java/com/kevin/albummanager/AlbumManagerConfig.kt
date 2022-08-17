package com.kevin.albummanager

import com.kevin.albummanager.constant.AlbumConstant
import com.kevin.albummanager.constant.AlbumTheme


/**
 * Created by Kevin on 2021/2/4<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：炽热的孤独心
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
    var canSelected = true
    var showOriginOption = false//是否显示原图选项
    var shotVideoDuration = 60
    var shotVideoSize = 300
    var shotVideoQuality= VideoQuality.HIGH

    enum class VideoQuality() {
        LOW,
        HIGH
    }

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
        canSelected = true
        shotVideoDuration = 60
        shotVideoSize = 100
        shotVideoQuality= VideoQuality.HIGH
    }
}