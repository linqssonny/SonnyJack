package com.sonnyjack.album

import android.content.Context

fun Float.dp(context: Context): Int{
    return AlbumImageUtils.dip2px(context, this)
}

fun Context.getDimenSize(resId: Int): Int{
    return resources.getDimensionPixelSize(resId)
}