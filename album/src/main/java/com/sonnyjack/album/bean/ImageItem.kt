package com.sonnyjack.album.bean

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.os.Parcel
import android.os.Parcelable


class ImageItem() : Parcelable {
    /**
     * 文件路径
     */
    @TargetApi(Build.VERSION_CODES.Q)
    @Deprecated("Android Q will not support visit file path direct, please use field uri")
    var path: String? = null

    /**
     * 文件uri
     */
    var uri: Uri? = null

    /**
     * 文件类型
     */
    var type: Int = 0

    /**
     * 时长(视频)
     */
    var duration: Long = 0
    var select: Boolean = false//是否选中

    var isEnable: Boolean = true//是否可以点击(用以只能选择图片或者视频)

    constructor(parcel: Parcel) : this() {
        path = parcel.readString()
        uri = parcel.readParcelable(Uri::class.java.classLoader)
        type = parcel.readInt()
        duration = parcel.readLong()
        select = parcel.readByte() != 0.toByte()
        isEnable = parcel.readByte() != 0.toByte()
    }

    constructor(path: String?) : this() {
        this.path = path
    }

    fun getFormatDuration(): String {
        return (duration / 1000 / 60).toString() + ":" + duration / 1000
    }

    fun isVideoType(): Boolean {
        return type == AlbumType.VIDEO
    }

    fun isImageType(): Boolean {
        return type == AlbumType.IMAGE
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(path)
        parcel.writeParcelable(uri, flags)
        parcel.writeInt(type)
        parcel.writeLong(duration)
        parcel.writeByte(if (select) 1 else 0)
        parcel.writeByte(if (isEnable) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageItem> {
        override fun createFromParcel(parcel: Parcel): ImageItem {
            return ImageItem(parcel)
        }

        override fun newArray(size: Int): Array<ImageItem?> {
            return arrayOfNulls(size)
        }
    }

}