package com.sonnyjack.album.preview

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.sonnyjack.album.bean.ImageItem
import java.util.ArrayList

class ImagePreviewAdapter : PagerAdapter {

    private var mImageItemList = ArrayList<ImageItem>()

    constructor(imageItems: ArrayList<ImageItem>) {
        if (!imageItems.isNullOrEmpty()) {
            mImageItemList.addAll(imageItems)
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return mImageItemList.size
    }

    fun getItem(position: Int): ImageItem? {
        if (position < 0 || position >= count) return null
        return mImageItemList[position]
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var photoView = PhotoView(container.context)
        var item = getItem(position)
        item?.run {
            Glide.with(container.context).load(item.path).into(photoView)
        }
        container.addView(photoView)
        return photoView
    }
}