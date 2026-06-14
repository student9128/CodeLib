package com.kevin.codelib.adapter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kevin.codelib.R
import com.kevin.codelib.bean.AppInfo
import com.kevin.codelib.util.LogUtils
import com.kevin.codelib.databinding.AdapterItemAppInfoListBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AppInfoListAdapter(var mContext: Context, var data: MutableList<AppInfo>) :
    RecyclerView.Adapter<AppInfoListAdapter.AppInfoListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppInfoListHolder {
        val binding = AdapterItemAppInfoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppInfoListHolder(binding)
    }

    override fun onBindViewHolder(holder: AppInfoListHolder, position: Int) {
        with(data[position]) {
            holder.binding.ivIcon.load(icon) {
                placeholder(R.drawable.ic_place_holder)
                error(R.drawable.ic_place_holder)
                crossfade(false)
            }
            holder.binding.tvAppName.text = name
            holder.binding.tvDescribe.text =
                "包名：$packageName \n版本名称：$versionName \n版本号：$versionCode \n安装时间：${
                    formatTime(firstInstallTime)
                } \n最近更新时间：${formatTime(lastUpdateTime)} \n是否系统应用：$isSystemApp"

            holder.binding.btnOpenInfo.setOnClickListener {
                showAppInfo(packageName)
            }
            if (canLaunchThisApp) {
                holder.binding.btnLaunch.visibility = View.VISIBLE
                holder.binding.btnLaunch.setOnClickListener {
                    var intent = mContext.packageManager.getLaunchIntentForPackage(packageName)
                    if (intent != null) {
                        mContext.startActivity(intent)
                    }
                }
            } else {
                holder.binding.btnLaunch.visibility = View.GONE
            }


        }
    }

    fun formatTime(time: Long): String {
        var sdf = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
        return sdf.format(time)
    }

    /**
     * 打开指定包名的App应用信息界面
     */
    fun showAppInfo(packageName: String) {
        val intent = Intent()
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS")
        intent.setData(Uri.parse("package:$packageName"))
        mContext.startActivity(intent)
    }

    fun startMainActivity(context: Context, packageName: String) {
        val pm: PackageManager = context.packageManager
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = pm.getPackageInfo(packageName, 0)
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setPackage(packageInfo.packageName)
            val apps: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)
            val resolveInfo: ResolveInfo = apps.iterator().next()
            if (resolveInfo != null) {
                val className: String = resolveInfo.activityInfo.name
                intent.component = ComponentName(packageName, className)
                context.startActivity(intent)
            }else{
                LogUtils.logD("AppInfoList","resolveInfo is null")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            LogUtils.logD("AppInfoList","exception:${e.toString()}")
        }
    }

    override fun getItemCount(): Int = data.size

    class AppInfoListHolder(val binding: AdapterItemAppInfoListBinding) : RecyclerView.ViewHolder(binding.root)
}
