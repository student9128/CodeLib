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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kevin.codelib.R
import com.kevin.codelib.adapter.AppInfoListAdapter.*
import com.kevin.codelib.bean.AppInfo
import com.kevin.codelib.util.LogUtils
import kotlinx.android.synthetic.main.adapter_item_app_info_list.view.*
import java.text.SimpleDateFormat

class AppInfoListAdapter(var mContext: Context, var data: MutableList<AppInfo>) :
    RecyclerView.Adapter<AppInfoListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppInfoListHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_item_app_info_list, parent, false)
        return AppInfoListHolder(view)
    }

    override fun onBindViewHolder(holder: AppInfoListHolder, position: Int) {
        with(data[position]) {
            Glide.with(mContext)
                .applyDefaultRequestOptions(
                    RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform()
                        .error(R.drawable.ic_place_holder)
                        .placeholder(R.drawable.ic_place_holder)
                ).load(icon)
                .into(holder.icon)
            holder.title.text = name
            holder.describe.text =
                "包名：$packageName \n版本名称：$versionName \n版本号：$versionCode \n安装时间：${
                    formatTime(firstInstallTime)
                } \n最近更新时间：${formatTime(lastUpdateTime)} \n是否系统应用：$isSystemApp"
//            holder.uninstall.setOnClickListener {
//                Intent intent = new Intent();
//                intent.setAction("android.intent.action.DELETE");
//                intent.addCategory("android.intent.category.DEFAULT");
//                intent.setData(Uri.parse("package:" + packageName));
//                startActivityForResult(intent, 0);
//            }
                holder.openAppInfo.setOnClickListener {
                    showAppInfo(packageName)
                }
            if (canLaunchThisApp) {
                holder.launchApp.visibility = View.VISIBLE
                holder.launchApp.setOnClickListener {
//                    startMainActivity(mContext, packageName)
                    var intent = mContext.packageManager.getLaunchIntentForPackage(packageName)
                    mContext.startActivity(intent)
                }
            } else {
                holder.launchApp.visibility = View.GONE
            }


        }
    }

    fun formatTime(time: Long): String {
        var sdf = SimpleDateFormat("yyyy年MM月dd日")
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
    class AppInfoListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon = itemView.ivIcon
        var title = itemView.tvAppName
        var describe = itemView.tvDescribe
        var uninstall = itemView.btnUninstall
        var openAppInfo = itemView.btnOpenInfo
        var launchApp = itemView.btnLaunch
    }
}