package com.sonnyjack.project.image

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.libalum.album.AlbumSelectionActivity
import com.sonnyjack.album.AlbumImageUtils
import com.sonnyjack.album.bean.AlbumType
import com.sonnyjack.album.bean.ImageItem
import com.sonnyjack.library.base.BaseActivity
import com.sonnyjack.library.image.ImageManager
import com.sonnyjack.project.R

class ImageActivity : BaseActivity<ImagePresenter>(), ImageContract.BaseImageView {

    lateinit var imageView: ImageView

    override fun createPresenter(): ImagePresenter {
        return ImagePresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        imageView = findViewById(R.id.ivImage)

        findViewById<View>(R.id.tvImage).setOnClickListener {
            ImageManager.displayImage(
                imageView,
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1558542532701&di=305e1924992547f564cda24958871186&imgtype=0&src=http%3A%2F%2Fimg2.ph.126.net%2F8Y1u9aYRhqT4KHumTO_y1w%3D%3D%2F6619210632305894354.jpg"
            )
            //ImageManager.displayImage(imageView, "")
        }

        findViewById<View>(R.id.tvAlbum).setOnClickListener {
            AlbumImageUtils.openAlbum(this, AlbumType.IMAGE, false, 5)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AlbumSelectionActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val parcelableArrayListExtra = data?.getParcelableArrayListExtra<ImageItem>(AlbumSelectionActivity.DATA)
            parcelableArrayListExtra?.run {
                ImageManager.displayImage(imageView, get(0).path!!)
            }
        }
    }
}