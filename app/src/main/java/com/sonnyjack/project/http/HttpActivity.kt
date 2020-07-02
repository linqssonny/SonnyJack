package com.sonnyjack.project.http

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.sonnyjack.library.base.BaseActivity
import com.sonnyjack.library.http.HttpManager
import com.sonnyjack.library.http.ProgressResponseListener
import com.sonnyjack.project.R
import com.sonnyjack.project.bean.Data
import com.sonnyjack.project.bean.http.DownloadInfo

class HttpActivity : BaseActivity<HttpPresenter>(), HttpContract.HttpView {

    var txtProgress: TextView? = null

    override fun createPresenter(): HttpPresenter {
        return HttpPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http)

        txtProgress = findViewById(R.id.txtDownloadProgress)

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
                    //val message = String.format("%.2f", progress.toFloat() / contentLong * 100) + "%"
                    val message = (progress.toFloat() / contentLong * 100).toInt().toString() + "%"
                    txtProgress?.text = "进度".plus(message)
                    Log.e("HttpActivity", message)
                }
            })
    }

    override fun requestDataResult(value: Data?) {
        showToast(value?.desc)
    }

    override fun downloadResult(downloadInfo: DownloadInfo) {
        if (downloadInfo.isSuccess()) {
            showToast("下载完成...")
            AppUtils.installApp(downloadInfo.saveFilePath)
        } else {
            showToast(downloadInfo.message)
        }
    }
}
