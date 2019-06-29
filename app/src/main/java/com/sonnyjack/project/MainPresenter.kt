package com.sonnyjack.project

import com.sonnyjack.library.mvp.BasePresenter

class MainPresenter : BasePresenter<MainContract.BaseMainView, MainContract.BaseMainModel> {

    constructor(v: MainContract.BaseMainView) : super(v, MainModel())


    fun requestData(){
        val requestData = mModel.requestData()
        mView.requestDataResult(requestData)
    }
}