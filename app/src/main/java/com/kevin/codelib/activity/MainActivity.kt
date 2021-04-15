package com.kevin.codelib.activity

import android.Manifest
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
import com.kevin.codelib.R
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.util.LogUtils
import kotlinx.android.synthetic.main.activity_function.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.btn_camera
import kotlinx.android.synthetic.main.activity_main.btn_get_app_sign_md5
import kotlinx.android.synthetic.main.activity_main.btn_get_imei
import kotlinx.android.synthetic.main.activity_main.btn_go_market
import kotlinx.android.synthetic.main.activity_main.btn_photo

class MainActivity : BaseActivity() {
    private val permissionList = arrayListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun getLayoutResID(): Int = R.layout.activity_main

    override fun initView() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        btn_custom_view.setOnClickListener {
            startNewActivity(CustomViewActivity::class.java)
        }
        btn_share_anim.setOnClickListener {
            startNewActivity(ShareActivity::class.java)
        }
//        btn_function.setOnClickListener {
//            startNewActivity(FunctionActivity::class.java)
//        }
        btn_animation.setOnClickListener({
            startNewActivity(AnimationActivity::class.java)
        })
        val function: (View) -> Unit = {
//            var intent = Intent(Intent.ACTION_VIEW)
//            intent.setData(Uri.parse("market://details?id=" + "com.changqi.yeka_app_2c"))
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            if (intent.resolveActivity(packageManager)!= null) {
//                startActivity(intent)
//            }
            LogUtils.logD(TAG, Build.BRAND)
            var x = goToSamsungMarket(this, "com.changqi.yeka_app_2c")
            ToastUtils.showShort("${x}")
            val hasAnyMarketInstalled = hasAnyMarketInstalled(this)
//            LogUtils.logD(TAG,"hasAnyMarketInstalled:$hasAnyMarketInstalled")

        }
        btn_go_market.setOnClickListener(function)
        btn_get_app_sign_md5.setOnClickListener {
            startNewActivity(AppSignMD5Activity::class.java)
        }
        btn_get_imei.setOnClickListener {
            startNewActivity(PhoneIMEIActivity::class.java)
        }
        btn_photo.setOnClickListener {
            XXPermissions.with(this)
                .permission(permissionList)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        startNewActivity(PhotoActivity::class.java)
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
//                    if(never){
//                    XXPermissions.startPermissionActivity(this@PhotoActivity, permissions)
//                    }else{
//                        ToastUtils.showShort("授权失败")
//                    }
                        val intent = Intent()
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        val packageName: String = packageName
                        intent.setAction("com.meizu.safe.security.SHOW_APPSEC");
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.putExtra("packageName", packageName);
                        startActivity(intent)
                    }

                })
        }
        btn_camera.setOnClickListener {
            XXPermissions.with(this)
                .permission(permissionList)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
//                        val getImageByCamera = Intent("android.media.action.IMAGE_CAPTURE")
                        // 图片路径？照相后图片要存储的位置
                        // 图片路径？照相后图片要存储的位置
//                        picPath = getPicName()
//                        // 指定输出路径
//                        // 指定输出路径
//                        getImageByCamera.putExtra(
//                            MediaStore.EXTRA_OUTPUT,
//                            Uri.fromFile(File(picPath))
//                        )
//                        getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
//                        startActivityForResult(getImageByCamera, 1001)
                        startNewActivity(CameraActivity::class.java)
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
//                    if(never){
//                    XXPermissions.startPermissionActivity(this@PhotoActivity, permissions)
//                    }else{
                        ToastUtils.showShort("授权失败")
//                    }
//                        val intent = Intent()
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        val packageName: String = packageName
//                        intent.setAction("com.meizu.safe.security.SHOW_APPSEC");
//                        intent.addCategory(Intent.CATEGORY_DEFAULT);
//                        intent.putExtra("packageName", packageName);
//                        startActivity(intent)
                    }

                })

        }
        btn_go_setting.setOnClickListener {
            val intent = Intent()
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.setData(Uri.parse("package:" + getPackageName()))
            startActivity(intent)
        }
        btn_go_notification.setOnClickListener {
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
//            Uri.parse("http://www.samsungapps.com/appquery/appDetail.as?appId=$packageName")
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
}