package com.sonnyjack.project

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.sonnyjack.library.base.BaseActivity
import com.sonnyjack.project.http.HttpActivity
import com.sonnyjack.project.image.ImageActivity

class MainActivity : BaseActivity<MainPresenter>(), MainContract.BaseMainView {
    override fun requestDataResult(value: String) {
        showToast(value)
    }

    override fun createPresenter(): MainPresenter {
        return MainPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.tv).setOnClickListener {
            getPresenter().requestData()
        }

        findViewById<View>(R.id.btnHttp).setOnClickListener {
            startActivity(Intent(getActivity(), HttpActivity::class.java))
        }

        findViewById<View>(R.id.btnImage).setOnClickListener {
            startActivity(Intent(getActivity(), ImageActivity::class.java))
        }
    }
}
