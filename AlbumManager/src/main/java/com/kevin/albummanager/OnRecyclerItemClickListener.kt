package com.kevin.albummanager

import android.view.View

/**
 * Created by Kevin on 2021/1/22<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
interface OnRecyclerItemClickListener {
   open fun onItemClick(position:Int,view:View){}
   open fun onItemClick(position: Int,view: View,type:String=""){}
   open fun onChildItemClick(position: Int,view: View,type:String=""){}
}