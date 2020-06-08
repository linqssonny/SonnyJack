package com.sonnyjack.project.http

import android.os.Bundle
import android.view.View
import com.sonnyjack.library.base.BaseActivity
import com.sonnyjack.project.R

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

        }
    }

    override fun requestDataResult(value: Data?) {
        showToast(value?.desc)
    }
}
