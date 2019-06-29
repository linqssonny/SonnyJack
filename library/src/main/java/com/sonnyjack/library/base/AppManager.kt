package com.sonnyjack.library.base

import android.app.Activity
import android.os.Build
import java.util.*


object AppManager {

    private val activityList = ArrayList<Activity>()

    fun addActivity(activity: Activity?) {
        activity ?: return
        var index = activityList.indexOf(activity)
        if (index >= 0) {
            if (index != activityList.size - 1) {
                activityList.remove(activity)
                activityList.add(activity)
            }
        } else {
            activityList.add(activity)
        }
    }

    fun removeActivity(activity: Activity?) {
        activity ?: return
        activityList.remove(activity)
    }

    fun getCurrentActivity(): Activity? {
        if (!activityList.isNullOrEmpty()) {
            for (i in activityList.size - 1 downTo 0) {
                var activity = activityList[i]
                if (null == activity || activity.isFinishing || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed)) {
                    continue
                }
                return activity
            }
        }
        return null
    }
}