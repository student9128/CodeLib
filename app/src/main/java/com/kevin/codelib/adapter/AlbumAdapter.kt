package com.kevin.codelib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kevin.codelib.R
import com.kevin.codelib.bean.AlbumData
import com.kevin.codelib.interfaces.OnRecyclerItemClickListener
import com.kevin.codelib.util.AlbumUtils
import com.kevin.codelib.util.DisplayUtils
import kotlinx.android.synthetic.main.adapter_album.view.*

/**
 * Created by Kevin on 2021/1/20<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
class AlbumAdapter(var mContext: Context, var data: MutableList<AlbumData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val screenWidth = DisplayUtils.getScreenWidth(mContext)
    val width = (screenWidth - 30) / 4
    private val TYPE_CONTENT = 0
    private val TYPE_FOOTER = 1
    private val TYPE_EMPTY = 2
    fun refreshData(d: MutableList<AlbumData>) {
        data = d
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_CONTENT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_album, parent, false)
            val layoutParams = view.ivImageView.layoutParams
            layoutParams.width = width
            layoutParams.height = width
            view.ivImageView.layoutParams = layoutParams
            return AlbumHolder(view)
        }else if(viewType==TYPE_FOOTER){
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_album_bottom, parent, false)
            return FooterHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_album_empty, parent, false)
            return EmptyHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_CONTENT -> {
                val albumHolder = holder as AlbumHolder
                val albumData = data[position]
                if (AlbumUtils.isVideo(albumData.mimeType)) {
                    albumHolder.tvDuration.text = AlbumUtils.parseTime(albumData.duration)
                }
                with(albumHolder) {
                    Glide.with(mContext)
                        .applyDefaultRequestOptions(
                            RequestOptions().skipMemoryCache(false)
                                .error(R.drawable.ic_image_error)
                                .placeholder(R.drawable.ic_image_placehodler)
                        )
                        .load(albumData.path)
                        .into(imageView)
                    imageView.setOnClickListener {
                        listener?.onItemClick(position, it, "albumData")
                    }
                    llSelectView.setOnClickListener {
                        listener?.onChildItemClick(position, it, "selectView")
                    }
                    tvSelectView.isEnabled = albumData.selected
                    if (albumData.selected) {
                        tvSelectView.text = albumData.selectedIndex.toString()
                    } else {
                        tvSelectView.text = ""
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            data.size == 0 -> {
                TYPE_EMPTY
            }
            position == data.size -> {
                TYPE_FOOTER
            }
            else -> {
                TYPE_CONTENT
            }
        }
    }

    class AlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView = itemView.ivImageView!!
        var tvDuration = itemView.tv_duration
        var tvSelectView = itemView.tv_select_view
        var llSelectView = itemView.ll_select_view

    }
    class FooterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
    class EmptyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


    private var listener: OnRecyclerItemClickListener? = null
    fun setOnItemClickListener(l: OnRecyclerItemClickListener) {
        listener = l
    }
}