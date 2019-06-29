package com.sonnyjack.project.image

import com.sonnyjack.library.mvp.BaseModel
import com.sonnyjack.library.mvp.BaseView

interface ImageContract {
    interface BaseImageView : BaseView {
    }

    interface BaseImageModel : BaseModel {
    }
}