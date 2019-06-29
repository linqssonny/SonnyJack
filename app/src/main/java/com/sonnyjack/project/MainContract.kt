package com.sonnyjack.project

import com.sonnyjack.library.mvp.BaseModel
import com.sonnyjack.library.mvp.BaseView

interface MainContract {
    interface BaseMainView : BaseView {
        fun requestDataResult(value: String)
    }

    interface BaseMainModel : BaseModel {
        fun requestData(): String
    }
}