package com.sonnyjack.project.image

import com.sonnyjack.library.mvp.BasePresenter

class ImagePresenter : BasePresenter<ImageContract.BaseImageView, ImageContract.BaseImageModel> {
    constructor(v: ImageContract.BaseImageView) : super(v, ImageModel())
}