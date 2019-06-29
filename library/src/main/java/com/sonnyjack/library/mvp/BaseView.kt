package com.sonnyjack.library.mvp

import android.app.Activity

interface BaseView {
    /**
     * 弹出Toast
     */
    fun showToast(message: String?)

    /**
     * 关闭
     */
    fun killMySelf()

    /**
     * 获取当前的Activity
     */
    fun getActivity(): Activity
}