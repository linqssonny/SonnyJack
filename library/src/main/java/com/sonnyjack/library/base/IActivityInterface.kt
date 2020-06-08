package com.sonnyjack.library.base

import android.app.Activity
import com.sonnyjack.library.mvp.BaseModel
import com.sonnyjack.library.mvp.BasePresenter
import com.sonnyjack.library.mvp.BaseView

interface IActivityInterface<P : BasePresenter<out BaseView, out BaseModel>> {

    open fun showToast(message: String?)

    fun getStatusConfig(): StatusBarConfig

    fun createPresenter(): P

    fun getPresenter(): P

    fun killMySelf()

    fun getActivity(): Activity
}