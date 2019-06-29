package com.sonnyjack.library.base

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sonnyjack.library.mvp.BaseModel
import com.sonnyjack.library.mvp.BasePresenter
import com.sonnyjack.library.mvp.BaseView

abstract class BaseActivity<P : BasePresenter<out BaseView, out BaseModel>> : AppCompatActivity() {

    val mHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }
    lateinit var mPresenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        mPresenter = createPresenter()
        super.onCreate(savedInstanceState)
    }

    //实现IView的方法
    fun showToast(message: String?) {
        if (message.isNullOrEmpty()) {
            return
        }
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    fun killMySelf() {
        finish()
    }

    fun getActivity(): Activity {
        return this
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.run {
            detachView()
        }
        mHandler?.run {
            removeCallbacksAndMessages(null)
        }
    }

    abstract fun createPresenter(): P

    fun getStatusConfig(): StatusBarConfig {
        return StatusBarConfig()
    }
}
