package com.sonnyjack.library.base

import android.app.Application

open class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(getActivityLifecycleCallbacks())
    }

    open fun getActivityLifecycleCallbacks(): BaseActivityLifecycleCallbacks {
        return BaseActivityLifecycleCallbacks()
    }
}