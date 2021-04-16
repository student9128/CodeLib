package com.kevin.albummanager.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kevin.albummanager.AlbumManagerConfig
import com.kevin.albummanager.OnRecyclerItemClickListener
import com.kevin.albummanager.R
import com.kevin.albummanager.bean.AlbumData
import com.kevin.albummanager.util.AlbumUtils
import com.kevin.albummanager.util.DisplayUtils
import com.kevin.albummanager.util.LogUtils
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
    private val TYPE_CMAERA = 3
    private var clickedImagePath: String? = null
    private val albumManagerCollectionInstance =
        com.kevin.albummanager.AlbumManagerCollection.albumManagerCollectionInstance
    private val albumManagerConfig: com.kevin.albummanager.AlbumManagerConfig = com.kevin.albummanager.AlbumManagerConfig.albumManagerConfig
    val requestOptionVideo = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .skipMemoryCache(false)
        .error(R.drawable.ic_image_error)
        .placeholder(R.drawable.ic_image_placehodler)
    val requestOptionImage = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .skipMemoryCache(false)
        .centerCrop()
        .dontAnimate()
        .dontTransform()
        .error(R.drawable.ic_image_error)
        .placeholder(R.drawable.ic_image_placehodler)

    fun addShotAlbum(albumData: AlbumData) {
        data.add(1, albumData)
        notifyItemInserted(1)
    }

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
            val llLayoutParams = view.ll_modal.layoutParams
            llLayoutParams.width = width
            llLayoutParams.height = width
            view.ll_modal.layoutParams = llLayoutParams
            return AlbumHolder(view)
        } else if (viewType == TYPE_FOOTER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_album_bottom, parent, false)
            return FooterHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_album_empty, parent, false)
            return EmptyHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is AlbumHolder) {
            val albumHolder = holder as AlbumHolder
            val albumData = data[position]
            LogUtils.logD("Hello", albumData.toString())
            if (albumData.showCameraPlaceholder) {
                albumHolder.cameraContainer.visibility = View.VISIBLE
                albumHolder.contentContainer.visibility = View.GONE
                albumHolder.cameraContainer.setOnClickListener {
                    if (albumManagerConfig.maxSelectedNum > albumManagerCollectionInstance.getSelectedAlbumDataSize()) {
                        listener?.onItemClick(position, it, "camera")
                    } else {
                        ToastUtils.showShort("最多只能选择${albumManagerConfig.maxSelectedNum}个文件")
                    }
                }
            } else {
                albumHolder.cameraContainer.visibility = View.GONE
                albumHolder.contentContainer.visibility = View.VISIBLE
                if (AlbumUtils.isVideo(albumData.mimeType)) {
                    albumHolder.tvDuration.text = AlbumUtils.parseTime(albumData.duration)
                }
                with(albumHolder) {

                    Glide.with(mContext)
                        .applyDefaultRequestOptions(
                            if (AlbumUtils.isVideo(albumData.mimeType)) requestOptionVideo else requestOptionImage
                        )
                        .asBitmap()
                        .load(albumData.path)
                        .into(imageView)
                    imageView.setOnClickListener {
                        listener?.onItemClick(position, it, "albumData")
                    }
                    llSelectView.setOnClickListener {
                        clickedImagePath = albumData.path
                        if (albumManagerCollectionInstance.isSelected(albumData)) {
                            albumManagerCollectionInstance.removeSelectedAlbumData(albumData)
                        } else {
                            if (albumManagerConfig.maxSelectedNum > albumManagerCollectionInstance.getSelectedAlbumDataSize()) {
                                albumManagerCollectionInstance.addSelectedAlbumData(albumData)
                            } else {
                                ToastUtils.showShort("最多只能选择${albumManagerConfig.maxSelectedNum}个文件")
                            }
                        }
                        listener?.onChildItemClick(position, it, "selectView")
                    }
                    if (AlbumUtils.isVideo(albumData.mimeType)) {
                        albumHolder.tvDuration.text = AlbumUtils.parseTime(albumData.duration)
                    } else {
                        albumHolder.tvDuration.text = ""
                    }
                    tvSelectView.isEnabled = albumManagerCollectionInstance.isSelected(albumData)
                    if (!albumManagerConfig.canSelected) {
                        if (!albumHolder.tvSelectView.isEnabled) {
                            llModal.visibility = View.GONE
                        } else {
                            llModal.visibility = View.VISIBLE
                            if (AlbumManagerConfig.albumManagerConfig.showNum) {
                                tvSelectView.text = albumData.selectedIndex.toString()
                            } else {
                                AlbumUtils.formatCustomFont(mContext, tvSelectView)
                                tvSelectView.textSize = 10f
                                tvSelectView.text = mContext.getString(R.string.icon_tick)
                            }
                            if (albumData.path == clickedImagePath) {
                                showAnim()
                            }
                        }
                    } else {
                        if (albumManagerCollectionInstance.isSelected(albumData)) {

                            llModal.visibility = View.VISIBLE
                            if (com.kevin.albummanager.AlbumManagerConfig.albumManagerConfig.showNum) {
//                            tvSelectView.text = albumData.selectedIndex.toString()
                                tvSelectView.text =
                                    albumManagerCollectionInstance.checkedNum(albumData).toString()
                            } else {
                                AlbumUtils.formatCustomFont(mContext, tvSelectView)
                                tvSelectView.textSize = 10f
                                tvSelectView.text = mContext.getString(R.string.icon_tick)
                            }
                            if (albumData.path == clickedImagePath) {
                                showAnim()
                            }
                        } else {
                            llModal.visibility = View.GONE
                            tvSelectView.text = ""
                        }
                    }
                }
            }

        }
    }

    private fun AlbumHolder.showAnim() {
        var set = AnimatorSet()
        set.playTogether(
            ObjectAnimator.ofFloat(llSelectView, "scaleX", 0.9f, 1.15f, 1f),
            ObjectAnimator.ofFloat(llSelectView, "scaleY", 0.9f, 1.15f, 1f)
        )
        set.interpolator = AccelerateDecelerateInterpolator()
        set.duration = 150

        set.start()
        clickedImagePath = ""
    }

    override fun getItemCount(): Int {
        return data.size + 4//4列的，底部加空白
    }

    override fun getItemViewType(position: Int): Int {
        if (data.size == 0) {
            return TYPE_EMPTY
        } else if (position >= data.size) {
            return TYPE_FOOTER
        } else {
            return TYPE_CONTENT
        }
//        return when {
//            data.size == 0 -> {
//                TYPE_EMPTY
//            }
//            position == data.size -> {
//                TYPE_FOOTER
//            }
//            else -> {
//                TYPE_CONTENT
//            }
//        }
    }

    class AlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView = itemView.ivImageView!!
        var tvDuration = itemView.tv_duration
        var tvSelectView = itemView.tv_select_view
        var llSelectView = itemView.ll_select_view
        var llModal = itemView.ll_modal
        var contentContainer = itemView.clContentContainer
        var cameraContainer = itemView.clCameraContainer

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