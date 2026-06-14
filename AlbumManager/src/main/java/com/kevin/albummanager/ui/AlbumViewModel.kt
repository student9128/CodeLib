package com.kevin.albummanager.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.kevin.albummanager.AlbumManagerConfig
import com.kevin.albummanager.AlbumManagerCollection
import com.kevin.albummanager.bean.AlbumData
import com.kevin.albummanager.bean.AlbumFolder
import com.kevin.albummanager.constant.AlbumConstant
import com.kevin.albummanager.loader.AlbumLoader
import com.kevin.albummanager.loader.AlbumPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumViewModel(application: Application) : AndroidViewModel(application) {
    
    private val albumLoader = AlbumLoader.albumLoaderInstance
    private val albumManagerConfig = AlbumManagerConfig.albumManagerConfig
    
    private val _albumFolders = MutableStateFlow<List<AlbumFolder>>(emptyList())
    val albumFolders: StateFlow<List<AlbumFolder>> = _albumFolders
    
    private val _selectedFolder = MutableStateFlow<AlbumFolder?>(null)
    val selectedFolder: StateFlow<AlbumFolder?> = _selectedFolder
    
    private val _selectedAlbumDataList = MutableStateFlow<List<AlbumData>>(emptyList())
    val selectedAlbumDataList: StateFlow<List<AlbumData>> = _selectedAlbumDataList
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _emptyMessage = MutableStateFlow("还没有数据 :(")
    val emptyMessage: StateFlow<String> = _emptyMessage

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val albumManagerCollection = AlbumManagerCollection.albumManagerCollectionInstance

    private val _showFolderSheet = MutableStateFlow(false)
    val showFolderSheet: StateFlow<Boolean> = _showFolderSheet

    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    private val albumPagingDataFlow: Flow<PagingData<AlbumData>> = combine(
        _selectedFolder.filterNotNull(),
        _refreshTrigger.onStart { emit(Unit) }
    ) { folder, _ ->
        folder
    }.flatMapLatest { folder ->
        Pager(
            config = PagingConfig(
                pageSize = 60,
                prefetchDistance = 60,
                enablePlaceholders = false,
                initialLoadSize = 60,
                maxSize = 1200
            ),
            pagingSourceFactory = { AlbumPagingSource(getApplication(), albumManagerConfig.mimeType, folder.bucketId) }
        ).flow
        .map { pagingData ->
            val dataWithCamera = if (folder.bucketId == -1L && albumManagerConfig.camera) {
                pagingData.insertSeparators { before, _ ->
                    if (before == null) AlbumData(showCameraPlaceholder = true) else null
                }
            } else {
                pagingData
            }
            dataWithCamera
        }
    }.cachedIn(viewModelScope)

    val pagingDataFlow: Flow<PagingData<AlbumData>> = combine(
        albumPagingDataFlow,
        _selectedAlbumDataList
    ) { pagingData, selectedList ->
        val selectedIndexByKey = selectedList
            .mapIndexed { index, data -> data.stableKey() to index }
            .toMap()
        pagingData.map { item ->
            val index = selectedIndexByKey[item.stableKey()] ?: -1
            item.copy(selected = index >= 0, selectedIndex = index)
        }
    }

    init {
        albumLoader.setParams(application)
        _selectedAlbumDataList.value = albumManagerCollection.getSelectionData()
    }

    fun toggleFolderSheet(show: Boolean) {
        _showFolderSheet.value = show
    }

    fun loadFolders() {
        viewModelScope.launch {
            _isLoading.value = true
            if (_selectedFolder.value == null) {
                val defaultFolder = AlbumFolder(
                    id = -1,
                    displayName = AlbumConstant.ALBUM_FOLDER_TYPE_DEFAULT,
                    bucketId = -1,
                    checked = true
                )
                _selectedFolder.value = defaultFolder
                _albumFolders.value = listOf(defaultFolder)
            }

            val folders = withContext(Dispatchers.IO) {
                albumLoader.loadFolderX()
            }
            val currentFolder = _selectedFolder.value
            if (folders.isNotEmpty()) {
                _albumFolders.value = folders.map { folder ->
                    folder.copy(
                        checked = currentFolder?.let {
                            it.bucketId == folder.bucketId && it.displayName == folder.displayName
                        } ?: folder.bucketId == -1L
                    )
                }
            } else {
                _emptyMessage.value = "还没有数据 :("
                _albumFolders.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun selectFolder(folder: AlbumFolder) {
        val folders = _albumFolders.value.map {
            it.copy(checked = it.bucketId == folder.bucketId && it.displayName == folder.displayName)
        }
        _albumFolders.value = folders
        _selectedFolder.value = folders.firstOrNull { it.checked } ?: folder
        albumManagerCollection.saveAlbumFolderType(folder.displayName)
    }

    fun toggleSelection(item: AlbumData): Boolean {
        if (item.showCameraPlaceholder) return false
        val selectedList = _selectedAlbumDataList.value.toMutableList()
        val index = selectedList.indexOfFirst { it.stableKey() == item.stableKey() }
        
        if (index >= 0) {
            selectedList.removeAt(index)
            albumManagerCollection.removeSelectedAlbumData(item)
        } else {
            if (selectedList.size >= albumManagerConfig.maxSelectedNum) {
                _toastMessage.value = "最多只能选择${albumManagerConfig.maxSelectedNum}个文件"
                return false
            }
            val newItem = item.copy(selected = true, selectedIndex = selectedList.size)
            selectedList.add(newItem)
            albumManagerCollection.addSelectedAlbumData(newItem)
        }

        val updatedSelectedList = selectedList.mapIndexed { i, data ->
            data.copy(selectedIndex = i)
        }
        _selectedAlbumDataList.value = updatedSelectedList
        albumManagerCollection.saveSelectionData(ArrayList(updatedSelectedList))
        return true
    }

    fun refreshAfterCamera() {
        viewModelScope.launch {
            _refreshTrigger.emit(Unit)
            loadFolders()
        }
    }

    fun consumeToast() {
        _toastMessage.value = null
    }

    fun markNoPermission() {
        _albumFolders.value = emptyList()
        _emptyMessage.value = "未获得相关权限，无法获取相册数据 :("
    }

    fun currentPreviewItems(selectedOnly: Boolean): List<AlbumData> {
        // Warning: This is tricky with Paging. 
        // For now, if not selectedOnly, we might need a way to get the full list for preview.
        // But the user might only want to preview what's currently loaded or all.
        // If they want to preview all, Paging doesn't give a simple list.
        // I might need to keep a separate "all data" loader or just use the current selection for preview.
        // The original code used albumLoader.loadAlbumDataX() for this.
        return if (selectedOnly) {
            _selectedAlbumDataList.value
        } else {
            // This is a bit heavy but used only when entering preview
            val folder = _selectedFolder.value ?: return emptyList()
            if (folder.bucketId == -1L) {
                albumLoader.loadAlbumDataX()
            } else {
                albumLoader.loadImageByBucketIdX(folder.mimeType, folder.bucketId.toString())
            }
        }
    }

    fun selectedResult(): ArrayList<AlbumData> {
        return ArrayList(_selectedAlbumDataList.value)
    }

    override fun onCleared() {
        super.onCleared()
        albumManagerCollection.reset()
        albumManagerConfig.reset()
    }
}
