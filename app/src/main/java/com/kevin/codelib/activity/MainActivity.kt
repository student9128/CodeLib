package com.kevin.codelib.activity

import android.Manifest
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import com.kevin.albummanager.util.PermissionUtils
import com.kevin.codelib.R
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.util.LogUtils
import com.kevin.codelib.databinding.ActivityMainBinding
import java.lang.reflect.InvocationTargetException

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    private var permissionList = PermissionUtils.getStorageAndCameraPermissions()

    override fun getLayoutView(): View {
        binding = ActivityMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupToolbar() {
        super.setupToolbar()
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(false)
            it.setHomeButtonEnabled(false)
        }
    }

    override fun initView() {
        binding.btnCustomView.setOnClickListener {
            startNewActivity(CustomViewActivity::class.java)
        }
        binding.btnShareAnim.setOnClickListener {
            startNewActivity(ShareActivity::class.java)
        }
        binding.btnAnimation.setOnClickListener({
            startNewActivity(AnimationActivity::class.java)
        })
        val function: (View) -> Unit = {
            LogUtils.logD(TAG, Build.BRAND)
            var x = goToSamsungMarket(this, "com.changqi.yeka_app_2c")
            ToastUtils.showShort("${x}")
            val hasAnyMarketInstalled = hasAnyMarketInstalled(this)
        }
        binding.btnGoMarket.setOnClickListener(function)
        binding.btnGetAppSignMd5.setOnClickListener {
            startNewActivity(AppSignMD5Activity::class.java)
        }
        binding.btnGetImei.setOnClickListener {
            startNewActivity(PhoneIMEIActivity::class.java)
        }
        binding.btnPhoto.setOnClickListener {
            startNewActivity(PhotoActivity::class.java)
        }
        binding.btnCamera.setOnClickListener {
            XXPermissions.with(this)
                .permissions(permissionList)
                .request(object : OnPermissionCallback {
                    override fun onResult(grantedList: List<IPermission>, deniedList: List<IPermission>) {
                        if (deniedList.isEmpty()) {
                            startNewActivity(CameraActivity::class.java)
                        } else {
                            ToastUtils.showShort("授权失败")
                        }
                    }
                })

        }
        binding.btnGoSetting.setOnClickListener {
            val intent = Intent()
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.setData(Uri.parse("package:" + getPackageName()))
            startActivity(intent)
        }
        binding.btnGoNotification.setOnClickListener {
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("app_package", getPackageName())
                intent.putExtra("app_uid", getApplicationInfo().uid)
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.setData(Uri.parse("package:" + getPackageName()))
            } else {
                ///< 4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 9) {
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setClassName(
                        "com.android.settings",
                        "com.android.setting.InstalledAppDetails"
                    );
                    intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
                }
            }
            startActivity(intent)
        }
        binding.btnGenerateExcel.setOnClickListener {
//            generateExcel()
        }
        binding.btnGetApplicationInfo.setOnClickListener {
            startNewActivity(AppInfoListActivity::class.java)
        }
    }

    private fun hasAnyMarketInstalled(context: Context): Boolean {
        val intent = Intent()
        intent.data = Uri.parse("market://details?id=android.browser")
        val list: List<ResolveInfo> = context.getPackageManager()
            .queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return 0 != list.size
    }

    /**
     * 跳转三星应用商店
     * @param context [Context]
     * @param packageName 包名
     * @return `true` 跳转成功 <br></br> `false` 跳转失败
     */
    fun goToSamsungMarket(
        context: Context,
        packageName: String
    ): Boolean {
        val uri =
            Uri.parse("http://apps.samsung.com/appquery/appDetail.as?appId=" + packageName);
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.sec.android.app.samsungapps")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    //判断通知权限是否打开
    private fun isEnableV19(context: Context): Boolean {
        val CHECK_OP_NO_THROW = "checkOpNoThrow"
        val OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION"
        val mAppOps = context.getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val appInfo = context.applicationInfo
        val pkg = context.applicationContext.packageName
        val uid = appInfo.uid
        var appOpsClass: Class<*>? = null /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager::class.java.name)
            val checkOpNoThrowMethod = appOpsClass.getMethod(
                CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                String::class.java
            )
            val opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION)
            val value = opPostNotificationValue[Int::class.java] as Int
            return checkOpNoThrowMethod.invoke(
                mAppOps,
                value,
                uid,
                pkg
            ) as Int == AppOpsManager.MODE_ALLOWED
        } catch (e: ClassNotFoundException) {
        } catch (e: NoSuchMethodException) {
        } catch (e: NoSuchFieldException) {
        } catch (e: InvocationTargetException) {
        } catch (e: IllegalAccessException) {
        } catch (e: Exception) {
        }
        return false
    }

    //判断通知权限是否打开
    private fun isEnableV26(context: Context): Boolean {
        val appInfo = context.applicationInfo
        val pkg = context.applicationContext.packageName
        val uid = appInfo.uid
        return try {
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val sServiceField = notificationManager.javaClass.getDeclaredMethod("getService")
            sServiceField.isAccessible = true
            val sService = sServiceField.invoke(notificationManager)
            val method = sService.javaClass.getDeclaredMethod(
                "areNotificationsEnabledForPackage",
                String::class.java,
                Integer.TYPE
            )
            method.isAccessible = true
            method.invoke(sService, pkg, uid) as Boolean
        } catch (e: Exception) {
            true
        }
    }
}
