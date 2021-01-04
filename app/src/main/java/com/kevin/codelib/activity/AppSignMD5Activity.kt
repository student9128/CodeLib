package com.kevin.codelib.activity

import android.text.TextUtils
import com.blankj.utilcode.util.ToastUtils
import com.kevin.codelib.R
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.util.AppUtils
import kotlinx.android.synthetic.main.activity_app_sign_md5.*

/**
 * Created by Kevin on 2021/1/4<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
class AppSignMD5Activity : BaseActivity() {
    override fun getLayoutResID(): Int {
        return R.layout.activity_app_sign_md5
    }

    override fun initView() {
        val installedPackages = packageManager.getInstalledPackages(0)
        btn_get.setOnClickListener {
            val packageName = etPackageName.text.toString().trim()
            if (TextUtils.isEmpty(packageName)) {
                ToastUtils.showShort("请输入包名")
            } else {
                var i = 0
                for (packageInfo in installedPackages) {
                    if (packageName == packageInfo.packageName) {
                        val signMd5Str = AppUtils.getSignMd5Str(packageName)
                        tvSignMD5.text = signMd5Str
                        i += 1
                    }
                }
                if (i == 0) {
                    ToastUtils.showShort("找不到相关应用")
                }
            }

        }
    }
}