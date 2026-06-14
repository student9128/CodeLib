package com.kevin.codelib.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import com.kevin.albummanager.util.PermissionUtils
import com.kevin.codelib.R
import com.kevin.albummanager.BaseActivity
import com.kevin.codelib.util.LogUtils
import com.kevin.codelib.databinding.ActivityFunctionBinding


/**
 * Created by Kevin on 2020/11/12<br/>
 * Blog:http://student9128.top/
 * 公众号：零點壹度ideality
 * Describe:<br/>
 */
class FunctionActivity : BaseActivity() {
    private lateinit var binding: ActivityFunctionBinding

    private var permissionList = PermissionUtils.getStorageAndCameraPermissions()

    override fun getLayoutView(): View {
        binding = ActivityFunctionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
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
        binding.btnGoMarket.setOnClickListener(function)
        binding.btnGetAppSignMd5.setOnClickListener {
            startNewActivity(AppSignMD5Activity::class.java)
        }
        binding.btnGetImei.setOnClickListener {
            startNewActivity(PhoneIMEIActivity::class.java)
        }
        binding.btnPhoto.setOnClickListener {
            XXPermissions.with(this)
                .permissions(permissionList)
                .request(object : OnPermissionCallback {
                    override fun onResult(grantedList: List<IPermission>, deniedList: List<IPermission>) {
                        if (deniedList.isEmpty()) {
                            startNewActivity(PhotoActivity::class.java)
                        } else {
                            val intent = Intent()
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            val packageName: String = packageName
                            intent.setAction("com.meizu.safe.security.SHOW_APPSEC");
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.putExtra("packageName", packageName);
                            startActivity(intent)
                        }
                    }

                })
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == XXPermissions.REQUEST_CODE) {
            if (XXPermissions.isGrantedPermissions(this, permissionList)) {
                ToastUtils.showShort("获取授权");
            } else {
                ToastUtils.showShort("没有获取授权");
            }
        }
    }

}
