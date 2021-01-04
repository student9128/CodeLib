package com.kevin.codelib.util

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.graphics.Color
import android.os.Build
import androidx.core.content.ContextCompat
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * Created by Kevin on 2020/9/6<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
class AppUtils {
    companion object {
        private var isDebugMode = false
        private var mContext: Context? = null
        fun isDebug(): Boolean {
            return isDebugMode
        }

        fun initAppUtils(context: Context) {
            mContext = context
            isDebugMode =
                context.applicationInfo != null && (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        }

        fun changeStatusBar(activity: Activity, color: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.statusBarColor = darkenColor(
                    ContextCompat.getColor(activity, color)
                )
            }
        }

        private fun darkenColor(color: Int): Int {
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            hsv[2] *= 0.8f
            return Color.HSVToColor(hsv)
        }

        fun getSignMd5Str(packageName: String): String? {
            try {

                val packageInfo: PackageInfo? =
                    mContext?.packageManager?.getPackageInfo(
                        packageName,
                        PackageManager.GET_SIGNATURES
                    )
                val signs = packageInfo?.signatures
                val sign: Signature? = signs?.get(0)
                return getMd5Str(sign?.toByteArray())
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return ""
        }

        private fun getMd5Str(byteStr: ByteArray?): String? {
            var messageDigest: MessageDigest? = null
            val md5StrBuff = StringBuffer()
            try {
                messageDigest = MessageDigest.getInstance("MD5")
                messageDigest.reset()
                messageDigest.update(byteStr)
                val byteArray = messageDigest.digest()
                for (i in byteArray.indices) {
                    if (Integer.toHexString(0xFF and byteArray[i].toInt()).length == 1) {
                        md5StrBuff.append("0")
                            .append(Integer.toHexString(0xFF and byteArray[i].toInt()))
                    } else {
                        md5StrBuff.append(Integer.toHexString(0xFF and byteArray[i].toInt()))
                    }
                }
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            return md5StrBuff.toString()
        }
    }
}