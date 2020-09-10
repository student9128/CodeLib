package com.kevin.codelib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kevin.codelib.R
import com.kevin.codelib.adapter.ImageAdapter.ImageHolder
import com.kevin.codelib.interfaces.ItemClickLisenter
import kotlinx.android.synthetic.main.item_image.view.*
import java.io.File

/**
 * auther：lkt
 * 时间：2020/9/10 10:25
 * 功能：
 */
class ImageAdapter : RecyclerView.Adapter<ImageHolder> {

    var listData: ArrayList<String> = ArrayList()
    var mRequestOptions: RequestOptions? = null
    var context: Context? = null
    var onItemClickLisenter: ItemClickLisenter? = null

    fun setOnImageClickLisenter(lisenter: ItemClickLisenter) {
        this.onItemClickLisenter = lisenter
    }

    constructor(list: List<String>, context: Context) : super() {
        this.context = context
        listData = list as ArrayList<String>
        mRequestOptions = RequestOptions()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .error(R.mipmap.ic_launcher)
            .placeholder(R.mipmap.ic_launcher)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageHolder(view)
    }

    override fun getItemCount(): Int {
        return listData.size as Int
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        Glide.with(context)
            .applyDefaultRequestOptions(mRequestOptions)
            .load(File(listData.get(position)))
            .into((holder.imageView))
        holder.imageView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                onItemClickLisenter?.onItemClick(position, v)
            }

        })
    }

    class ImageHolder : RecyclerView.ViewHolder {
        var imageView: ImageView

        constructor(view: View) : super(view) {
            imageView = view.image
        }
    }


}
