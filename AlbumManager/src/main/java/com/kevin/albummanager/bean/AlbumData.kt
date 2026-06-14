package com.kevin.albummanager.bean

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Parcelize
@Serializable
data class AlbumData(
    val id: Long = 0,
    val path: String? = "",
    val width: Int = 0,
    val height: Int = 0,
    val mimeType: String = "",
    val duration: Long = 0,
    val size: Long = 0,
    val selected: Boolean = false,
    val selectedIndex: Int = -1,
    val original: Boolean = false,
    val key: Int = -1,
    val showCameraPlaceholder: Boolean = false,
    @Transient
    val videoCover: Bitmap? = null,
    @Transient
    val videoCoverThumbnail: Bitmap? = null,
    val uriString: String? = null
) : Parcelable {
    val uri: Uri?
        get() = uriString?.let { Uri.parse(it) }

    fun stableKey(): String = "${id}_${path.orEmpty()}"
}

@Serializable
data class AlbumFolder(
    val id: Long = 0,
    val mimeType: String = "",
    val displayName: String = "",
    val bucketId: Long = 0,
    val count: Long = 0,
    val checked: Boolean = false,
    val coverUriString: String? = null
) {
    val coverUri: Uri?
        get() = coverUriString?.let { Uri.parse(it) }
}
