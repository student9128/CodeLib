package com.kevin.albummanager.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kevin.albummanager.OnRecyclerItemClickListener
import com.kevin.albummanager.bean.AlbumData

@Deprecated("AlbumManager now uses Compose. This adapter is kept only for binary/source compatibility.")
class AlbumAdapter(
    private val context: Context,
    private var data: MutableList<AlbumData>
) : RecyclerView.Adapter<AlbumAdapter.EmptyHolder>() {
    private var listener: OnRecyclerItemClickListener? = null

    fun addShotAlbum(albumData: AlbumData) {
        data.add(0, albumData)
        notifyItemInserted(0)
    }

    fun refreshDataItem(position: Int) {
        notifyItemChanged(position)
    }

    fun refreshSelectData() = Unit

    fun refreshData(d: MutableList<AlbumData>) {
        data = d
        notifyDataSetChanged()
    }

    fun refreshData() {
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(l: OnRecyclerItemClickListener) {
        listener = l
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmptyHolder {
        return EmptyHolder(View(context))
    }

    override fun onBindViewHolder(holder: EmptyHolder, position: Int) = Unit

    override fun getItemCount(): Int = data.size

    class EmptyHolder(view: View) : RecyclerView.ViewHolder(view)
}
