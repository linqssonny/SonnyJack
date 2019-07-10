package com.sonnyjack.album

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import androidx.core.content.FileProvider
import com.libalum.album.AlbumSelectionActivity
import com.sonnyjack.album.bean.AlbumType
import com.sonnyjack.album.bean.ImageItem
import com.sonnyjack.album.preview.ImagePreviewActivity
import com.sonnyjack.permission.IRequestPermissionCallBack
import com.sonnyjack.permission.PermissionUtils
import java.io.File


object AlbumImageUtils {

    /**
     * 存放图片的文件夹
     */
    fun getImagePath(context: Context): String {
        var imagePath =
            Environment.getExternalStorageDirectory().absolutePath + File.separator + context.packageName + File.separator + "Image"
        val imageFolder = File(imagePath)
        if (!imageFolder.exists()) {
            imageFolder.mkdirs()
        }
        return imagePath
    }

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

    fun openCamera(activity: Activity) {
        var permission = ArrayList<String>()
        permission.add(Manifest.permission.CAMERA)
        permission.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        PermissionUtils.getInstances().requestPermission(activity, permission, object : IRequestPermissionCallBack() {
            override fun onGranted() {
                val openCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val imageUri = buildImageOutputPath(activity)
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                //Android7.0添加临时权限标记，此步千万别忘了
                openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                activity.startActivityForResult(openCameraIntent, AlbumSelectionActivity.TAKE_PICTURE)
            }

            override fun onDenied() {

            }
        })
    }

    private fun buildImageOutputPath(context: Context): Uri? {
        var imageName = System.currentTimeMillis()
        val imagePath = getImagePath(context) + File.separator + imageName + ".png"
        var imageFile = File(imagePath)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// sdk >= 24  android7.0以上
            //与清单文件中android:authorities的值保持一致 File(imagePath))
            FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", imageFile)
        } else {
            Uri.fromFile(imageFile)//或者 Uri.isPaise("file://"+file.toString()
        }
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

    /**
     * dp  转为  px
     */
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

    fun imageUri2Path(context: Context, uri: Uri?): String? {
        if (null == uri) return null
        val filePathColumns = arrayOf(MediaStore.Images.Media.DATA)
        val c = context.contentResolver.query(uri, filePathColumns, null, null, null)
        c.moveToFirst()
        val columnIndex = c.getColumnIndex(filePathColumns[0])
        val picturePath = c.getString(columnIndex)
        c.close()
        return picturePath
    }
}