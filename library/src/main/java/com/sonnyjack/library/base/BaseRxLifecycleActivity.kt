package com.sonnyjack.library.base

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sonnyjack.library.mvp.BaseModel
import com.sonnyjack.library.mvp.BasePresenter
import com.sonnyjack.library.mvp.BaseView

abstract class BaseRxLifecycleActivity<P : BasePresenter<out BaseView, out BaseModel>> :
    AppCompatActivity(),
    IActivityInterface<P> {

    private var activityDelegate: ActivityDelegate = ActivityDelegate(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        activityDelegate.initPresenter(createPresenter())
        super.onCreate(savedInstanceState)
    }

    //实现IView的方法
    override fun showToast(message: String?) {
        activityDelegate.showToast(message)
    }

    override fun killMySelf() {
        activityDelegate.killMySelf()
    }

    override fun getActivity(): Activity {
        return activityDelegate.activity
    }

    override fun onDestroy() {
        super.onDestroy()
        activityDelegate.onDestroy()
    }

    override fun getStatusConfig(): StatusBarConfig {
        return activityDelegate.getStatusConfig()
    }

    override fun getPresenter(): P {
        return activityDelegate.presenter as P
    }
}
