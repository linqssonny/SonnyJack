package com.sonnyjack.album.preview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.libalum.album.AlbumSelectionActivity
import com.sonnyjack.album.R
import com.sonnyjack.album.bean.ImageItem
import java.util.*

/**
 * 图片预览
 */
class ImagePreviewActivity : AppCompatActivity(), View.OnClickListener, ViewPager.OnPageChangeListener {

    companion object {
        const val REQUEST_CODE = 1110
    }

    private lateinit var mTvTitle: TextView//标题
    private lateinit var mTvCheck: TextView//选中
    private lateinit var mTvComplete: TextView//完成
    private lateinit var mVpContent: ViewPager

    private var mSelectItem = ArrayList<ImageItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)
        initView()
        initLogic()
        initData()
    }

    private fun initView() {
        mTvTitle = findViewById(R.id.album_tv_toolbar_title)//标题
        mTvCheck = findViewById(R.id.album_tv_preview_choose)//选中
        mTvComplete = findViewById(R.id.album_tv_toolbar_complete)//完成
        mVpContent = findViewById(R.id.album_vp_preview_content)//ViewPager
    }

    private fun initLogic() {
        //返回键事件
        findViewById<View>(R.id.album_iv_toolbar_back).setOnClickListener(this)
        //完成
        mTvComplete.setOnClickListener(this)
        //选中
        mTvCheck.setOnClickListener(this)
    }

    private fun initData() {
        var selectItem = intent.getParcelableArrayListExtra<ImageItem>(AlbumSelectionActivity.DATA)
        if (selectItem.isNullOrEmpty()) {
            finish()
            return
        }
        mSelectItem.addAll(selectItem)
        initVpContent()
        initTitle()
        initCheckStatus(0)
        initCompleteText()
    }

    override fun onClick(v: View?) {
        v ?: return
        when (v.id) {
            R.id.album_iv_toolbar_back -> finish()//返回键
            R.id.album_tv_toolbar_complete -> complete()//完成
            R.id.album_tv_preview_choose -> check()//选中
        }
    }

    private fun initVpContent() {
        var imagePreviewAdapter = ImagePreviewAdapter(mSelectItem)
        mVpContent.adapter = imagePreviewAdapter
        mVpContent.addOnPageChangeListener(this)
    }

    private fun initTitle() {
        var position = mVpContent.currentItem + 1
        var title = position.toString() + "/" + mSelectItem.size
        mTvTitle.text = title
    }

    private fun initCheckStatus(position: Int = 0) {
        val imageItem = mSelectItem[position]
        mTvCheck.isSelected = imageItem.select
    }

    private fun initCompleteText() {
        var selectImagesSize = getSelectItemsSize()
        var text = resources.getString(R.string.album_complete_text)
        if (selectImagesSize > 0) {
            text = text.plus("(").plus(selectImagesSize).plus("/").plus(mSelectItem.size).plus(")")
        }
        mTvComplete.text = text
    }

    private fun getSelectItemsSize(): Int {
        if (mSelectItem.isNullOrEmpty()) {
            return 0
        }
        var num = 0
        mSelectItem.forEach {
            if (it.select) {
                num += 1
            }
        }
        return num
    }

    /**
     * 选中
     */
    private fun check() {
        val currentItem = mVpContent.currentItem
        val imageItem = mSelectItem[currentItem]
        var select = !imageItem.select
        imageItem.select = select
        initCheckStatus(currentItem)
        initCompleteText()
    }

    /**
     * 完成
     */
    private fun complete() {
        if (mSelectItem.isNullOrEmpty()) {
            finish()
            return
        }
        var selectItems = ArrayList<ImageItem>()
        mSelectItem.forEach {
            it?.run {
                if (select) {
                    selectItems.add(this)
                }
            }
        }
        if (!selectItems.isNullOrEmpty()) {
            var intent = Intent()
            intent.putParcelableArrayListExtra(AlbumSelectionActivity.DATA, selectItems)
            setResult(Activity.RESULT_OK, intent)
        }
        finish()
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        initCheckStatus(position)
        initTitle()
    }

    class Builder {
        private var activity: Activity? = null
        private var selectItems: ArrayList<ImageItem>? = null


        fun with(activity: Activity): Builder {
            this.activity = activity
            return this
        }

        fun setSelectItems(selectItems: ArrayList<ImageItem>?): Builder {
            this.selectItems = selectItems
            return this
        }

        fun build() {
            var intent = Intent(activity, ImagePreviewActivity::class.java)
            intent.putExtra(AlbumSelectionActivity.DATA, selectItems)
            activity?.startActivityForResult(intent, REQUEST_CODE)
        }
    }
}
