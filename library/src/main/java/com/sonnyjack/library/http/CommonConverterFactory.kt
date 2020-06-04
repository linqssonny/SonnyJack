package com.sonnyjack.library.http

import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

open class CommonConverterFactory : Converter.Factory {

    private var gson: Gson = Gson()

    constructor()

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {

        return super.responseBodyConverter(type, annotations, retrofit)
    }
}