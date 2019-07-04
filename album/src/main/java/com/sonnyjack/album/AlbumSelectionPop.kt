package com.sonnyjack.album


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sonnyjack.album.bean.ImageFolder

class AlbumSelectionPop : PopupWindow {

    private var mContext: Context
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerView.Adapter<ViewHolder>
    private var mAlbumSelectionPopCallBack: AlbumSelectionPopCallBack? = null

    constructor(context: Context) : super(context) {
        this.mContext = context
        initView()
    }

    fun setAlbumSelectionPopCallBack(albumSelectionPopCallBack: AlbumSelectionPopCallBack) {
        mAlbumSelectionPopCallBack = albumSelectionPopCallBack
    }

    private fun initView() {
        var view = LayoutInflater.from(mContext).inflate(R.layout.album_layout_pop, null)
        contentView = view
        mRecyclerView = contentView.findViewById(R.id.album_rv_pop_menu)

        var layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        mRecyclerView!!.layoutManager = layoutManager

        //contentView.setOnClickListener { dismiss() }

        animationStyle = R.style.popAnimStyle

        isFocusable = true
        isOutsideTouchable = true
        setBackgroundDrawable(null)
        width = ViewGroup.LayoutParams.MATCH_PARENT
        //height = AlbumImageUtils.getScreenHeight(mContext) - mContext.getDimenSize(R.dimen.album_bottom_menu_height)

    }

    fun setData(data: ArrayList<ImageFolder>, selectionImageFolder: ImageFolder?) {
        var maxSize = 4
        var itemHeight = mContext.getDimenSize(R.dimen.album_pop_image_size)
        +mContext.getDimenSize(R.dimen.album_pop_image_top)
        +mContext.getDimenSize(R.dimen.album_pop_image_bottom) + 55f.dp(mContext)
        var totalHeight = Math.min(data.size, maxSize) * itemHeight
        //mRecyclerView.layoutParams.height = totalHeight
        height = totalHeight

        mAdapter = object : RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                var view = LayoutInflater.from(parent.context).inflate(R.layout.album_recycler_item_pop, parent, false)
                return ViewHolder(view)
            }

            override fun getItemCount(): Int {
                return data?.size
            }

            private fun getItem(position: Int): ImageFolder? {
                if (position < 0 || position >= data.size) return null
                return data[position]
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val item = getItem(position)
                item ?: return
                var imageView = holder.getView<ImageView>(R.id.album_iv_pop_image)
                Glide.with(holder.getContext()).load(item.firstImagePath).into(imageView)
                var tvName = holder.getView<TextView>(R.id.album_tv_pop_name)
                tvName.text = item.name
                var tvNum = holder.getView<TextView>(R.id.album_tv_pop_num)
                tvNum.text = item.images.size.toString().plus("张")
                var selectImageView = holder.getView<ImageView>(R.id.album_iv_pop_select)
                if (null != selectionImageFolder && selectionImageFolder == item) {
                    selectImageView.visibility = View.VISIBLE
                } else {
                    selectImageView.visibility = View.GONE
                }
                holder.itemView.setOnClickListener {
                    mAlbumSelectionPopCallBack?.call(getItem(position), position, null)
                }
            }

        }
        mRecyclerView!!.adapter = mAdapter
    }

    interface AlbumSelectionPopCallBack {
        fun call(imageFolder: ImageFolder?, position: Int, obj: Any?)
    }
}
