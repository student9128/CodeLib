package com.kevin.codelib

import android.app.Activity
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
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
            return AlbumManager(activity)
        }

        fun withContext(@NonNull fragment: Fragment): AlbumManager {
            return AlbumManager(fragment)
        }
    }
}