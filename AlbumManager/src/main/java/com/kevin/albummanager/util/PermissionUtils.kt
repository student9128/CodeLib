package com.kevin.albummanager.util

import android.os.Build
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import com.kevin.albummanager.constant.AlbumConstant

object PermissionUtils {
    fun getStorageAndCameraPermissions(mimeType: String = AlbumConstant.TYPE_ALL, includeCamera: Boolean = true): List<IPermission> {
        val permissions = mutableListOf<IPermission>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (mimeType) {
                AlbumConstant.TYPE_VIDEO -> permissions.add(PermissionLists.getReadMediaVideoPermission())
                AlbumConstant.TYPE_IMAGE,
                AlbumConstant.TYPE_GIF,
                AlbumConstant.TYPE_IMAGE_NO_GIF -> permissions.add(PermissionLists.getReadMediaImagesPermission())
                else -> {
                    permissions.add(PermissionLists.getReadMediaImagesPermission())
                    permissions.add(PermissionLists.getReadMediaVideoPermission())
                }
            }
        } else {
            permissions.add(PermissionLists.getReadExternalStoragePermission())
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                permissions.add(PermissionLists.getWriteExternalStoragePermission())
            }
        }
        if (includeCamera) {
            permissions.add(PermissionLists.getCameraPermission())
        }
        return permissions
    }
}