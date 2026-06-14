package com.kevin.albummanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kevin.albummanager.OnRecyclerItemClickListener
import com.kevin.albummanager.R
import com.kevin.albummanager.bean.AlbumFolder

@Deprecated("AlbumManager now uses Compose. This adapter is kept only for binary/source compatibility.")
class AlbumFolderAdapter(private val context: Context, private var data: MutableList<AlbumFolder>) :
    RecyclerView.Adapter<AlbumFolderAdapter.AlbumFolderHolder>() {
    private var listener: OnRecyclerItemClickListener? = null

    fun refreshData(d: MutableList<AlbumFolder>) {
        data = d
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumFolderHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_album_floder, parent, false)
        return AlbumFolderHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumFolderHolder, position: Int) {
        val folder = data[position]
        holder.coverImage.load(folder.coverUri ?: folder.coverUriString) {
            placeholder(R.drawable.ic_image_placehodler)
            error(R.drawable.ic_image_error)
            size(120)
        }
        holder.title.text = folder.displayName
        holder.count.text = folder.count.toString()
        holder.checked.visibility = if (folder.checked) View.VISIBLE else View.INVISIBLE
        holder.container.setOnClickListener {
            listener?.onItemClick(position, it, "albumFolder")
        }
    }

    override fun getItemCount(): Int = data.size

    fun setOnFolderItemClickListener(l: OnRecyclerItemClickListener) {
        listener = l
    }

    class AlbumFolderHolder(view: View) : RecyclerView.ViewHolder(view) {
        val coverImage: ImageView = view.findViewById(R.id.ivCover)
        val title: TextView = view.findViewById(R.id.tvTitle)
        val count: TextView = view.findViewById(R.id.tvCount)
        val checked: ImageView = view.findViewById(R.id.iv_check)
        val container: View = view.findViewById(R.id.clContainer)
    }
}
