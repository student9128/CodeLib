package com.kevin.albummanager

import com.kevin.albummanager.bean.AlbumData
import com.kevin.albummanager.bean.AlbumFolder
import com.kevin.albummanager.constant.AlbumConstant
import com.kevin.albummanager.util.AlbumUtils
import com.kevin.albummanager.util.LogUtils


/**
 * Created by Kevin on 2021/2/8<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：炽热的孤独心
 *
 * Describe:<br/>
 *
 * 相簿中选中的图片或者视频集合，相册文件夹集合，当前选中的文件夹数据
 */
class AlbumManagerCollection {
    private var mAlbumFolderDataList: ArrayList<AlbumFolder> = ArrayList()
    private var mSelectionCollection: ArrayList<AlbumData> = ArrayList()
    private var mCurrentAlbumDataList: ArrayList<AlbumData> = ArrayList()
    private var mAllAlbumDataList: ArrayList<AlbumData> = ArrayList()
    private var mCurrentAlbumFolderType: String = AlbumConstant.ALBUM_FOLDER_TYPE_DEFAULT
    private var mPreviewSelectionCollection: ArrayList<AlbumData> = ArrayList()
    var mSelectionList: MutableList<MutableMap<Int, AlbumData>> = mutableListOf()

    companion object {
        val albumManagerCollectionInstance: AlbumManagerCollection by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AlbumManagerCollection()
        }
    }

    fun savePreviewSelectionData(data: AlbumData) {
        if (mPreviewSelectionCollection.size == 0) {
            mPreviewSelectionCollection.add(data)
        } else {
            if (mPreviewSelectionCollection.contains(data)) {
//                mPreviewSelectionCollection.remove(data)
            } else {
                mPreviewSelectionCollection.add(data)
            }
        }
    }

    fun getPreviewSelectionData() = mPreviewSelectionCollection

    /**
     * 存储已经选择的数据集合
     */
    fun saveSelectionData(selectionList: ArrayList<AlbumData>) {
        selectionList?.let {
            mSelectionCollection = it
        }
//        LogUtils.logD(
//            "AlbumManagerCollection",
//            "mSelectionCollection.size=${mSelectionCollection.size},,$mSelectionCollection"
//        )
    }

    fun getSelectionData() = mSelectionCollection

    fun saveSelectionList(selectionList: MutableList<MutableMap<Int, AlbumData>>) {
//        mSelectionList.clear()
        mSelectionList = selectionList
    }

    fun getSelectionList() = mSelectionList

    fun saveAllAlbumData(albumDataList: ArrayList<AlbumData>) {
        albumDataList?.let {
            mAllAlbumDataList = it
        }
    }

    fun getAllAlbumData() = mAllAlbumDataList

    fun saveCurrentAlbumData(albumDataList: ArrayList<AlbumData>) {
        mCurrentAlbumDataList = albumDataList
    }

    fun getCurrentAlbumData() = mCurrentAlbumDataList

    fun saveAlbumFolder(albumFolderList: ArrayList<AlbumFolder>) {
        albumFolderList?.let { mAlbumFolderDataList = it }
    }

    fun getAlbumFolder(): ArrayList<AlbumFolder> = mAlbumFolderDataList

    fun saveAlbumFolderType(albumFolderType: String) {
        mCurrentAlbumFolderType = albumFolderType
    }

    fun getAlbumFolderType() = mCurrentAlbumFolderType


    fun clearSelectionData() {
        mSelectionCollection?.let { it.clear() }
    }

    fun clearSelectionList() {
        mSelectionList?.let { it.clear() }
    }

    fun clearAllAlbumData() {
        mAllAlbumDataList?.let { it.clear() }
    }

    fun clearCurrentAlbumData() {
        mCurrentAlbumDataList?.let { it.clear() }
    }

    fun clearAlbumFolderData() {
        mAlbumFolderDataList.let { it.clear() }
    }
    fun clearPreviewSelectionData(){
        mPreviewSelectionCollection?.let { it.clear() }
    }

    fun reset() {
        clearSelectionData()
        clearSelectionList()
        clearAllAlbumData()
        clearCurrentAlbumData()
        clearAlbumFolderData()
        clearPreviewSelectionData()
    }

    fun isSelected(album: AlbumData): Boolean {
        if (AlbumUtils.isVideo(album.mimeType)) {
            for (albumData in mSelectionCollection){
                if(album.id==albumData.id&&album.path==albumData.path){
                    return true
                }
            }
        }
//        LogUtils.logI("AlbumManagerCollection", "albume= $album====${mSelectionCollection?.contains(album)}")
//        LogUtils.logD("AlbumManagerCollection", "albume=${mSelectionCollection}")
        return mSelectionCollection?.contains(album)
    }

    fun checkedNum(album: AlbumData): Int {
        var index = mSelectionCollection.indexOf(album)
        return if (index == -1) {
            -1
        } else {
            index + 1
        }
    }

    fun removeSelectedAlbumData(album: AlbumData) {
        mSelectionCollection?.remove(album)
    }

    fun addSelectedAlbumData(album: AlbumData) {
//        LogUtils.logI("AlbumManagerCollection", "addSelectedAlbumData=$album")
        mSelectionCollection.add(album)
//        LogUtils.logI("AlbumManagerCollection", "addSelectedAlbumData=$mSelectionCollection")
    }

    fun hasSelectedAlbumData(): Boolean {
        return mSelectionCollection?.size > 0
    }

    fun getSelectedAlbumDataSize(): Int = mSelectionCollection?.size
}