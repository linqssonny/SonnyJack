package com.sonnyjack.project.http

import android.text.TextUtils
import com.sonnyjack.library.bean.http.BaseDownloadInfo
import com.sonnyjack.library.http.ApiHttp
import com.sonnyjack.library.http.HttpManager
import com.sonnyjack.library.utils.LibraryUtils
import com.sonnyjack.project.app.SonnyJackApplication
import com.sonnyjack.project.bean.Data
import com.sonnyjack.project.bean.http.DownloadInfo
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class HttpModel : HttpContract.HttpModel {
    override fun requestData(): Observable<Data> {
        return HttpManager.instance.createService(IApiService::class.java).get("101010100")
    }

    override fun download(downloadInfo: DownloadInfo?): Observable<DownloadInfo> {
        val result = downloadInfo ?: DownloadInfo()
        if (TextUtils.isEmpty(result.downloadUrl)) {
            result.code = BaseDownloadInfo.ERROR_CODE_URL_NULL
            return Observable.just(result)
        }
        return HttpManager.instance.createService(ApiHttp::class.java)
            .download(result.downloadUrl!!)
            .subscribeOn(Schedulers.io()).map {
                it.byteStream()
            }.observeOn(Schedulers.computation())
            .doOnNext {
                var filePath = LibraryUtils.createFilePathInExternalFiles(
                    SonnyJackApplication.application,
                    "apk",
                    "test.apk"
                )
                var success = LibraryUtils.writeFileFromIS(filePath, it)
                if (success) {
                    result.code = BaseDownloadInfo.SUCCESS_CODE
                    result.saveFilePath = filePath
                } else {
                    result.code = BaseDownloadInfo.ERROR_CODE_DEFAULT
                    result.message = "下载失败"
                    result.saveFilePath = null
                }
            }.flatMap {
                Observable.just(result)
            }
    }
}