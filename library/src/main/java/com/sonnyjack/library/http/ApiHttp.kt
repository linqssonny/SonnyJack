package com.sonnyjack.library.http

import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ApiHttp {

    /**
     * 下载
     */
    @Streaming
    @GET
    fun download(@Url downloadUrl: String): Observable<ResponseBody>
}