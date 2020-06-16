package com.sonnyjack.project.http

import com.sonnyjack.project.bean.Data
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface IApiService {
    @GET("weather_mini")
    fun get(@Query("citykey") cityKey: String): Observable<Data>
}