package com.sonnyjack.project.http

import com.sonnyjack.library.http.HttpManager
import io.reactivex.Observable

class HttpModel : HttpContract.HttpModel {
    override fun requestData(): Observable<Data> {
        HttpManager.instance.init("http://wthrcdn.etouch.cn/")
        return HttpManager.instance.createService(IApiService::class.java).get("101010100")
    }
}