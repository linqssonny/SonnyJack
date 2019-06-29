package com.sonnyjack.project.app

import com.sonnyjack.library.base.BaseActivityLifecycleCallbacks
import com.sonnyjack.library.base.BaseApplication
import com.sonnyjack.library.image.ImageManager
import com.sonnyjack.project.R

class SonnyJackApplication : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        var imageDisplayOption = ImageManager.createDefaultImageDisplayOption()
        imageDisplayOption.setPlaceholder(R.drawable.ic_launcher_background)
        imageDisplayOption.setError(R.drawable.ic_launcher_foreground)
        ImageManager.init(imageDisplayOption)
    }

    override fun getActivityLifecycleCallbacks(): BaseActivityLifecycleCallbacks {
        //return super.getActivityLifecycleCallbacks()
        return SonnyJackActivityLifecycleCallbacks()
    }
}