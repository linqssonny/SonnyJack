package com.sonnyjack.library.http

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class HttpManager {

    private lateinit var baseUrl: String
    private lateinit var commonConverterFactory: CommonConverterFactory
    private val urlCallBack = HashMap<String, ProgressResponseListener>()
    private val retrofit: Retrofit by lazy {
        createRetrofit(baseUrl)
    }

    private fun createInterceptor(): Interceptor {
        return Interceptor {
            val originalResponse = it.proceed(it.request())
            val responseBody = ProgressResponseBody(
                originalResponse.body(),
                createResponseProgressListener(originalResponse.body())
            )
            originalResponse.newBuilder().body(responseBody).build()
        }
    }

    private fun createResponseProgressListener(responseBody: ResponseBody?): ProgressResponseListener {
        return object : ProgressResponseListener {
            override fun progress(
                url: String?,
                progress: Long,
                contentLong: Long,
                done: Boolean,
                obj: Any?
            ) {
                if (url.isNullOrEmpty()) return
                val callBack = urlCallBack[url]
                callBack?.run {
                    progress(url, progress, contentLong, done, obj)
                    if (done) {
                        urlCallBack.remove(url)
                    }
                }
            }
        }
    }

    fun addProgressResponseListener(
        url: String?,
        progressResponseListener: ProgressResponseListener?
    ) {
        url ?: return
        progressResponseListener ?: return
        urlCallBack.put(url, progressResponseListener)
    }

    private fun createRetrofit(baseUrl: String, isNew: Boolean = false): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            .addInterceptor(createInterceptor())
            .build()
        val builder = Retrofit.Builder().baseUrl(baseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
        if (!isNew && null != commonConverterFactory) {
            builder.addConverterFactory(commonConverterFactory)
        }
        return builder.build()
    }

    private constructor() {
    }

    fun init(baseUrl: String, commonConverterFactory: CommonConverterFactory) {
        init(baseUrl, CONNECT_TIMEOUT, WRITE_TIMEOUT, commonConverterFactory)
    }

    fun init(
        baseUrl: String,
        connectTime: Long,
        writeTime: Long,
        commonConverterFactory: CommonConverterFactory
    ) {
        this.baseUrl = baseUrl
        CONNECT_TIMEOUT = connectTime
        WRITE_TIMEOUT = writeTime
        this.commonConverterFactory = commonConverterFactory
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

    fun <T> createNewService(baseUrl: String, clazz: Class<T>): T {
        return createRetrofit(baseUrl, true).create(clazz)
    }
}