package com.kevin.albummanager

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.kevin.albummanager.bean.AlbumData
import com.kevin.albummanager.constant.AlbumConstant
import com.kevin.albummanager.util.AppUtils
import java.lang.ref.WeakReference

/**
 * Created by Kevin on 2021/1/29<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 *
 * 相簿选择管理器
 */
class AlbumManager {
    var mContext: WeakReference<Activity?>? = null
    var mFragment: WeakReference<Fragment?>? = null

    constructor(activity: Activity?, fragment: Fragment?) {
        mContext = WeakReference(activity)
        mFragment = WeakReference(fragment)

    }

    constructor(activity: Activity) : this(activity, null)

    constructor(fragment: Fragment) : this(fragment.activity, fragment)

    companion object {
        fun withContext(@NonNull activity: Activity): AlbumManager {
            AppUtils.initAppUtils(activity)
            return AlbumManager(activity)
        }

        fun withContext(@NonNull fragment: Fragment): AlbumManager {
            fragment.activity?.let { AppUtils.initAppUtils(it) }
            return AlbumManager(fragment)
        }
        fun getAlbumDataResult(data: Intent?): List<AlbumData> {
            return if (data != null) {
                val albumData =
                    data!!.getParcelableArrayListExtra<AlbumData>(AlbumConstant.SET_RESULT_FOR_SELECTION)
                albumData!!
            } else {
                ArrayList<AlbumData>()
            }
        }
    }

    fun getActivity(): Activity? {
        return mContext?.get()
    }

    fun getFragment(): Fragment? {
        return mFragment?.get()
    }

    /**
     * @param mimeType 选择的媒体类型:全部内容（图片和视频）、图片、GIF、不含GIF的图片、视频等
     */
    fun openAlbum(mimeType: String = AlbumConstant.TYPE_ALL): AlbumManagerModel {
        return AlbumManagerModel(this, mimeType)
    }


    fun openAlbumWithCameraShot(mimeType: String): AlbumManagerModel {
        return AlbumManagerModel(this, mimeType, true)
    }


}