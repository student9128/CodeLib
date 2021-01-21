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
import com.kevin.codelib.util.DisplayUtils
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
    val width=(screenWidth-30)/4
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_album, parent, false)
        val layoutParams = view.ivImageView.layoutParams
        layoutParams.width=width
        layoutParams.height=width
        view.ivImageView.layoutParams=layoutParams
        return AlbumHolder(view)

    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        Glide.with(mContext)
            .applyDefaultRequestOptions(
                RequestOptions().skipMemoryCache(false)
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
            )
            .load(data[position].path)
            .into(holder.imageView)
        holder.imageView.setOnClickListener {
            listener?.onItemClick(position, it)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class AlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView = itemView.ivImageView!!

    }

    private var listener: ItemClickLisenter? = null
    fun setOnItemClickListener(l: ItemClickLisenter) {
        listener = l
    }
}