package com.kevin.codelib.activity


import android.Manifest
import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.kevin.albummanager.AlbumManager
import com.kevin.albummanager.AlbumManagerCollection
import com.kevin.codelib.R
import com.kevin.albummanager.constant.AlbumConstant
import com.kevin.albummanager.constant.AlbumTheme
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.interfaces.OnRecyclerItemClickListener
import kotlinx.android.synthetic.main.activity_photo.*


/**
 * Created by Kevin on 2021/1/12<br/>
 * Blog:http://student9128.top/
 * 公众号：炽热的孤独心
 *
 * Describe:<br/>
 */
class PhotoActivity : BaseActivity(), OnRecyclerItemClickListener, View.OnClickListener {
    private val permissionList = arrayListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun getLayoutResID(): Int {
        return R.layout.activity_photo
    }

    override fun initView() {
        btn_photo.setOnClickListener(this)
        btn_photo2.setOnClickListener(this)
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
                XXPermissions.with(this)
                    .permission(permissionList)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                            AlbumManager.withContext(this@PhotoActivity)
                                .openAlbum(AlbumConstant.TYPE_ALL)
                                .setTheme(AlbumTheme.Red)
                                .showCameraShot(true)
                                .showSelectedWithNum(false)
                                .maxSelectedNum(3)
                                .forResult(AlbumConstant.REQUEST_CODE_ALBUM_RESULT)
                        }

                        override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                            printW("onDenied")
                        }

                    })
            }
            R.id.btn_photo2 -> {
                AlbumManager.withContext(this)
                    .openAlbum()
                    .setTheme(AlbumTheme.Green)
                    .showCameraShot(true)
                    .showSelectedWithNum(true)
                    .maxSelectedNum(10)
                    .forResult(AlbumConstant.REQUEST_CODE_ALBUM_RESULT)
            }
            R.id.btn_get_gif -> {
//                var intent = Intent(this, AlbumActivity::class.java)
//                intent.putExtra("type", "gif")
//                startActivity(intent)
                com.kevin.albummanager.AlbumManager.withContext(this)
                    .openAlbum(AlbumConstant.TYPE_GIF)
                    .setTheme(AlbumTheme.Green)
                    .forResult(AlbumConstant.REQUEST_CODE_ALBUM_RESULT)
            }
            R.id.btn_get_image -> {
//                var intent = Intent(this, AlbumActivity::class.java)
//                intent.putExtra("type", "noGif")
//                startActivity(intent)
                com.kevin.albummanager.AlbumManager.withContext(this)
                    .openAlbum(AlbumConstant.TYPE_IMAGE_NO_GIF)
                    .setTheme(AlbumTheme.Orange)
                    .forResult(AlbumConstant.REQUEST_CODE_ALBUM_RESULT)
            }
            R.id.btn_get_video -> {
//                var intent = Intent(this, AlbumActivity::class.java)
//                intent.putExtra("type", "video")
//                startActivity(intent)
                com.kevin.albummanager.AlbumManager.withContext(this)
                    .openAlbum(AlbumConstant.TYPE_VIDEO)
                    .showCameraShot(true)
                    .forResult(AlbumConstant.REQUEST_CODE_ALBUM_RESULT)
            }
            R.id.btn_photo_test -> {
                com.kevin.albummanager.AlbumManager.withContext(this)
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
                    val albumManagerCollectionInstance =
                        AlbumManagerCollection.albumManagerCollectionInstance
                    val selectionData = albumManagerCollectionInstance.getSelectionData()
//                    val albumData =
//                        AlbumManager.getAlbumDataResult(data)
//                    printD("$albumData")
                    val path = selectionData!![0].path
                    Glide.with(this)
                        .load(path)
                        .into(iv_preview)
                    tv_preview_path.text = path
                }
            }
        }
    }


}
