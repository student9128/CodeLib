package com.kevin.albummanager.util

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.kevin.albummanager.R
import com.kevin.albummanager.constant.AlbumTheme

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

    fun expandBackground(theme:AlbumTheme,context: Context): GradientDrawable {
        var drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.cornerRadius = DensityUtil.dp2px(context, 25).toFloat()
        val color=when(theme){
            AlbumTheme.Default -> getColor(context, R.color.lightBlue)
            AlbumTheme.Red -> getColor(context,R.color.redPrimaryAlpha)
            AlbumTheme.Pink -> getColor(context,R.color.pinkPrimaryAlpha)
            AlbumTheme.Purple -> getColor(context,R.color.purplePrimaryAlpha)
            AlbumTheme.DeepPurple -> getColor(context,R.color.deepPurplePrimaryAlpha)
            AlbumTheme.Indigo -> getColor(context,R.color.indigoPrimaryAlpha)
            AlbumTheme.LightBlue -> getColor(context,R.color.lightBluePrimaryAlpha)
            AlbumTheme.Cyan -> getColor(context,R.color.cyanPrimaryAlpha)
            AlbumTheme.Teal -> getColor(context,R.color.tealPrimaryAlpha)
            AlbumTheme.Green -> getColor(context,R.color.greenPrimaryAlpha)
            AlbumTheme.Amber -> getColor(context,R.color.amberPrimaryAlpha)
            AlbumTheme.Orange -> getColor(context,R.color.orangePrimaryAlpha)
            AlbumTheme.BlueGrey -> getColor(context,R.color.blueGreyPrimaryAlpha)
        }
        drawable.setColor(AppUtils.addAlphaForColor(0.3f,color))
        return drawable
    }

    private fun getColor(context: Context,@ColorRes resId:Int):Int {
      return ContextCompat.getColor(context, resId)
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
    fun getThemeColor(theme:AlbumTheme,context: Context): Int {
        val color=when(theme){
            AlbumTheme.Default -> getColor(context, R.color.colorPrimary)
            AlbumTheme.Red -> getColor(context,R.color.redPrimary)
            AlbumTheme.Pink -> getColor(context,R.color.pinkPrimary)
            AlbumTheme.Purple -> getColor(context,R.color.purplePrimary)
            AlbumTheme.DeepPurple -> getColor(context,R.color.deepPurplePrimary)
            AlbumTheme.Indigo -> getColor(context,R.color.indigoPrimary)
            AlbumTheme.LightBlue -> getColor(context,R.color.lightBluePrimary)
            AlbumTheme.Cyan -> getColor(context,R.color.cyanPrimary)
            AlbumTheme.Teal -> getColor(context,R.color.tealPrimary)
            AlbumTheme.Green -> getColor(context,R.color.greenPrimary)
            AlbumTheme.Amber -> getColor(context,R.color.amberPrimary)
            AlbumTheme.Orange -> getColor(context,R.color.orangePrimary)
            AlbumTheme.BlueGrey -> getColor(context,R.color.blueGreyPrimary)
        }
        return color
    }

    fun formatCustomFont(context: Context,textView: TextView) {
        val typeFace = Typeface.createFromAsset(context.assets, "font/iconfont.ttf")
        textView.typeface=typeFace
    }

}