package com.libalum.album

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sonnyjack.album.AlbumImageUtils
import com.sonnyjack.album.R
import com.sonnyjack.album.ViewHolder
import com.sonnyjack.album.bean.AlbumType
import com.sonnyjack.album.bean.ImageFolder
import com.sonnyjack.album.bean.ImageItem

class AlbumSelectionAdapter : RecyclerView.Adapter<ViewHolder> {

    companion object {
        const val REFRESH_SELECT = "refresh_select"
        const val REFRESH_ENABLE = "refresh_enable"
    }

    //当前选中的类型
    enum class SelectMode {
        NONE, IMAGE, Video
    }

    //所有的图片
    private var mImageFolders = ArrayList<ImageFolder>()

    //当前的相册位置
    private var mCurrentPosition = 0

    private var mMaxImage = 9//选中最大的图片数
    private var mMaxVideoLength = 0//视频的最大长度
    private var mAlbumSelectionCallBack: AlbumSelectionCallBack? = null
    private var mCurrentSelectMode = SelectMode.NONE//当前选中的类型

    private var mImageSize = 0

    constructor(imageSize: Int) {
        mImageSize = imageSize
    }

    fun setAlbumSelectionCallBack(albumSelectionCallBack: AlbumSelectionCallBack) {
        mAlbumSelectionCallBack = albumSelectionCallBack
    }

    fun initParams(maxImage: Int, maxVideoLength: Int) {
        mMaxImage = maxImage
        mMaxVideoLength = maxVideoLength
    }

    fun setAllImageFolders(imageFolders: ArrayList<ImageFolder>) {
        if (imageFolders.isNullOrEmpty()) return
        mImageFolders.addAll(imageFolders)
    }

    fun getAllImageFolders(): ArrayList<ImageFolder> {
        return mImageFolders
    }

    //改变图片文件夹
    fun changeImageFolder(position: Int) {
        if (position < 0 || position >= mImageFolders.size) {
            return
        }
        mCurrentPosition = position
        notifyDataSetChanged()
    }

    //当前要显示的文件夹
    fun getCurrentImageFolder(): ImageFolder? {
        if (mCurrentPosition < 0 || mCurrentPosition >= mImageFolders.size) return null
        return mImageFolders[mCurrentPosition]
    }

    fun getData(): ArrayList<ImageItem>? {
        return getCurrentImageFolder()?.images
    }

    fun getItem(position: Int): ImageItem? {
        return getCurrentImageFolder()?.images?.get(position) ?: null
    }

