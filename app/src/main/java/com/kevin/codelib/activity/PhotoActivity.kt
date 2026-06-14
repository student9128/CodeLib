package com.kevin.codelib.activity


import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import coil.load
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import com.kevin.albummanager.util.PermissionUtils
import com.kevin.albummanager.AlbumManager
import com.kevin.albummanager.AlbumManagerCollection
import com.kevin.codelib.R
import com.kevin.albummanager.constant.AlbumConstant
import com.kevin.albummanager.constant.AlbumTheme
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.interfaces.OnRecyclerItemClickListener
import com.kevin.codelib.databinding.ActivityPhotoBinding


/**
 * Created by Kevin on 2021/1/12<br/>
 * Blog:http://student9128.top/
 * 公众号：零點壹度ideality
 *
 * Describe:<br/>
 */
class PhotoActivity : BaseActivity(), OnRecyclerItemClickListener, View.OnClickListener {
    private lateinit var binding: ActivityPhotoBinding

    private var permissionList = PermissionUtils.getStorageAndCameraPermissions()

    override fun getLayoutView(): View? {
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        binding.btnPhoto.setOnClickListener(this)
        binding.btnPhoto2.setOnClickListener(this)
        binding.btnGetGif.setOnClickListener(this)
        binding.btnGetImage.setOnClickListener(this)
        binding.btnGetVideo.setOnClickListener(this)
        binding.btnPhotoTest.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_photo -> {
//                var intent = Intent(this, AlbumActivity::class.java)
//                intent.putExtra("type", "all")
//                startActivity(intent)
                XXPermissions.with(this)
                    .permissions(permissionList)
                    .request(object : OnPermissionCallback {
                        override fun onResult(grantedList: List<IPermission>, deniedList: List<IPermission>) {
                            if (deniedList.isEmpty()) {
                                AlbumManager.withContext(this@PhotoActivity)
                                    .openAlbum(AlbumConstant.TYPE_ALL)
                                    .setTheme(AlbumTheme.Red)
                                    .showCameraShot(true)
                                    .showSelectedWithNum(false)
                                    .maxSelectedNum(3)
                                    .forResult(AlbumConstant.REQUEST_CODE_ALBUM_RESULT)
                            } else {
                                printW("onDenied")
                            }
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
                    binding.ivPreview.load(path)
                    binding.tvPreviewPath.text = path
                }
            }
        }
    }


}
