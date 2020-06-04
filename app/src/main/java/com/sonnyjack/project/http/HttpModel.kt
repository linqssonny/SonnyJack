package com.sonnyjack.project.http

import com.sonnyjack.library.http.HttpManager
import io.reactivex.rxjava3.core.Observable

class HttpModel : HttpContract.HttpModel {
    override fun requestData(): Observable<Data> {
        return HttpManager.instance.createService(IApiService::class.java).get("101010100")
    }
}