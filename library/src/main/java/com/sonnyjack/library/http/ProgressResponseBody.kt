package com.sonnyjack.library.http

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException


open class ProgressResponseBody : ResponseBody {

    var responseBody: ResponseBody
    var progressResponseListener: ProgressResponseListener
    private var bufferedSource: BufferedSource? = null

    constructor(
        responseBody: ResponseBody?,
        progressResponseListener: ProgressResponseListener?
    ) {
        this.responseBody = responseBody!!
        this.progressResponseListener = progressResponseListener!!
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source? {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L

            @Throws(IOException::class)
            override fun read(sink: Buffer?, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                progressResponseListener.progress(
                    null, totalBytesRead, responseBody.contentLength(), bytesRead == -1L, null
                )
                return bytesRead
            }
        }
    }
}