package com.sonnyjack.library.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.gyf.immersionbar.ImmersionBar

/**
 * 默认的Activity生命周期回调
 */
open class BaseActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        AppManager.addActivity(activity)
        var statusBarConfig = if (activity is BaseActivity<*>) {
            activity.getStatusConfig()
        } else {
            StatusBarConfig()
        }
        //ImmersionBar用法：https://www.jianshu.com/p/2a884e211a62
        ImmersionBar.with(activity!!)
            .fitsSystemWindows(statusBarConfig.fitsSystemWindows)
            .statusBarDarkFont(statusBarConfig.statusBarDarkFont)
            .statusBarColor(statusBarConfig.statusBarColor)
            .init()
    }

    override fun onActivityPaused(activity: Activity?) {

    }

    override fun onActivityResumed(activity: Activity?) {

    }

    override fun onActivityStarted(activity: Activity?) {

    }

    override fun onActivityDestroyed(activity: Activity?) {
        AppManager.removeActivity(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

    }

    override fun onActivityStopped(activity: Activity?) {

    }
}