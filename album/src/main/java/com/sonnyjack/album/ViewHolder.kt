package com.sonnyjack.album

import android.content.Context
import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ViewHolder : RecyclerView.ViewHolder {

    private val viewArrays = SparseArray<View>()

    constructor(itemView: View) : super(itemView)

    fun <T> getView(id: Int): T {
        var view = viewArrays[id]
        if (null == view) {
            view = itemView.findViewById(id)
            viewArrays.put(id, view)
        }
        return view as T
    }

    fun getContext(): Context {
        return itemView.context
    }
}