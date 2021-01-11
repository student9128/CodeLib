package com.kevin.codelib.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.widget.Button
import com.blankj.utilcode.util.LogUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.kevin.codelib.R
import com.kevin.codelib.activity.PhoneIMEIActivity
import com.kevin.codelib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_phone_imei.*

/**
 * Created by Kevin on 2021/1/8<br></br>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br></br>
 */
class PhoneIMEIActivity : BaseActivity() {
    override fun getLayoutResID(): Int {
        return R.layout.activity_phone_imei
    }

    override fun initView() {
        val btn = findViewById<Button>(R.id.btn_get_imei)
        btn.setOnClickListener {
            XXPermissions.with(this@PhoneIMEIActivity)
                .permission(Permission.READ_PHONE_STATE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(
                        list: List<String>,
                        all: Boolean
                    ) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            val imei = getIMEI(this@PhoneIMEIActivity)
                            tv_imei.text = "IMEI是 $imei"
                        } else {
                            val string =
                                Settings.System.getString(
                                    this@PhoneIMEIActivity.contentResolver,
                                    Settings.Secure.ANDROID_ID
                                )
                            tv_imei.text = "Android ID替代的imei是 $string"
                            LogUtils.d("string=$string")
                        }
                    }

                    override fun onDenied(
                        list: List<String>,
                        never: Boolean
                    ) {
                        if (never) {
                            XXPermissions.startPermissionActivity(this@PhoneIMEIActivity, list)
                        } else {
                        }
                    }
                })
        }
    }

    companion object {
        /**
         * @param context
         * @return
         */
        @SuppressLint("MissingPermission")
        fun getIMEI(context: Context): String {
            val manager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            try {
                val method =
                    manager.javaClass.getMethod("getImei", Int::class.javaPrimitiveType)
                val imei1 = method.invoke(manager, 0) as String
                val imei2 = method.invoke(manager, 1) as String
                LogUtils.d("imei1=$imei1,imei2=$imei2")
                if (TextUtils.isEmpty(imei2)) {
                    return imei1
                }
                if (!TextUtils.isEmpty(imei1)) {
                    //因为手机卡插在不同位置，获取到的imei1和imei2值会交换，所以取它们的最小值,保证拿到的imei都是同一个
                    var imei = ""
                    imei = if (imei1.compareTo(imei2) <= 0) {
                        imei1
                    } else {
                        imei2
                    }
                    return imei
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return manager.deviceId
            }
            return ""
        }
    }
}