package com.sonnyjack.library.base

import com.sonnyjack.library.R

class StatusBarConfig {

    var statusBarColor = R.color.transparent//状态栏颜色
    var statusBarDarkFont = true
    var fitsSystemWindows = true

    fun setStatusBarColor(statusColorResId: Int): StatusBarConfig {
        this.statusBarColor = statusColorResId
        return this
    }

    fun setFitsSystemWindows(fitsSystemWindows: Boolean): StatusBarConfig {
        this.fitsSystemWindows = fitsSystemWindows
        return this
    }

    fun setStatusBarDarkFont(statusBarDarkFont: Boolean): StatusBarConfig {
        this.statusBarDarkFont = statusBarDarkFont
        return this
    }
}