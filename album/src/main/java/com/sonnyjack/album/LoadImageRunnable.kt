package com.libalum.album

import android.R.id
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import com.sonnyjack.album.AlbumImageUtils
import com.sonnyjack.album.bean.AlbumType
import com.sonnyjack.album.bean.ImageFolder
import com.sonnyjack.album.bean.ImageItem
import java.io.File
import java.util.*


/**
 * 加载图片线程
 */
class LoadImageRunnable : Runnable {

    private var albumType = AlbumType.IMAGE_AND_VIDEO
    private var mContext: Context? = null
    private var mLoadImageRunnableCallBack: LoadImageRunnableCallBack? = null

    constructor(
        context: Context?,
        albumType: Int = AlbumType.IMAGE_AND_VIDEO,
        loadImageRunnableCallBack: LoadImageRunnableCallBack?
    ) {
        this.mContext = context
        this.albumType = albumType
        this.mLoadImageRunnableCallBack = loadImageRunnableCallBack
    }

    override fun run() {
        mLoadImageRunnableCallBack ?: return
        if (null == mContext) {
            callBackError()
            return
        }
        loadImage()
    }

    private fun loadImage() {
        var cursor = buildImageCursor()
        if (null == cursor) {
            callBackError()
            return
        }
        var folderImageArray = ArrayList<ImageFolder>()
        //全部
        var allImageFolder = ImageFolder()
        allImageFolder.name = if (albumType == AlbumType.IMAGE_AND_VIDEO) {
            "图片和视频"
        } else {
            "图片"
        }
        //视频
        var videoFolder = ImageFolder()
        videoFolder.name = "视频"

        val idIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
        val pathIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
        val typeIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)
        val durationIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION)

        while (cursor.moveToNext()) {
            //图片或者视频的路径
            var imagePath = cursor.getString(pathIndex)
            if (TextUtils.isEmpty(imagePath)) continue
            var file = File(imagePath)
            if (!file.exists() || file.length() / 1000 <= 10) continue
            if (AlbumImageUtils.isWebp(imagePath)) continue

            var uri: Uri

            var imageItem = ImageItem()

            imageItem.path = imagePath
            //默认不选中
            imageItem.select = false
            //类型
            var type = cursor.getInt(typeIndex)
            //id
            var id = cursor.getInt(idIndex)
            imageItem.type = AlbumImageUtils.changeType(type)
            if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                //如果是视频
                var duration = cursor.getLong(durationIndex)//视频长度
                imageItem.duration = duration
                videoFolder.images.add(imageItem)
                if (albumType == AlbumType.VIDEO || albumType == AlbumType.IMAGE_AND_VIDEO) {
                    allImageFolder.images.add(imageItem)
                }
                val baseUri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                //val baseUri = Uri.parse("content://media/external/images/media")
                uri = Uri.withAppendedPath(baseUri, "" + id)
            } else {
                //图片类型
                allImageFolder.images.add(imageItem)
                var parentFile = file.parentFile
                if (null == parentFile || !parentFile.exists()) continue
                //该图片的文件夹信息
                var imageFolder = ImageFolder()
                imageFolder.dir = parentFile.absolutePath
                var index = folderImageArray.indexOf(imageFolder)
                if (index >= 0) {
                    imageFolder = folderImageArray[index]
                    imageFolder.images.add(imageItem)
                } else {
                    imageFolder.name = parentFile.name
                    imageFolder.firstImagePath = imagePath
                    imageFolder.images.add(imageItem)
                    folderImageArray.add(imageFolder)
                }
                val baseUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                //val baseUri = Uri.parse("content://media/external/images/media")
                uri = Uri.withAppendedPath(baseUri, "" + id)
            }
            imageItem.uri = uri
            //封面图  -->  全部
            if (TextUtils.isEmpty(allImageFolder.firstImagePath)) {
                allImageFolder.firstImagePath = imagePath
                allImageFolder.firstImageUri = uri
            }
        }
        cursor.close()
        when (albumType) {
            AlbumType.VIDEO -> {//单纯视频
                folderImageArray.clear()
                folderImageArray.add(videoFolder)
            }
            AlbumType.IMAGE -> {//单纯图片
                folderImageArray.add(0, allImageFolder)
            }
            else -> {//视频和图片
                folderImageArray.add(0, videoFolder)
                folderImageArray.add(0, allImageFolder)
            }
        }
        callBackComplete(folderImageArray)
    }

    private fun callBackError() {
        if (null == mLoadImageRunnableCallBack) return
        var activity = AlbumImageUtils.getActivity(mContext)
        if (null == activity) {
            mLoadImageRunnableCallBack?.onError()
        } else {
            activity.runOnUiThread { mLoadImageRunnableCallBack?.onError() }
        }
    }

    private fun callBackComplete(folderImageArray: ArrayList<ImageFolder>) {
        if (null == mLoadImageRunnableCallBack) return
        var activity = AlbumImageUtils.getActivity(mContext)
        if (null == activity) {
            mLoadImageRunnableCallBack?.onError()
        } else {
            activity.runOnUiThread { mLoadImageRunnableCallBack?.complete(folderImageArray) }
        }
    }

    private fun buildImageCursor(): Cursor? {
        val imageUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        var projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Video.VideoColumns.DURATION
        )
        return mContext?.contentResolver?.query(
            imageUri,
            projection,
            buildResolverSelection(),
            null,
            MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        )
    }

    private fun buildResolverSelection(): String {
        val sb = StringBuilder()
        if (albumType == AlbumType.IMAGE) {
            sb.append(MediaStore.Files.FileColumns.MEDIA_TYPE)
                .append("=")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
        } else if (albumType == AlbumType.VIDEO) {
            sb.append(MediaStore.Files.FileColumns.MEDIA_TYPE)
                .append("=")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
        } else {
            sb.append(MediaStore.Files.FileColumns.MEDIA_TYPE)
                .append("=")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
                .append(" OR ")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE)
                .append("=")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
        }
        return sb.toString()
    }

    interface LoadImageRunnableCallBack {
        fun complete(imageFolders: ArrayList<ImageFolder>)
        fun onError()
    }
}