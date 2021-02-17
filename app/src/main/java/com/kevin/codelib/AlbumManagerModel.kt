package com.kevin.codelib

import android.content.Intent
import com.kevin.codelib.activity.AlbumActivity

/**
 * Created by Kevin on 2021/2/4<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
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
            val intent = Intent(activity, AlbumActivity::class.java)
            val fragment = albumManager.getFragment()
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode)
            } else {
                activity.startActivityForResult(intent, requestCode)
            }
        }

    }


}