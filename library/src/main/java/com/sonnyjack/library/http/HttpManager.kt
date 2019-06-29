package com.sonnyjack.library.http

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class HttpManager {

    private lateinit var baseUrl: String
    private val retrofit: Retrofit by lazy {
        var okHttpClient = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS).build()
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private constructor() {
    }

    fun init(baseUrl: String) {
        init(baseUrl, CONNECT_TIMEOUT, WRITE_TIMEOUT)
    }

    fun init(baseUrl: String, connectTime: Long, writeTime: Long) {
        this.baseUrl = baseUrl
        CONNECT_TIMEOUT = connectTime
        WRITE_TIMEOUT = writeTime
    }

    fun getHttpObject(): Retrofit {
        return retrofit
    }

    companion object {
        private var CONNECT_TIMEOUT = 30 * 1000L
        private var WRITE_TIMEOUT = 30 * 60 * 1000L
        val instance: HttpManager by lazy {
            HttpManager()
        }
    }

    fun <T> createService(clazz: Class<T>): T {
        return getHttpObject().create(clazz)
    }
}