package com.libalum.album

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sonnyjack.album.AlbumImageUtils
import com.sonnyjack.album.AlbumSelectionPop
import com.sonnyjack.album.ImageTypeUtils
import com.sonnyjack.album.R
import com.sonnyjack.album.bean.AlbumType
import com.sonnyjack.album.bean.ImageFolder
import com.sonnyjack.album.bean.ImageItem
import com.sonnyjack.album.preview.ImagePreviewActivity

/**
 * 相册选择
 */
class AlbumSelectionActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val DEFAULT_VIDEO_LENGTH = 60999//默认最长的视频长度
        const val MAX_NUM = "max_num"//最大的照片数
        const val MAX_VIDEO_LENGTH = "max_video_length"//最长的视频长度
        const val NEED_CROP = "need_crop"//是否需要裁剪
        const val ALBUM_TYPE = "album_type"//打开相册的类型
        const val REQUEST_CODE = 1100
        const val TAKE_PICTURE = 1101//拍照
        const val DATA = "data"//返回的图片
        const val DATA_TYPE = "data_type"//返回的类型

        const val REQUEST_CROP = 1111//裁剪
    }

    private val mColumn = 4//每行4列

    private lateinit var mTvTitle: TextView//标题
    private lateinit var mRvContent: RecyclerView
    private lateinit var mVBg: View
    private lateinit var mTvComplete: TextView//完成
    private lateinit var mTvFolder: TextView//图片文件夹
    private lateinit var mTvPreview: TextView//预览

    private var mIsNeedCrop = true//是否需要裁剪
    private var mMaxNum = 9//最多选中的图片数
    private var mAlbumType = AlbumType.IMAGE
    private var mMaxVideoLength = DEFAULT_VIDEO_LENGTH//视频最长的长度
    private var mShowTakePhoto = true//是否显示拍照

    private var mLoadImageRunnable: LoadImageRunnable? = null//加载图片的线程

    private var mAlbumSelectionAdapter: AlbumSelectionAdapter? = null

    private var mImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_activity_selection)
        initView()
        initLogic()
        initData()
    }

    private fun initView() {
        mTvTitle = findViewById(R.id.album_tv_toolbar_title)//标题
        mRvContent = findViewById(R.id.album_rv_selection_content)
        mVBg = findViewById(R.id.album_v_bg)
        mTvComplete = findViewById(R.id.album_tv_toolbar_complete)//确定按钮
        mTvPreview = findViewById(R.id.album_tv_selection_preview)//预览按钮
        mTvFolder = findViewById(R.id.album_tv_selection_folder)//图片文件夹

        mMaxNum = intent.getIntExtra(MAX_NUM, 1)
        mMaxVideoLength = intent.getIntExtra(MAX_VIDEO_LENGTH, DEFAULT_VIDEO_LENGTH)
        mIsNeedCrop = intent.getBooleanExtra(NEED_CROP, false)
        mAlbumType = intent.getIntExtra(ALBUM_TYPE, AlbumType.IMAGE_AND_VIDEO)

        showBg(false)
    }

    private fun initLogic() {
        //返回键事件
        findViewById<View>(R.id.album_iv_toolbar_back).setOnClickListener(this)
        //确定点击事件
        mTvComplete.setOnClickListener(this)
        //顶部点击事件
        mTvTitle.setOnClickListener(this)
        //预览
        mTvPreview.setOnClickListener(this)
        //图片文件夹
        mTvFolder.setOnClickListener(this)

        //RecyclerView中item的间距
        val space = getImageSpace()
        mRvContent.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                var position = parent.getChildAdapterPosition(view)
                var spanCount = position % mColumn
                var top = 0 // 默认 top为0
                var bottom = space// 默认bottom为间距值
                var left = spanCount * space / (mColumn - 1)
                //var right = space - left
                var right = 0

                outRect.set(left, top, right, bottom)
            }
        })
        var gridLayoutManager = GridLayoutManager(this, mColumn)
        mRvContent.layoutManager = gridLayoutManager

        //adapter
        mAlbumSelectionAdapter = AlbumSelectionAdapter(countImageSize())
        mAlbumSelectionAdapter!!.setAlbumSelectionCallBack(object :
            AlbumSelectionAdapter.AlbumSelectionCallBack {
            override fun onClick(view: View, imageItem: ImageItem, obj: Any?) {
                if (imageItem.type == AlbumType.TAKE_PHOTO) {//拍照
                    mImageUri = AlbumImageUtils.buildImageOutputPathUri(this@AlbumSelectionActivity)
                    mImageUri?.run {
                        AlbumImageUtils.openCamera(this@AlbumSelectionActivity, this)
                    }
                }
            }

            override fun onSelectChange() {
                setCompleteBtnText()
            }
        })
        mAlbumSelectionAdapter!!.initParams(mMaxNum, mMaxVideoLength)
        mRvContent.adapter = mAlbumSelectionAdapter
    }

    private fun initData() {
        setTitleText()
        mLoadImageRunnable = LoadImageRunnable(
            this,
            mAlbumType,
            object : LoadImageRunnable.LoadImageRunnableCallBack {
                override fun complete(imageFolders: ArrayList<ImageFolder>) {
                    if (null == imageFolders || imageFolders.isEmpty()) return
                    loadImageComplete(imageFolders)
                }

                override fun onError() {
                    loadImageError()
                }

            })
        Thread(mLoadImageRunnable).start()
    }

    override fun onClick(v: View?) {
        v ?: return
        when (v.id) {
            R.id.album_iv_toolbar_back -> finish()//返回键
            R.id.album_tv_toolbar_complete -> complete()//确认
            R.id.album_tv_selection_folder -> showPop()//图片文件夹选择pop
            R.id.album_tv_selection_preview -> preview()//预览图片
        }
    }

    //显示选择图片文件夹的Pop
    private fun showPop() {
        var albumSelectionPop = AlbumSelectionPop(this)
        albumSelectionPop.setData(
            getAllImageFolder(),
            mAlbumSelectionAdapter?.getCurrentImageFolder()
        )
        albumSelectionPop.setAlbumSelectionPopCallBack(object :
            AlbumSelectionPop.AlbumSelectionPopCallBack {
            override fun call(imageFolder: ImageFolder?, position: Int, obj: Any?) {
                albumSelectionPop.dismiss()
                changeImageFolder(position)
            }
        })
        albumSelectionPop.setOnDismissListener { showBg(false) }
        var disY = resources.getDimensionPixelSize(R.dimen.album_bottom_menu_height)
        albumSelectionPop.showAtLocation(mRvContent, Gravity.BOTTOM, 0, disY)
        showBg(true)
    }

    private fun showBg(show: Boolean = false) {
        mVBg.visibility = if (show) View.VISIBLE else View.GONE
    }

    //加载图片失败
    private fun loadImageError() {
        Toast.makeText(this, "加载图片失败", Toast.LENGTH_SHORT).show()
    }

    //加载图片成功
    private fun loadImageComplete(imageFolders: ArrayList<ImageFolder>) {
        if (mShowTakePhoto) {//显示拍照
            imageFolders.forEach {
                var imageItem = ImageItem()
                imageItem.type = AlbumType.TAKE_PHOTO
                it.images.add(0, imageItem)
            }
        }
        mAlbumSelectionAdapter?.setAllImageFolders(imageFolders)
        changeImageFolder(0)//默认选中第一个
    }

    private fun changeImageFolder(position: Int) {
        mAlbumSelectionAdapter?.run {
            changeImageFolder(position)
        }
        setImageFolderName()
    }

    //设置底部确定按钮
    private fun setCompleteBtnText() {
        var text = resources.getString(R.string.album_complete_text)
        var previewText = resources.getString(R.string.album_preview_text)
        var selectImagesSize = 0
        mAlbumSelectionAdapter?.run {
            selectImagesSize = getSelectItemsSize()
            if (selectImagesSize > 0) {
                var maxNum = if (currentIsVideoMode()) 1 else mMaxNum//如果选中的是视频的话，则最多只能选择一个
                text = text.plus("(").plus(selectImagesSize).plus("/").plus(maxNum).plus(")")
                previewText = previewText.plus("(").plus(selectImagesSize).plus(")")
            }
        }
        mTvComplete.text = text
        mTvPreview.text = previewText
        mTvComplete.isSelected = selectImagesSize > 0
    }

    private fun setTitleText() {
        var title = when (mAlbumType) {
            AlbumType.IMAGE -> "图片"
            AlbumType.VIDEO -> "视频"
            AlbumType.IMAGE_AND_VIDEO -> "图片和视频"
            else -> "图片"
        }
        mTvTitle.text = title
    }

    private fun setImageFolderName() {
        var imageFolders = getCurrentImageFolder()
        mTvFolder.text = imageFolders?.name
    }

    private fun getAllImageFolder(): ArrayList<ImageFolder> {
        return mAlbumSelectionAdapter!!.getAllImageFolders()
    }

    //当前要显示的文件夹
    private fun getCurrentImageFolder(): ImageFolder? {
        return mAlbumSelectionAdapter?.getCurrentImageFolder()
    }

    //预览图片
    private fun preview() {
        val selectItems = mAlbumSelectionAdapter?.getSelectItems()
        if (null == selectItems || selectItems.isEmpty()) {
            return
        }
        when (selectItems[0].type) {
            AlbumType.IMAGE -> AlbumImageUtils.openPreviewImage(this, selectItems)
        }
    }

    private fun cropImage(imageUri: Uri?) {
        mImageUri = AlbumImageUtils.buildImageOutputPathUri(this@AlbumSelectionActivity)
        AlbumImageUtils.openCropImage(this, imageUri, mImageUri, REQUEST_CROP)

        /*var imageUrl = AlbumImageUtils.buildImageOutputPathUrl(this@AlbumSelectionActivity)
        mImageUri = Uri.parse(imageUrl)
        AlbumImageUtils.openCropImage(this, imageUri, mImageUri, REQUEST_CROP)*/
    }

    //确认返回
    private fun complete() {
        if (null == mAlbumSelectionAdapter) {
            finish()
            return
        }
        var selectItems = mAlbumSelectionAdapter!!.getSelectItems()
        if (selectItems.isNullOrEmpty()) {
            finish()
            return
        }
        //选中的是视频,目前没用，因为点击视频会直接跳到ShortVideoPreviewActivity，要在onActivityResult处理
        if (mAlbumSelectionAdapter!!.currentIsVideoMode()) {
            videoComplete(selectItems[0])
            return
        }
        //是否裁剪
        var firstImageUri = selectItems[0].uri
        var firstImageType = ImageTypeUtils.getImageType(this, firstImageUri)//第一张图片类型
        if (mIsNeedCrop && !"GIF".equals(firstImageType, ignoreCase = true)) {
            cropImage(firstImageUri)
            return
        }
        //选择图片完成
        imageComplete(selectItems)
    }

    private fun videoComplete(videoItem: ImageItem) {
        val intent = Intent()
        var resultArray = ArrayList<ImageItem>()
        resultArray.add(videoItem)
        intent.putParcelableArrayListExtra(DATA, resultArray)
        intent.putExtra(DATA_TYPE, AlbumType.VIDEO)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun imageComplete(selectItems: ArrayList<ImageItem>?) {
        val intent = Intent()
        intent.putParcelableArrayListExtra(DATA, selectItems)
        intent.putExtra(DATA_TYPE, AlbumType.IMAGE)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    //拍照完成后，处理
    private fun takePhotoComplete(imageUri: Uri?) {
        var resultArray = ArrayList<ImageItem>()
        imageUri?.run {
            var imageItem = ImageItem()
            imageItem.path = AlbumImageUtils.imageUri2Path(this@AlbumSelectionActivity, imageUri)
            imageItem.uri = imageUri
            resultArray.add(imageItem)
        }
        if (mIsNeedCrop) {
            cropImage(imageUri)
        } else {
            imageComplete(resultArray)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ImagePreviewActivity.REQUEST_CODE -> {//预览返回
                    imageComplete(data?.getParcelableArrayListExtra(DATA))
                }
                TAKE_PICTURE -> {//拍照
                    takePhotoComplete(mImageUri)
                }
                REQUEST_CROP -> {//裁剪
                    var imageList = ArrayList<ImageItem>()
                    var imageItem = ImageItem()
                    imageItem.uri = mImageUri
                    imageItem.path = AlbumImageUtils.imageUri2Path(this, mImageUri)
                    imageList.add(imageItem)
                    imageComplete(imageList)
                }
            }
        }
    }

    /**
     * 计算每个图片的大小
     */
    private fun countImageSize(): Int {
        var space = AlbumImageUtils.getScreenWidth(this) - (mColumn - 1) * getImageSpace()
        return space / mColumn
    }

    private fun getImageSpace(): Int {
        return AlbumImageUtils.dip2px(this, 2f)
    }

    class Builder {
        private var activity: Activity? = null
        private var maxImage = 0
        private var needCrop = false
        private var albumType = AlbumType.IMAGE


        fun with(activity: Activity): Builder {
            this.activity = activity
            return this
        }

        fun setMaxImage(maxImage: Int): Builder {
            this.maxImage = maxImage
            return this
        }

        fun setNeedCrop(needCrop: Boolean): Builder {
            this.needCrop = needCrop
            return this
        }

        fun setType(type: Int): Builder {
            this.albumType = type
            return this
        }

        fun build(requestCode: Int) {
            var intent = Intent(activity, AlbumSelectionActivity::class.java)
            intent.putExtra(MAX_NUM, maxImage)
            intent.putExtra(NEED_CROP, needCrop)
            intent.putExtra(ALBUM_TYPE, albumType)
            activity?.startActivityForResult(intent, requestCode)
        }
    }
}