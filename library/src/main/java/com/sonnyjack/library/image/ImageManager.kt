package com.sonnyjack.library.image

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop
import com.bumptech.glide.request.RequestOptions


object ImageManager {
    private var imageDisplayOption: ImageDisplayOption? = null

    fun init(imageDisplayOption: ImageDisplayOption?) {
        this.imageDisplayOption = imageDisplayOption;
    }

    fun createDefaultImageDisplayOption(): ImageDisplayOption {
        return this.imageDisplayOption ?: ImageDisplayOption()
    }

    fun displayImage(imageView: ImageView, imageUri: Uri?) {
        displayImage(imageView.context, imageView, imageUri, null)
    }

    fun displayImage(
        context: Context,
        imageView: ImageView,
        imageUri: Uri?
    ) {
        displayImage(context, imageView, imageUri, imageDisplayOption)
    }

    fun displayImage(imageView: ImageView, imageUrl: String?) {
        displayImage(imageView.context, imageView, imageUrl)
    }

    fun displayImage(
        imageView: ImageView,
        imageUrl: String?,
        imageDisplayOption: ImageDisplayOption?
    ) {
        displayImage(imageView.context, imageView, imageUrl, imageDisplayOption)
    }

    fun displayImage(context: Context, imageView: ImageView, imageUrl: String?) {
        displayImage(context, imageView, imageUrl, null)
    }

    fun displayImage(
        context: Context,
        imageView: ImageView,
        imageUrl: String?,
        imageDisplayOption: ImageDisplayOption?
    ) {
        var requestOptions = buildRequestOptions(imageDisplayOption)
        Glide.with(context).load(imageUrl).apply(requestOptions).into(imageView)
    }

    fun displayImage(
        context: Context,
        imageView: ImageView,
        imageUri: Uri?,
        imageDisplayOption: ImageDisplayOption?
    ) {
        var requestOptions = buildRequestOptions(imageDisplayOption)
        Glide.with(context).load(imageUri).apply(requestOptions).into(imageView)
    }

    private fun buildRequestOptions(imageDisplayOption: ImageDisplayOption?): RequestOptions {
        var displayOption: ImageDisplayOption? = imageDisplayOption ?: this.imageDisplayOption
        if (null == displayOption) {
            displayOption = createDefaultImageDisplayOption()
        }
        val requestOptions = RequestOptions()
        requestOptions.centerCrop()
        if (displayOption.getPlaceholder() > 0) {
            requestOptions.placeholder(displayOption.getPlaceholder())
        }
        if (displayOption.getError() > 0) {
            requestOptions.error(displayOption.getError())
        }
        if (displayOption.getOverrideWidth() > 0 || displayOption.getOverrideHeight() > 0) {
            requestOptions.override(
                displayOption.getOverrideWidth(),
                displayOption.getOverrideHeight()
            )
        }
        when (displayOption.getDecodeFormat()) {
            ImageDisplayOption.DECODE_FORMAT_RGB_565 -> requestOptions.format(DecodeFormat.PREFER_RGB_565)
            ImageDisplayOption.DECODE_FORMAT_ARGB_8888 -> requestOptions.format(DecodeFormat.PREFER_ARGB_8888)
            else -> requestOptions.format(DecodeFormat.PREFER_RGB_565)
        }

        return requestOptions
    }
}