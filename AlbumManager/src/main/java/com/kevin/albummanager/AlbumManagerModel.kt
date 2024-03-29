package com.kevin.albummanager

import android.content.Intent
import com.kevin.albummanager.constant.AlbumTheme

/**
 * Created by Kevin on 2021/2/4<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：炽热的孤独心
 *
 * Describe:<br/>
 *
 *
 */
class AlbumManagerModel(
    val albumManager: AlbumManager,
    val mimeType: String,
    var showCameraShot: Boolean = false,
    private val albumManagerConfig: AlbumManagerConfig = AlbumManagerConfig.albumManagerConfig
) {
    init {
        albumManagerConfig.mimeType = mimeType
    }

    fun setTheme(theme: AlbumTheme): AlbumManagerModel {
        albumManagerConfig.theme = theme
        return this
    }

    fun setCustomTheme(themeId: Int): AlbumManagerModel {
        albumManagerConfig.themeId = themeId
        return this
    }
    fun showCameraShot(b: Boolean): AlbumManagerModel {
        albumManagerConfig.camera=b
        return this
    }

    fun showSelectedWithNum(b: Boolean): AlbumManagerModel {
        albumManagerConfig.showNum = b
        return this
    }

    fun maxSelectedNum(maxSelectNum: Int): AlbumManagerModel {
        albumManagerConfig.maxSelectedNum = maxSelectNum
        return this
    }

    fun minSelectedNum(minSelectNum: Int): AlbumManagerModel {
        albumManagerConfig.minSelectedNum = minSelectNum
        return this
    }

    fun showPreview(enablePreview: Boolean): AlbumManagerModel {
        albumManagerConfig.enablePreview = enablePreview
        return this
    }

    fun forResult(requestCode: Int) {
        val activity = albumManager.getActivity()
        activity?.let {
            val intent = Intent(activity, com.kevin.albummanager.AlbumActivity::class.java)
            val fragment = albumManager.getFragment()
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode)
            } else {
                activity.startActivityForResult(intent, requestCode)
            }
        }

    }


}