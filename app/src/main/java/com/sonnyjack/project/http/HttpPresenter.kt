package com.sonnyjack.project.http

import com.sonnyjack.library.mvp.BasePresenter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers

class HttpPresenter : BasePresenter<HttpContract.HttpView, HttpContract.HttpModel> {
    constructor(view: HttpContract.HttpView) : super(view, HttpModel())

    fun requestData() {
        //http://wthrcdn.etouch.cn/weather_mini?citykey=101010100
        mModel.requestData().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer {
                mView.requestDataResult(it)
            }, Consumer {
                mView.showToast(it?.message)
            })
    }
}