package com.sonnyjack.project.http

import com.sonnyjack.library.mvp.BaseModel
import com.sonnyjack.library.mvp.BaseView
import com.sonnyjack.project.bean.Data
import com.sonnyjack.project.bean.http.DownloadInfo
import io.reactivex.rxjava3.core.Observable

interface HttpContract {
    interface HttpView : BaseView {
        fun requestDataResult(value: Data?)

        fun downloadResult(downloadInfo: DownloadInfo)
    }

    interface HttpModel : BaseModel {
        fun requestData(): Observable<Data>

        //下载
        fun download(downloadInfo: DownloadInfo?): Observable<DownloadInfo>
    }
}