package com.kevin.albummanager

/**
 * Created by Kevin on 2021/4/21<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：炽热的孤独心
 *
 * Describe:<br/>
 */
interface OnPermissionListener {
    fun onPermissionGrated(permissions: ArrayList<String>, all:Boolean)
    fun onPermissionDenied(permissions: ArrayList<String>, all:Boolean)
}