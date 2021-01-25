package com.kevin.codelib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kevin.codelib.R
import com.kevin.codelib.bean.AlbumData
import com.kevin.codelib.interfaces.ItemClickLisenter
import com.kevin.codelib.interfaces.OnRecyclerItemClickListener
import com.kevin.codelib.util.AlbumUtils
import com.kevin.codelib.util.DisplayUtils
import com.kevin.codelib.util.LogUtils
import kotlinx.android.synthetic.main.activity_toggle_view.view.*
import kotlinx.android.synthetic.main.adapter_album.view.*

/**
 * Created by Kevin on 2021/1/20<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
class AlbumAdapter(var mContext: Context, var data: MutableList<AlbumData>) :
    RecyclerView.Adapter<AlbumAdapter.AlbumHolder>() {
    val screenWidth = DisplayUtils.getScreenWidth(mContext)
    val width = (screenWidth - 30) / 4

    fun refreshData(d: MutableList<AlbumData>) {
        data = d
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_album, parent, false)
        val layoutParams = view.ivImageView.layoutParams
        layoutParams.width = width
        layoutParams.height = width
        view.ivImageView.layoutParams = layoutParams
        return AlbumHolder(view)

    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        val albumData = data[position]
        if (AlbumUtils.isVideo(albumData.mimeType)) {
            holder.tvDuration.text=AlbumUtils.parseTime(albumData.duration)
        }
        LogUtils.logD("AlbumPre",albumData.path+"")
        Glide.with(mContext)
            .applyDefaultRequestOptions(
                RequestOptions().skipMemoryCache(false)
                    .error(R.drawable.ic_image_error)
                    .placeholder(R.drawable.ic_image_placehodler)
            )
            .load(albumData.path)
            .into(holder.imageView)
        holder.imageView.setOnClickListener {
            listener?.onItemClick(position, it, "albumData")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class AlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView = itemView.ivImageView!!
        var tvDuration=itemView.tv_duration

    }

    private var listener: OnRecyclerItemClickListener? = null
    fun setOnItemClickListener(l: OnRecyclerItemClickListener) {
        listener = l
    }
}