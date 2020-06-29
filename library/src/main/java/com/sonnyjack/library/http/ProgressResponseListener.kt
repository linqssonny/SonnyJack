package com.sonnyjack.library.http

/**
 * 请求结果  进度监听
 */
interface ProgressResponseListener {
    fun progress(url: String?, progress: Long, contentLong: Long, done: Boolean, obj: Any?)
}