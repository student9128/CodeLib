package com.kevin.codelib.activity


import android.content.Intent
import android.view.View
import com.kevin.codelib.R
import com.kevin.codelib.base.BaseActivity
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
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_photo -> {
                var intent = Intent(this,AlbumActivity::class.java)
                intent.putExtra("type", "all")
                startActivity(intent)
            }
            R.id.btn_get_gif -> {
                var intent = Intent(this,AlbumActivity::class.java)
                intent.putExtra("type", "gif")
                startActivity(intent)
            }
            R.id.btn_get_image -> {
                var intent = Intent(this,AlbumActivity::class.java)
                intent.putExtra("type", "noGif")
                startActivity(intent)
            }
            R.id.btn_get_video -> {
                var intent = Intent(this,AlbumActivity::class.java)
                intent.putExtra("type", "video")
                startActivity(intent)
            }
        }
    }


}
