package com.sonnyjack.project

class MainModel : MainContract.BaseMainModel {
    override fun requestData(): String {
        return "取到数据了..."
    }
}
