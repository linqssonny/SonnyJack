package com.sonnyjack.project.http

import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.sonnyjack.library.base.BaseActivity
import com.sonnyjack.library.http.HttpManager
import com.sonnyjack.library.http.ProgressResponseListener
import com.sonnyjack.project.R
import com.sonnyjack.project.bean.Data
import com.sonnyjack.project.bean.http.DownloadInfo

class HttpActivity : BaseActivity<HttpPresenter>(), HttpContract.HttpView {

    override fun createPresenter(): HttpPresenter {
        return HttpPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http)

        findViewById<View>(R.id.btnPost).setOnClickListener {

        }

        findViewById<View>(R.id.btnGet).setOnClickListener {
            getPresenter().requestData()
        }

        findViewById<View>(R.id.btnDownload).setOnClickListener {
            getPresenter().download()
        }

        HttpManager.instance.addProgressResponseListener(
            "http://dldir1.qq.com/weixin/android/weixin7015android1680_arm64.apk",
            object : ProgressResponseListener {
                override fun progress(
                    url: String?,
                    progress: Long,
                    contentLong: Long,
                    done: Boolean,
                    obj: Any?
                ) {
                    val message = (progress / contentLong * 100).toString() + "%"
                    Log.e("HttpActivity", message)
                }
            })
    }

    override fun requestDataResult(value: Data?) {
        showToast(value?.desc)
    }

    override fun downloadResult(downloadInfo: DownloadInfo) {
        if (downloadInfo.isSuccess()) {
            AppUtils.installApp(downloadInfo.saveFilePath)
        } else {
            showToast(downloadInfo.message)
        }
    }
}
