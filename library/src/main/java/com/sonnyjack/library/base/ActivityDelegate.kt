package com.sonnyjack.library.base

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.sonnyjack.library.mvp.BaseModel
import com.sonnyjack.library.mvp.BasePresenter
import com.sonnyjack.library.mvp.BaseView

class ActivityDelegate {

    var activity: Activity
    lateinit var presenter: BasePresenter<out BaseView, out BaseModel>

    val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    constructor(activity: Activity) {
        this.activity = activity
    }

    fun initPresenter(presenter: BasePresenter<out BaseView, out BaseModel>) {
        this.presenter = presenter
    }

    fun showToast(message: String?) {
        if (message.isNullOrEmpty()) {
            return
        }
        activity.runOnUiThread {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    fun killMySelf() {
        activity.finish()
    }

    fun onDestroy() {
        presenter?.run {
            detachView()
        }
        handler.run {
            removeCallbacksAndMessages(null)
        }
    }

    fun getStatusConfig(): StatusBarConfig {
        return StatusBarConfig()
    }

    fun createPresenter(clazz: Class<out BasePresenter<out BaseView, out BaseModel>>): Class<*> {
        val newInstance = clazz.newInstance() as Class<*>
        return newInstance
    }
}