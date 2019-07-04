package com.sonnyjack.album

import android.Manifest
import android.provider.MediaStore
import android.text.TextUtils
import com.sonnyjack.album.bean.AlbumType
import android.content.ContextWrapper
import android.app.Activity
import android.content.Context
import android.view.View
import com.libalum.album.AlbumSelectionActivity
import com.sonnyjack.album.bean.ImageItem
import com.sonnyjack.album.preview.ImagePreviewActivity
import com.sonnyjack.permission.IRequestPermissionCallBack
import com.sonnyjack.permission.PermissionUtils


object AlbumImageUtils {
    /**
     * 是否webp
     */
    fun isWebp(url: String): Boolean {
        var isWebp = false
        if (!TextUtils.isEmpty(url)) {
            val index = url.lastIndexOf(".")
            if (index >= 0 && index < url.length) {
                val prefix = url.substring(index + 1)
                if (TextUtils.equals("webp", prefix)) {
                    isWebp = true
                }
            }
        }
        return isWebp
    }

    fun changeType(type: Int): Int {
        return when (type) {
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> AlbumType.VIDEO
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> AlbumType.IMAGE
            MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO -> AlbumType.AUDIO
            else -> AlbumType.NONE
        }
    }

    fun getActivity(view: View?): Activity? {
        return if (null == view) {
            null
        } else getActivity(view?.context)
    }

    fun getActivity(context: Context?): Activity? {
        var context = context
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

    fun openAlbum(activity: Activity, albumType: Int, isNeedCrop: Boolean, maxImage: Int) {
        var permission = ArrayList<String>()
        permission.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        PermissionUtils.getInstances().requestPermission(activity, permission, object : IRequestPermissionCallBack() {
            override fun onGranted() {
                AlbumSelectionActivity.Builder()
                    .with(activity)
                    .setType(albumType)
                    .setNeedCrop(isNeedCrop)
                    .setMaxImage(maxImage)
                    .build(AlbumSelectionActivity.REQUEST_CODE)
            }

            override fun onDenied() {

            }

        })
    }

    fun openPreviewImage(activity: Activity, selectItems: java.util.ArrayList<ImageItem>?) {
        var permission = ArrayList<String>()
        permission.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        PermissionUtils.getInstances().requestPermission(activity, permission, object : IRequestPermissionCallBack() {
            override fun onGranted() {
                ImagePreviewActivity.Builder()
                    .with(activity)
                    .setSelectItems(selectItems)
                    .build()
            }

            override fun onDenied() {

            }

        })
    }

    fun dip2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }
}