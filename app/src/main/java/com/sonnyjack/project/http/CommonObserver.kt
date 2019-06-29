package com.sonnyjack.project.http

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

abstract class CommonObserver<T> : Observer<Data> {
    override fun onComplete() {
    }

    override fun onSubscribe(d: Disposable) {
    }

    override fun onNext(t: Data) {
    }

    override fun onError(e: Throwable) {
    }

    abstract fun onSuccess(t: T)

    fun onFail(code: Int, message: String) {

    }
}