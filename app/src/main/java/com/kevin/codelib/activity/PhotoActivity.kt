package com.kevin.codelib.activity


import android.content.Intent
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.kevin.codelib.AlbumManager
import com.kevin.codelib.R
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.bean.AlbumData
import com.kevin.codelib.constant.AlbumConstant
import com.kevin.codelib.constant.AlbumTheme
import com.kevin.codelib.interfaces.OnRecyclerItemClickListener
import kotlinx.android.synthetic.main.activity_function.*
import kotlinx.android.synthetic.main.activity_function.btn_photo
import kotlinx.android.synthetic.main.activity_photo.*


/**
 * Created by Kevin on 2021/1/12<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
class PhotoActivity : BaseActivity(), OnRecyclerItemClickListener, View.OnClickListener {

    override fun getLayoutResID(): Int {
        return R.layout.activity_photo
    }

    override fun initView() {
        btn_photo.setOnClickListener(this)
        btn_get_gif.setOnClickListener(this)
        btn_get_image.setOnClickListener(this)
        btn_get_video.setOnClickListener(this)
        btn_photo_test.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_photo -> {
//                var intent = Intent(this, AlbumActivity::class.java)
//                intent.putExtra("type", "all")
//                startActivity(intent)
                AlbumManager.withContext(this)
                    .openAlbum(AlbumConstant.TYPE_ALL)
                    .setTheme(AlbumTheme.Red)
                    .showSelectedWithNum(false)
                    .maxSelectedNum(3)
                    .forResult(AlbumConstant.REQUEST_CODE_ALBUM_RESULT)
            }
            R.id.btn_get_gif -> {
//                var intent = Intent(this, AlbumActivity::class.java)
//                intent.putExtra("type", "gif")
//                startActivity(intent)
                AlbumManager.withContext(this)
                    .openAlbum(AlbumConstant.TYPE_GIF)
                    .setTheme(AlbumTheme.Green)
                    .forResult(AlbumConstant.REQUEST_CODE_ALBUM_RESULT)
            }
            R.id.btn_get_image -> {
//                var intent = Intent(this, AlbumActivity::class.java)
//                intent.putExtra("type", "noGif")
//                startActivity(intent)
                AlbumManager.withContext(this)
                    .openAlbum(AlbumConstant.TYPE_IMAGE_NO_GIF)
                    .setTheme(AlbumTheme.Orange)
                    .forResult(AlbumConstant.REQUEST_CODE_ALBUM_RESULT)
            }
            R.id.btn_get_video -> {
//                var intent = Intent(this, AlbumActivity::class.java)
//                intent.putExtra("type", "video")
//                startActivity(intent)
                AlbumManager.withContext(this)
                    .openAlbum(AlbumConstant.TYPE_VIDEO)
                    .forResult(AlbumConstant.REQUEST_CODE_ALBUM_RESULT)
            }
            R.id.btn_photo_test -> {
                AlbumManager.withContext(this)
                    .openAlbum(AlbumConstant.TYPE_ALL)
                    .forResult(AlbumConstant.REQUEST_CODE_ALBUM_RESULT)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                AlbumConstant.REQUEST_CODE_ALBUM_RESULT -> {
                    val albumData =
                        AlbumManager.getAlbumDataResult(data)
                    printD("$albumData")
                    val path = albumData!![0].path
                    Glide.with(this)
                        .load(path)
                        .into(iv_preview)
                    tv_preview_path.text = path
                }
            }
        }
    }


}
