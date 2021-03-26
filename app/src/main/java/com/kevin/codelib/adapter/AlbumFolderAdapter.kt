package com.kevin.codelib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kevin.codelib.R
import com.kevin.codelib.bean.AlbumFolder
import com.kevin.codelib.interfaces.ItemClickLisenter
import com.kevin.codelib.interfaces.OnRecyclerItemClickListener
import kotlinx.android.synthetic.main.adapter_album_floder.view.*

/**
 * Created by Kevin on 2021/1/22<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
class AlbumFolderAdapter(var mContext: Context, var data: MutableList<AlbumFolder>) :
    RecyclerView.Adapter<AlbumFolderAdapter.AlbumFolderHolder>() {

    fun refreshData(d: MutableList<AlbumFolder>){
        data=d
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumFolderHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_album_floder, parent, false)
        return AlbumFolderHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumFolderHolder, position: Int) {
        with(data[position]) {
                Glide.with(mContext)
                    .applyDefaultRequestOptions(
                        RequestOptions().skipMemoryCache(false)
                            .error(R.mipmap.ic_launcher)
                            .placeholder(R.mipmap.ic_launcher)
                    )
                    .load(coverUri)
                    .into(holder.coverImage)
                holder.title.text = displayName
               holder.count.text = count.toString()
               holder.thisChecked.visibility = if (checked) View.VISIBLE else View.INVISIBLE
        }
        holder.container?.setOnClickListener {
            listener?.onItemClick(position, it, "albumFolder") }

    }

    override fun getItemCount(): Int = data.size

    class AlbumFolderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var coverImage = itemView.ivCover!!
        var title = itemView.tvTitle!!
        var count = itemView.tvCount!!
        var thisChecked = itemView.iv_check!!
        var container = itemView.clContainer!!
    }

    private var listener: OnRecyclerItemClickListener? = null
    fun setOnFolderItemClickListener(l: OnRecyclerItemClickListener) {
        listener = l
    }
}
