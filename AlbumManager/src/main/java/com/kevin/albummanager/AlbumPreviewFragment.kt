package com.kevin.albummanager

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kevin.albummanager.bean.AlbumData
import com.kevin.albummanager.util.AlbumUtils
import com.kevin.albummanager.util.PathUtils
import kotlinx.android.synthetic.main.fragment_album_preview.*

/**
 * Created by Kevin on 2021/1/27<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
class AlbumPreviewFragment : Fragment() {
    var handler: Handler? = null
    var runnable: Runnable? = null

    companion object {
        private val ARGS = "preview_args"
        fun newInstance(data: AlbumData): AlbumPreviewFragment {
            var fragment = AlbumPreviewFragment()
            var bundle = Bundle()
            bundle.putParcelable(ARGS, data)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_album_preview, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val albumData = arguments?.getParcelable<AlbumData>(ARGS)
        val mimeType = albumData?.mimeType
        val albumPath =if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                PathUtils.getPath(this.activity, Uri.parse(albumData?.path))
            } else {
                albumData?.path
            }
        if (AlbumUtils.isVideo(mimeType!!)) {
            ivImageViewPreview.visibility = View.VISIBLE
            videoViewPreview.visibility = View.GONE
            ivImageViewPreview.doubleTapEnabled = false
            Glide.with(this.activity)
                .applyDefaultRequestOptions(
                    RequestOptions()
                        .frame(1000)
                        .placeholder(R.drawable.ic_image_placehodler)
                )
                .asBitmap()
                .load(albumPath)
                .into(ivImageViewPreview)
//            ivImageViewPreview.loadImageIfAvailable(albumPath)
            ivPlay.visibility = View.VISIBLE
            ivPlay.setOnClickListener {
                ivPlay.visibility = View.GONE
                postLoadVideo(albumPath!!)
            }
            ToastUtils.showShort(albumPath)
        } else {
            ivPlay.visibility = View.GONE
            ivImageViewPreview.visibility = View.VISIBLE
            videoViewPreview.visibility = View.GONE
            ivImageViewPreview.doubleTapEnabled = true
            clContainer.setOnClickListener {

            }
            ivImageViewPreview.loadImageIfAvailable(albumPath)
//            if (AlbumUtils.isGif(mimeType!!)) {//共享动画的时候gif不会播放
//                postLoadImageAgain(albumPath)
//            }
        }
    }

    private fun postLoadVideo(albumPath: String) {
        ivImageViewPreview.visibility = View.GONE
        videoViewPreview.visibility = View.VISIBLE
        videoViewPreview.setVideoPath(albumPath)
        val mediaController = MediaController(context)
        videoViewPreview.setMediaController(mediaController)
        videoViewPreview.start()
    }

    private fun postLoadImageAgain(albumPath: String?) {
        ivImageViewPreview?.let {
            handler = Handler()
            runnable = Runnable { it.loadImageIfAvailable(albumPath) }
            handler!!.postDelayed(runnable!!, 500)
        }
    }

    private fun ImageView.loadImageIfAvailable(url: String?) {
        url?.let {
            Glide.with(context)
                .applyDefaultRequestOptions(
                    RequestOptions().skipMemoryCache(false)
                        .error(R.drawable.ic_image_error)
                        .placeholder(R.drawable.ic_image_placehodler)
                        .fitCenter()
                )
                .load(url)
                .into(this)
        }
    }
}