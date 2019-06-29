package com.sonnyjack.library.mvp

abstract class BasePresenter<V : BaseView, M : BaseModel> {
    var mView: V
    var mModel: M

    constructor(v: V, m: M) {
        mView = v
        mModel = m
        if (null == mView) {
            throw NullPointerException("BaseView is null")
        }
        mView = mView
        mModel = mModel
    }

    fun detachView() {
    }
}

//abstract class ddd<V : BaseView, M : BaseModel>(protected var v: V, protected var m: M)