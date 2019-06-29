package com.sonnyjack.library.image

class ImageDisplayOption {

    companion object {
        const val DECODE_FORMAT_RGB_565 = 1
        const val DECODE_FORMAT_ARGB_8888 = 2
    }

    private var placeholder: Int = 0//默认图片
    private var error: Int = 0//下载失败显示的图片

    private var overrideWidth: Int = 0//指定图片宽度
    private var overrideHeight: Int = 0//指定图片高度

    private var decodeFormat = DECODE_FORMAT_RGB_565//bitmap编码格式 清晰度

    fun getPlaceholder(): Int {
        return placeholder
    }

    fun setPlaceholder(placeholder: Int) {
        this.placeholder = placeholder
    }

    fun getError(): Int {
        return error
    }

    fun setError(error: Int) {
        this.error = error
    }

    fun getOverrideWidth(): Int {
        return overrideWidth
    }

    fun setOverrideWidth(overrideWidth: Int) {
        this.overrideWidth = overrideWidth
    }

    fun getOverrideHeight(): Int {
        return overrideHeight
    }

    fun setOverrideHeight(overrideHeight: Int) {
        this.overrideHeight = overrideHeight
    }

    fun getDecodeFormat(): Int {
        return decodeFormat
    }

    fun setDecodeFormat(decodeFormat: Int) {
        this.decodeFormat = decodeFormat
    }
}