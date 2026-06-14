package com.kevin.codelib.activity

import android.content.ContentResolver
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.base.IPermission
import com.kevin.albummanager.util.PermissionUtils
import com.kevin.codelib.R
import com.kevin.codelib.adapter.ImageAdapter
import com.kevin.albummanager.BaseActivity
import com.kevin.codelib.interfaces.ItemClickLisenter
import com.kevin.codelib.databinding.ActivityShareBinding

/**
 * 1.在ImageActivity的布局文件的根节点添加属性android:transitionName="@string/share_anim"
 * 2.在开启界面是生成option=ActivityOptionsCompat将共享的view作为ActivityOptionsCompat的参数
 * 3.开启startActivity(intent, option.toBundle())
 */
class ShareActivity : com.kevin.albummanager.BaseActivity() {
    private lateinit var binding: ActivityShareBinding

    var list: ArrayList<String> = ArrayList()
    var adapter = ImageAdapter(list, this)

    override fun getLayoutView(): View {
        binding = ActivityShareBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)

        binding.recyclerView.adapter = adapter

        adapter.setOnImageClickLisenter(object : ItemClickLisenter {
            override fun onItemClick(position: Int, view: View?) {
                var intent: Intent = Intent(this@ShareActivity, ImageActivity::class.java)
                intent.putExtra("filePath", list.get(position))

                var option: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@ShareActivity,
                        view!!,
                        getString(R.string.share_anim)
                    )
                startActivity(intent, option.toBundle())
            }
        })

        XXPermissions.with(this)
            .permissions(PermissionUtils.getStorageAndCameraPermissions())
            .request(object : OnPermissionCallback {
                override fun onResult(grantedList: List<IPermission>, deniedList: List<IPermission>) {
                    if (deniedList.isEmpty()) {
                        query(contentResolver)
                    }
                }
            })
    }


    private fun query(resolver: ContentResolver) {
        object : Thread() {
            override fun run() {
                super.run()
                val cursor = resolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    MediaStore.Images.Media.DEFAULT_SORT_ORDER
                )
                while (cursor!!.moveToNext()) {
                    val string =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                    list.add(string)
                }
                if (!cursor.isClosed) {
                    cursor.close()
                }
                runOnUiThread {
                    Log.d("list.size=adapter", "=" + list.size)
                    adapter.notifyDataSetChanged()
                }
            }
        }.start()
    }
}