    private fun needAlpha(item: ImageItem?): Boolean {
        item ?: return false
        if (!item.isEnable && item.type != AlbumType.TAKE_PHOTO) {
            return true
        }
        return false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.album_recycler_item_image, parent, false)
        val layoutParams = RecyclerView.LayoutParams(mImageSize, mImageSize)
        view.layoutParams = layoutParams
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (mCurrentPosition < 0 || mCurrentPosition >= mImageFolders.size) return 0
        return mImageFolders[mCurrentPosition].images?.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNullOrEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        }
        var imageItem = getItem(position)
        imageItem ?: return
        payloads?.forEach {
            var payload = it as String
            if (TextUtils.equals(payload, REFRESH_SELECT)) {
                var checkImage = holder?.getView<ImageView>(R.id.album_iv_selection_check)
                checkImage?.isSelected = imageItem?.select
            }
            if (TextUtils.equals(payload, REFRESH_ENABLE)) {
                //图片
                var imageView = holder?.getView<ImageView>(R.id.album_iv_selection_image)
                imageView?.alpha = if (!needAlpha(imageItem)) 1.0f else 0.5f
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var imageItem = getItem(position)
        imageItem ?: return
        var position = holder.adapterPosition
        //图片
        var imageView = holder.getView<ImageView>(R.id.album_iv_selection_image)
        //check
        var checkImage = holder.getView<ImageView>(R.id.album_iv_selection_check)
        //视频
        var videoIcon = holder.getView<ImageView>(R.id.album_iv_selection_video)
        //时长
        var videoDuration = holder.getView<TextView>(R.id.album_tv_selection_duration)

        imageView.alpha = if (!needAlpha(imageItem)) 1.0f else 0.5f
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        checkImage.visibility = View.VISIBLE
        videoIcon.visibility = View.GONE
        videoDuration.visibility = View.GONE

        if (imageItem.type == AlbumType.TAKE_PHOTO) {
            //拍照
            imageView.scaleType = ImageView.ScaleType.CENTER
            imageView.setImageResource(R.drawable.album_take_photo)
            checkImage.visibility = View.GONE
            imageView.setOnClickListener {
                mAlbumSelectionCallBack?.onClick(
                    imageView,
                    imageItem,
                    null
                )
            }
            return
        }
        //Build.VERSION_CODES.Q
        if (Build.VERSION.SDK_INT >= 29) {
            Glide.with(holder.getContext()).load(imageItem.uri).into(imageView)
        } else {
            Glide.with(holder.getContext()).load(imageItem.path).into(imageView)
        }
        checkImage.isSelected = imageItem.select
        if (imageItem.type == AlbumType.VIDEO) {
            videoIcon.visibility = View.VISIBLE
            videoDuration.visibility = View.VISIBLE
            videoDuration.text = imageItem.getFormatDuration()
        }

        checkImage.setOnClickListener {
            if (!imageItem.isEnable) return@setOnClickListener
            setSelectStatus(holder.getContext(), position, imageItem)
        }

        //点击预览
        imageView.setOnClickListener {
            if (getSelectMode(imageItem) == SelectMode.Video) {//如果选中的是视频，则直接跳到视频预览页
                selectVideo(holder.getContext(), imageItem)
            } else {
                var activity = AlbumImageUtils.getActivity(it)
                activity?.run {
                    var imageArrayList = ArrayList<ImageItem>()
                    imageArrayList.add(imageItem)
                    AlbumImageUtils.openPreviewImage(this, imageArrayList)
                }
            }
        }
    }

    private fun setSelectStatus(context: Context, position: Int, item: ImageItem) {
        var selectOrigin = item.select
        var selectMode = mCurrentSelectMode
        if (selectOrigin) {//原来是选中
            item.select = false
            notifyItemChanged(position, REFRESH_SELECT)
            if (getSelectItemsSize() == 0) {
                selectMode = SelectMode.NONE
            }
        } else {//原来没选中，这次选中
            if (mCurrentSelectMode == SelectMode.NONE) {
                var tempSelectMode = getSelectMode(item)
                selectMode = tempSelectMode
            }
            val selectImages = getSelectItems()
            var selectImagesSize = selectImages.size
            if (selectImagesSize == 1 && mCurrentSelectMode == SelectMode.Video) {
                Toast.makeText(context, "只能选择一个视频", Toast.LENGTH_LONG).show()
                return
            } else if (selectImagesSize == 1 && mMaxImage == 1) {
                selectImages[0].select = false
                item.select = true
                var pos = getData()?.indexOf(selectImages[0]) ?: -1
                if (pos != -1) {
                    notifyItemChanged(pos, REFRESH_SELECT)
                    notifyItemChanged(position, REFRESH_SELECT)
                }
            } else if (selectImagesSize >= mMaxImage) {
                Toast.makeText(context, "最多可选择".plus(mMaxImage).plus("张图片"), Toast.LENGTH_LONG)
                    .show()
                return
            } else {
                item.select = true
                notifyItemChanged(position, REFRESH_SELECT)
            }
        }
        notifySelectMode(selectMode)
        mAlbumSelectionCallBack?.onSelectChange()
    }

    //选中的是视频
    private fun selectVideo(context: Context, item: ImageItem) {
        val videoDuration = item.duration
        if (videoDuration < mMaxVideoLength) {
            /*val intent = Intent(context, ShortVideoPreviewActivity::class.java)
            intent.putExtra(ShortVideoPreviewActivity.EXTRA_PATH, item.path)
            if (mMaxVideoLength > 0) {
                intent.putExtra(ShortVideoPreviewActivity.EXTRA_MAX_LENGTH, mMaxVideoLength)
            }
            (mContext as Activity).startActivityForResult(intent, ImageSDK.REQUEST_CODE_TAKE_VIDEO)*/
        } else {
            Toast.makeText(context, "请选择小于" + mMaxVideoLength / 1000 + "s的视频", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun notifySelectMode(selectMode: SelectMode) {
        if (itemCount <= 0) return
        if (selectMode == mCurrentSelectMode) return
        mCurrentSelectMode = selectMode
        for (i in 0 until itemCount) {
            val item = getItem(i)
            if (mCurrentSelectMode == getSelectMode(item!!) && item.isEnable) {
                continue
            }
            item.run {
                if (mCurrentSelectMode == SelectMode.NONE && !isEnable) {
                    isEnable = true
                    notifyItemChanged(i, REFRESH_ENABLE)
                }
                if (mCurrentSelectMode == SelectMode.IMAGE) {
                    isEnable = item.isImageType()
                    notifyItemChanged(i, REFRESH_ENABLE)
                }
                if (mCurrentSelectMode == SelectMode.Video) {
                    isEnable = item.isVideoType()
                    notifyItemChanged(i, REFRESH_ENABLE)
                }
            }

        }
    }

    //当前选中的mode是否是视频
    fun currentIsVideoMode(): Boolean {
        return mCurrentSelectMode == SelectMode.Video
    }

    private fun getSelectMode(item: ImageItem): SelectMode {
        return if (item.type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) SelectMode.IMAGE
        else SelectMode.Video
    }

    fun getSelectItemsSize(): Int {
        return getSelectItems().size
    }

    fun getSelectItems(): ArrayList<ImageItem> {
        var selectImages = ArrayList<ImageItem>()
        var allImageItemList =
            if (mImageFolders.size > 0) mImageFolders[0].images else ArrayList<ImageItem>()
        if (allImageItemList.size <= 0) return selectImages
        for (i in 0 until allImageItemList.size) {
            val item = allImageItemList[i]
            if (null != item && item.select) {
                selectImages.add(item)
            }
        }
        return selectImages
    }

    interface AlbumSelectionCallBack {
        fun onClick(view: View, imageItem: ImageItem, obj: Any?)
        fun onSelectChange()
    }
}