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
                "http://seopic.699pic.com/photo/50037/1038.jpg_wh1200.jpg"
            )
            //ImageManager.displayImage(imageView, "")
        }

        findViewById<View>(R.id.tvAlbum).setOnClickListener {
            AlbumImageUtils.openAlbum(this, AlbumType.IMAGE, true, 5)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AlbumSelectionActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val parcelableArrayListExtra =
                data?.getParcelableArrayListExtra<ImageItem>(AlbumSelectionActivity.DATA)
            val dataType = data?.getIntExtra(AlbumSelectionActivity.DATA_TYPE, AlbumType.IMAGE)
            parcelableArrayListExtra?.run {
                if (dataType == AlbumType.IMAGE) {
                    ImageManager.displayImage(imageView, get(0).uri)
                }
            }
        }
    }
}