package com.kevin.codelib.activity

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.kevin.codelib.R
import com.kevin.codelib.adapter.AppInfoListAdapter
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.bean.AppInfo
import kotlinx.android.synthetic.main.activity_app_info_list.*

class AppInfoListActivity : BaseActivity() {
    var mAdapter: AppInfoListAdapter? = null
    private var mHandler: Handler? = null
    var appInfoList = ArrayList<AppInfo>()
    override fun getLayoutResID(): Int = R.layout.activity_app_info_list
    override fun initView() {
        Thread {
            appInfoList = getAppList()
            var msg = Message.obtain()
            msg.what = 1
            mHandler?.sendMessage(msg)
        }.start()
        mHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    1 -> {
                        mAdapter = AppInfoListAdapter(this@AppInfoListActivity, appInfoList)
                        rvRecyclerView.layoutManager = LinearLayoutManager(this@AppInfoListActivity)
                        rvRecyclerView.adapter = mAdapter
                        ll_progress.visibility = View.GONE
                        rvRecyclerView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun getAppList(): ArrayList<AppInfo> {
        var appInfoList = ArrayList<AppInfo>()
        var sysAppInfoList = ArrayList<AppInfo>()
        val list =
            packageManager.getInstalledPackages(0)
        printE("list.length = ${list.size}")
        for (l in list) {
            var appInfo = AppInfo(icon = getDrawable(R.drawable.ic_place_holder))
            val value = l.applicationInfo
            //            value.loadLogo()
            //            value.minSdkVersion
            if (value != null) {
                printI("${value.name},${value.icon},${value.packageName}")
            }
            val icon = value.loadIcon(packageManager)
            var label = value.loadLabel(packageManager)
            appInfo.packageName = value.packageName
            appInfo.name = label.toString()
            appInfo.icon = icon
            appInfo.versionName = l.versionName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appInfo.versionCode = l.longVersionCode
            } else {
                appInfo.versionCode = l.versionCode.toLong()
            }
            appInfo.firstInstallTime = l.firstInstallTime
            appInfo.lastUpdateTime = l.lastUpdateTime
            val isSystemApp = value.flags and ApplicationInfo.FLAG_SYSTEM == 1
            appInfo.isSystemApp = isSystemApp
            if (isSystemApp) {
                sysAppInfoList.add(appInfo)
            } else {
                appInfo.canLaunchThisApp = true
                appInfoList.add(appInfo)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                printD("${l.packageName},${l.versionName},${l.firstInstallTime},${l.installLocation},${l.lastUpdateTime},${l.longVersionCode},${l.splitNames},是否是系统应用：$isSystemApp")
            } else {
                printD("${l.packageName},${l.versionName},${l.firstInstallTime},${l.installLocation},${l.lastUpdateTime},${l.splitNames}")
            }
        }
        var intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfo =
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        printW("resolvePackageName=${resolveInfo.size},,${sysAppInfoList.size}")
        for (r in resolveInfo) {
            val resolvePackageName = r.activityInfo.packageName
            printW("resolvePackageName=$resolvePackageName")
            for (i in sysAppInfoList) {
                if (resolvePackageName.equals(i.packageName)) {
                    i.canLaunchThisApp = true
                }
            }
        }
        appInfoList.addAll(sysAppInfoList)
        return appInfoList
    }

    override fun onResume() {
        super.onResume()
    }
}