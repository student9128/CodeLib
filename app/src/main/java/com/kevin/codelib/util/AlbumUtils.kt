package com.kevin.codelib.util

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.kevin.codelib.R
import com.kevin.codelib.constant.AlbumTheme
import java.lang.NullPointerException

/**
 * Created by Kevin on 2021/1/22<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
object AlbumUtils {
    const val TAG = "AlbumUtils"
    fun isVideo(mimeType: String): Boolean {
        if (mimeType.isNullOrEmpty()) return false
        return mimeType.startsWith("video")
    }

    fun isImage(mimeType: String): Boolean {
        if (mimeType.isNullOrEmpty()) return false
        return mimeType.startsWith("image")
    }

    fun isGif(mimeType: String): Boolean {
        if (mimeType.isNullOrEmpty()) return false
        return mimeType == "image/gif"
    }

    /**
     * 获取的视频毫秒数转化成 mm:ss 格式下显示
     */
    fun parseTime(duration: Long): String {
        var stringBuilder = StringBuilder()
        val seconds = duration / 1000
        val min = seconds / 60
        val sec = seconds % 60
        val l = min / 10
        val l1 = min % 10
        val l2 = sec / 10
        val l3 = sec % 10
//        LogUtils.logD(TAG,"seconds=$seconds,min=$min,sec=$sec,duration=$duration")
        return "$l$l1:$l2$l3"
    }

    fun expandBackground(theme:AlbumTheme): GradientDrawable {
        if (mContext == null) {
            throw NullPointerException("You must init AlbumUtils before calling this method")
        }
        var drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.cornerRadius = DensityUtil.dp2px(mContext, 25).toFloat()
        val color=when(theme){
            AlbumTheme.Default -> getColor(R.color.lightBlue)
            AlbumTheme.Red -> getColor(R.color.redPrimaryAlpha)
            AlbumTheme.Pink -> getColor(R.color.pinkPrimaryAlpha)
            AlbumTheme.Purple -> getColor(R.color.purplePrimaryAlpha)
            AlbumTheme.DeepPurple -> getColor(R.color.deepPurplePrimaryAlpha)
            AlbumTheme.Indigo -> getColor(R.color.indigoPrimaryAlpha)
            AlbumTheme.LightBlue -> getColor(R.color.lightBluePrimaryAlpha)
            AlbumTheme.Cyan -> getColor(R.color.cyanPrimaryAlpha)
            AlbumTheme.Teal -> getColor(R.color.tealPrimaryAlpha)
            AlbumTheme.Green -> getColor(R.color.greenPrimaryAlpha)
            AlbumTheme.Amber -> getColor(R.color.amberPrimaryAlpha)
            AlbumTheme.Orange -> getColor(R.color.orangePrimaryAlpha)
            AlbumTheme.BlueGrey -> getColor(R.color.blueGreyPrimaryAlpha)
        }
        drawable.setColor(AppUtils.addAlphaForColor(0.3f,color))
        return drawable
    }

    private fun getColor(@ColorRes resId:Int):Int {
      return ContextCompat.getColor(mContext!!, resId)
    }

    fun getTheme(theme: AlbumTheme): Int {
        return when (theme) {
            AlbumTheme.Default -> R.style.AppTheme
            AlbumTheme.Red -> R.style.RedTheme
            AlbumTheme.Pink -> R.style.PinkTheme
            AlbumTheme.Purple -> R.style.PurpleTheme
            AlbumTheme.DeepPurple -> R.style.DeepPurpleTheme
            AlbumTheme.Indigo -> R.style.IndigoTheme
            AlbumTheme.LightBlue -> R.style.LightBlueTheme
            AlbumTheme.Cyan -> R.style.CyanTheme
            AlbumTheme.Teal -> R.style.TealTheme
            AlbumTheme.Green -> R.style.GreenTheme
            AlbumTheme.Amber -> R.style.AmberTheme
            AlbumTheme.Orange -> R.style.OrangeTheme
            AlbumTheme.BlueGrey -> R.style.BlueGreyTheme
//            else -> R.style.AppTheme
        }
    }

    var mContext: Context? = null
    fun initAlbumUtils(context: Context) {
        mContext = context
    }

    fun formatCustomFont(textView: TextView) {
        if (mContext == null) {
            throw NullPointerException("You must init AlbumUtils before calling this method")
        }
        val typeFace = Typeface.createFromAsset(mContext?.assets, "font/iconfont.ttf")
        textView.typeface=typeFace
    }

}