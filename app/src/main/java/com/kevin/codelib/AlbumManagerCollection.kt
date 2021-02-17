package com.kevin.codelib

import com.kevin.codelib.bean.AlbumData
import com.kevin.codelib.bean.AlbumFolder
import com.kevin.codelib.constant.AlbumConstant

/**
 * Created by Kevin on 2021/2/8<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
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
    var mSelectionList: MutableList<MutableMap<Int, AlbumData>> = mutableListOf()

    companion object {
        val albumManagerCollectionInstance: AlbumManagerCollection by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AlbumManagerCollection()
        }
    }

    /**
     * 存储已经选择的数据集合
     */
    fun saveSelectionData(selectionList: ArrayList<AlbumData>) {
        selectionList?.let {
            mSelectionCollection = it
        }
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

    fun reset() {
        clearSelectionData()
        clearSelectionList()
        clearAllAlbumData()
        clearCurrentAlbumData()
        clearAlbumFolderData()
    }
}