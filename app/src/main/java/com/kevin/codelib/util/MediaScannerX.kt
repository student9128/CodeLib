package com.kevin.codelib.util

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri

/**
 * Created by Kevin on 2021/3/29<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
class MediaScannerX(
    val context: Context,
    val mPath: String,
    val listener: ScanListener?
) : MediaScannerConnection.MediaScannerConnectionClient {
    var msc: MediaScannerConnection = MediaScannerConnection(context, this)
    override fun onScanCompleted(path: String?, uri: Uri?) {
        msc.disconnect()
        listener?.onScanFinish()
    }

    override fun onMediaScannerConnected() {
        msc.scanFile(mPath, null)
    }

    interface ScanListener {
        fun onScanFinish()
    }
}