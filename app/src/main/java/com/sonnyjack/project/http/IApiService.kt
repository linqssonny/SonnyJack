package com.sonnyjack.project.http

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IApiService {
    @GET("weather_mini")
    fun get(@Query("citykey") cityKey: String): Observable<Data>
}