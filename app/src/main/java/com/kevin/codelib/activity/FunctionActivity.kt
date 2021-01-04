package com.kevin.codelib.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.kevin.codelib.R
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.util.AppUtils
import com.kevin.codelib.util.LogUtils
import kotlinx.android.synthetic.main.activity_function.*


/**
 * Created by Kevin on 2020/11/12<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
class FunctionActivity:BaseActivity() {
    override fun getLayoutResID(): Int {
        return R.layout.activity_function
    }

    override fun initView() {
        val function: (View) -> Unit = {
//            var intent = Intent(Intent.ACTION_VIEW)
//            intent.setData(Uri.parse("market://details?id=" + "com.changqi.yeka_app_2c"))
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            if (intent.resolveActivity(packageManager)!= null) {
//                startActivity(intent)
//            }
            LogUtils.logD(TAG,Build.BRAND)
         var x=  goToSamsungMarket(this, "com.changqi.yeka_app_2c")
            ToastUtils.showShort("${x}")
            val hasAnyMarketInstalled = hasAnyMarketInstalled(this)
//            LogUtils.logD(TAG,"hasAnyMarketInstalled:$hasAnyMarketInstalled")

        }
        btn_go_market.setOnClickListener(function)
        btn_get_app_sign_md5.setOnClickListener {
           startNewActivity(AppSignMD5Activity::class.java )
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