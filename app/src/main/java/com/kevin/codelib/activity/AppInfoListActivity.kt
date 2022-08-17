package com.kevin.codelib.activity

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.kevin.codelib.R
import com.kevin.codelib.adapter.AppInfoListAdapter
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.bean.AppInfo
import kotlinx.android.synthetic.main.activity_app_info_list.*

class AppInfoListActivity : BaseActivity() {
    var mAdapter:AppInfoListAdapter?=null
    override fun getLayoutResID(): Int = R.layout.activity_app_info_list
    override fun initView() {
        var appInfoList = ArrayList<AppInfo>()
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
            appInfo.name =label.toString()
            appInfo.icon = icon
            appInfo.versionName=l.versionName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appInfo.versionCode=l.longVersionCode
            }else{
                appInfo.versionCode= l.versionCode.toLong()
            }
            appInfo.firstInstallTime=l.firstInstallTime
            appInfo.lastUpdateTime=l.lastUpdateTime
            appInfo.isSystemApp=value.flags and ApplicationInfo.FLAG_SYSTEM==1
            appInfoList.add(appInfo)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                printD("${l.packageName},${l.versionName},${l.firstInstallTime},${l.installLocation},${l.lastUpdateTime},${l.longVersionCode},${l.splitNames},是否是系统应用：${value.flags and ApplicationInfo.FLAG_SYSTEM==1}")
            } else {
                printD("${l.packageName},${l.versionName},${l.firstInstallTime},${l.installLocation},${l.lastUpdateTime},${l.splitNames}")
            }
        }
        mAdapter= AppInfoListAdapter(this,appInfoList)
        rvRecyclerView.layoutManager = LinearLayoutManager(this)
        rvRecyclerView.adapter=mAdapter
    }
}