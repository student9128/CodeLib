package com.kevin.albummanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kevin.albummanager.bean.AlbumData
import com.kevin.albummanager.constant.AlbumConstant
import com.kevin.albummanager.constant.AlbumPreviewMethod
import com.kevin.albummanager.ui.AlbumTheme
import com.kevin.albummanager.ui.AlbumPreviewScreen

open class PhotoPreviewActivity : ComponentActivity() {
    
    val albumManagerCollectionInstance = AlbumManagerCollection.albumManagerCollectionInstance
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val position = intent.getIntExtra("position", 0)
        val previewMethod = intent.getStringExtra(AlbumConstant.PREVIEW_METHOD)
        val data = if (previewMethod == AlbumPreviewMethod.SINGLE.name) {
            albumManagerCollectionInstance.getSelectionData()
        } else {
            albumManagerCollectionInstance.getCurrentAlbumData()
        }

        setContent {
            AlbumTheme {
                AlbumPreviewScreen(
                    items = data,
                    selectedCount = albumManagerCollectionInstance.getSelectedAlbumDataSize(),
                    initialPage = position,
                    onBackClick = { finish() },
                    onSelectClick = { item -> toggleSelection(item) },
                    onSendClick = { finishWithSelection() }
                )
            }
        }
    }

    private fun toggleSelection(item: AlbumData) {
        albumManagerCollectionInstance.savePreviewSelectionData(item)
        if (albumManagerCollectionInstance.isSelected(item)) {
            albumManagerCollectionInstance.removeSelectedAlbumData(item)
        } else if (AlbumManagerConfig.albumManagerConfig.maxSelectedNum > albumManagerCollectionInstance.getSelectedAlbumDataSize()) {
            albumManagerCollectionInstance.addSelectedAlbumData(item)
        }
    }

    private fun finishWithSelection() {
        setResult(
            RESULT_OK,
            android.content.Intent().putParcelableArrayListExtra(
                AlbumConstant.SET_RESULT_FOR_SELECTION,
                albumManagerCollectionInstance.getSelectionData()
            )
        )
        finish()
    }
}
