package com.kevin.albummanager

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.layout_tool_bar.*

/**
 * Created by Kevin on 2020/9/6<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
abstract class BaseActivity : AppBaseActivity() {
    private val REQ_PERIMSSION_CODE = 0x1000
    private val mPermissions: MutableList<String> = ArrayList()
    private var hasPermission = false
    override fun onCreate(savedInstanceState: Bundle?) {
        doSomethingBeforeOnCreate()
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResID())
        setSupportActionBar(toolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
        initView()
    }

    open fun doSomethingBeforeOnCreate() {
    }

    abstract fun getLayoutResID(): Int
    abstract fun initView()

    fun startNewActivity(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    fun checkPermission(permissionArgs: ArrayList<String>): Boolean {
        mPermissions.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasPermission = false
            for (permission in permissionArgs) {
                if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    )
                ) {
                    mPermissions.add(permission)
                }
            }
            if (mPermissions.size == 0) {
                hasPermission = true
            }
        } else {
            hasPermission = true
        }
        return hasPermission
    }

    fun checkPermission(vararg permissionArgs: String): Boolean {
        mPermissions.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasPermission = false
            for (permission in permissionArgs) {
                if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    )
                ) {
                    mPermissions.add(permission)
                }
            }
            if (mPermissions.size == 0) {
                hasPermission = true
            }
        } else {
            hasPermission = true
        }
        return hasPermission
    }

    fun requestPermissions() {
        if (mPermissions.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                mPermissions.toTypedArray(),
                REQ_PERIMSSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        var grantedPermissions = ArrayList<String>()
        var deniedPermissions = ArrayList<String>()
        if (requestCode == REQ_PERIMSSION_CODE && grantResults.isNotEmpty()) {
            for (index in grantResults.indices) {
                if (grantResults[index] == 0) {//允许权限返回0禁止返回-1
                    grantedPermissions.add(permissions[index])
                } else {
                    deniedPermissions.add(permissions[index])
                }
            }
            if (grantedPermissions.size == mPermissions.size) {
                permissionListener?.onPermissionGrated(grantedPermissions, true)
            } else if (grantedPermissions.size > 0 && grantedPermissions.size < mPermissions.size) {
                permissionListener?.onPermissionGrated(grantedPermissions, false)
                permissionListener?.onPermissionDenied(deniedPermissions, false)
            } else {
                permissionListener?.onPermissionDenied(deniedPermissions, true)
            }
        } else {
            deniedPermissions.addAll(permissions)
            permissionListener?.onPermissionDenied(deniedPermissions, true)
        }
    }

    var permissionListener: OnPermissionListener? = null
    fun setOnPermissionRequestListener(listener: OnPermissionListener) {
        permissionListener = listener
    }
}