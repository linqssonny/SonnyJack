package com.sonnyjack.album.bean

import android.net.Uri
import android.text.TextUtils
import java.util.ArrayList

class ImageFolder {
    /**
     * 文件夹的名称
     */
    var name: String? = null

    /**
     * 文件夹下第一张图片地址
     */
    var firstImagePath: String? = null

    /**
     * 文件夹下第一张图片地址
     */
    var firstImageUri: Uri? = null

    /**
     * 该文件夹下的图片集
     */
    var images = ArrayList<ImageItem>()

    /**
     * 路径
     */
    var dir: String? = null

    override fun equals(other: Any?): Boolean {
        if (other is ImageFolder) {
            return TextUtils.equals(dir, other.dir)
        }
        return super.equals(other)
    }
}