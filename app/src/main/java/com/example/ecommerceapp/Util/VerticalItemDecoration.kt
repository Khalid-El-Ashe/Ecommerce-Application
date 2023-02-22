package com.example.ecommerceapp.Util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// this class to add some extra spaces
class VerticalItemDecoration(private val amount: Int = 30) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = amount
    }
}