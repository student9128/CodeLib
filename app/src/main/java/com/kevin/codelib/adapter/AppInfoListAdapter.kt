package com.kevin.codelib.adapter

import android.content.Context
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
        }
    }

    fun formatTime(time: Long): String {
        var sdf = SimpleDateFormat("yyyy年MM月dd日")
        return sdf.format(time)
    }

    override fun getItemCount(): Int = data.size
    class AppInfoListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon = itemView.ivIcon
        var title = itemView.tvAppName
        var describe = itemView.tvDescribe
    }
}