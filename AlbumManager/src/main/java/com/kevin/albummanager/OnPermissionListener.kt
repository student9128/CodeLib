package com.kevin.albummanager

/**
 * Created by Kevin on 2021/4/21<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
interface OnPermissionListener {
    fun onPermissionGrated(permissions: ArrayList<String>, all:Boolean)
    fun onPermissionDenied(permissions: ArrayList<String>, all:Boolean)
}