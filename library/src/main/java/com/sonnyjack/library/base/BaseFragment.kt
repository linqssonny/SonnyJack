package com.sonnyjack.library.base

import android.os.Handler
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sonnyjack.library.mvp.BaseModel
import com.sonnyjack.library.mvp.BasePresenter
import com.sonnyjack.library.mvp.BaseView

abstract class BaseFragment<P : BasePresenter<BaseView, BaseModel>> : Fragment() {

    var mPresenter: P? = null
    val mHandler: Handler by lazy {
        Handler()
    }

    //实现IView的方法
    fun showToast(message: String?) {
        if (message.isNullOrEmpty()) {
            return
        }
        activity?.runOnUiThread {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
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
}