package com.kevin.codelib.util

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.math.roundToInt


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

        /**
         * @param alphaFactor:0.0-1.0
         */
        fun addAlphaForColor(alphaFactor: Float, @ColorInt color: Int): Int {
            if (alphaFactor > 1 || alphaFactor < 0) throw IllegalArgumentException("Alpha value must be 0.0~1.0")
            val alpha = (Color.alpha(color) * alphaFactor).toDouble().roundToInt()
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return Color.argb(alpha, red, green, blue)
        }

        fun beforeAndroidQ(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q

        /**
         * @param context
         * @return 返回true表示显示虚拟导航键、false表示隐藏虚拟导航键
         */
        fun hasNavigationBar(): Boolean {
            //navigationGestureEnabled()从设置中取不到值的话，返回false，因此也不会影响在其他手机上的判断
            return deviceHasNavigationBar() && !navigationGestureEnabled(mContext!!)
        }

        /**
         * 获取主流手机设置中的"navigation_gesture_on"值，判断当前系统是使用导航键还是手势导航操作
         *
         * @param context app Context
         * @return false 表示使用的是虚拟导航键(NavigationBar)，
         * true 表示使用的是手势， 默认是false
         */
        private fun navigationGestureEnabled(context: Context): Boolean {
            val enable =
                Settings.Global.getInt(context.contentResolver, getDeviceInfo(), 0)
            return enable != 0
        }

        /**
         * 判断设备是否存在NavigationBar
         *
         * @return true 存在, false 不存在
         */
        fun deviceHasNavigationBar(): Boolean {
            var haveNav = false
            try {
                //1.通过WindowManagerGlobal获取windowManagerService
                // 反射方法：IWindowManager windowManagerService = WindowManagerGlobal.getWindowManagerService();
                val windowManagerGlobalClass = Class.forName("android.view.WindowManagerGlobal")
                val getWmServiceMethod =
                    windowManagerGlobalClass.getDeclaredMethod("getWindowManagerService")
                getWmServiceMethod.isAccessible = true
                //getWindowManagerService是静态方法，所以invoke null
                val iWindowManager = getWmServiceMethod.invoke(null)

                //2.获取windowMangerService的hasNavigationBar方法返回值
                // 反射方法：haveNav = windowManagerService.hasNavigationBar();
                val iWindowManagerClass: Class<*> = iWindowManager.javaClass
                val hasNavBarMethod = iWindowManagerClass.getDeclaredMethod("hasNavigationBar")
                hasNavBarMethod.isAccessible = true
                haveNav = hasNavBarMethod.invoke(iWindowManager) as Boolean
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return haveNav
        }

        /**
         * 获取设备信息（目前支持几大主流的全面屏手机，亲测华为、小米、oppo、魅族、vivo、三星都可以）
         *
         * @return
         */
        private fun getDeviceInfo(): String? {
            val brand = Build.BRAND
            if (TextUtils.isEmpty(brand)) return "navigationbar_is_min"
            return if (brand.equals("HUAWEI", ignoreCase = true) || "HONOR" == brand) {
                "navigationbar_is_min"
            } else if (brand.equals("XIAOMI", ignoreCase = true)) {
                "force_fsg_nav_bar"
            } else if (brand.equals("VIVO", ignoreCase = true)) {
                "navigation_gesture_on"
            } else if (brand.equals("OPPO", ignoreCase = true)) {
                "navigation_gesture_on"
            } else if (brand.equals("samsung", ignoreCase = true)) {
                "navigationbar_hide_bar_enabled"
            } else {
                "navigationbar_is_min"
            }
        }
        fun getAppName():String{
            val packageManager = mContext?.applicationContext?.packageManager
            val applicationInfo = packageManager?.getApplicationInfo(mContext?.packageName!!,0)
            val applicationLabel = packageManager?.getApplicationLabel(applicationInfo!!)
            return applicationLabel.toString()
        }

    }
}