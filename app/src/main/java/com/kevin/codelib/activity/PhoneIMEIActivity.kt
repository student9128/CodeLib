package com.kevin.codelib.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import com.kevin.codelib.R
import com.kevin.albummanager.BaseActivity
import com.kevin.codelib.databinding.ActivityPhoneImeiBinding

/**
 * Created by Kevin on 2021/1/8<br></br>
 * Blog:http://student9128.top/
 * 公众号：零點壹度ideality
 * Describe:<br></br>
 */
class PhoneIMEIActivity : com.kevin.albummanager.BaseActivity() {
    private lateinit var binding: ActivityPhoneImeiBinding

    override fun getLayoutView(): View {
        binding = ActivityPhoneImeiBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        binding.btnGetImei.setOnClickListener {
            XXPermissions.with(this@PhoneIMEIActivity)
                .permission(PermissionLists.getReadPhoneStatePermission())
                .request(object : OnPermissionCallback {
                    override fun onResult(grantedList: List<IPermission>, deniedList: List<IPermission>) {
                        if (deniedList.isEmpty()) {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                val imei = getIMEI(this@PhoneIMEIActivity)
                                binding.tvImei.text = "IMEI是 $imei"
                            } else {
                                val string =
                                    Settings.System.getString(
                                        this@PhoneIMEIActivity.contentResolver,
                                        Settings.Secure.ANDROID_ID
                                    )
                                binding.tvImei.text = "Android ID替代的imei是 $string"
                                LogUtils.d("string=$string")
                            }
                        } else {
                            XXPermissions.startPermissionActivity(this@PhoneIMEIActivity, deniedList)
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
