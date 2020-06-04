package com.sonnyjack.project.http

import com.sonnyjack.library.mvp.BaseModel
import com.sonnyjack.library.mvp.BaseView
import io.reactivex.rxjava3.core.Observable

interface HttpContract {
    interface HttpView : BaseView {
        fun requestDataResult(value: Data?)
    }

    interface HttpModel : BaseModel {
        fun requestData(): Observable<Data>
    }
}