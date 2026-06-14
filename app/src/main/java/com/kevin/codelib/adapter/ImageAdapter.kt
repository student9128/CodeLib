package com.kevin.codelib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kevin.codelib.R
import com.kevin.codelib.interfaces.ItemClickLisenter
import com.kevin.codelib.databinding.ItemImageBinding
import java.io.File

/**
 * auther：lkt
 * 时间：2020/9/10 10:25
 * 功能：
 */
class ImageAdapter(list: List<String>, private val context: Context) : RecyclerView.Adapter<ImageAdapter.ImageHolder>() {

    private var listData: ArrayList<String> = list as ArrayList<String>
    private var onItemClickLisenter: ItemClickLisenter? = null

    fun setOnImageClickLisenter(lisenter: ItemClickLisenter) {
        this.onItemClickLisenter = lisenter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageHolder(binding)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.binding.image.load(File(listData[position])) {
            placeholder(R.mipmap.ic_launcher)
            error(R.mipmap.ic_launcher)
        }
        holder.binding.image.setOnClickListener { v ->
            onItemClickLisenter?.onItemClick(position, v)
        }
    }

    class ImageHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)
}
