package com.golden13way.indigofilms.adapters

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

open class EndlessRecyclerViewScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val onLoadMore: () -> Unit
) : RecyclerView.OnScrollListener() {

    private var previousTotalItemCount = 0
    private var isLoading = true
    private val visibleThreshold = 48

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val totalItemCount = layoutManager.itemCount
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        if (isLoading && totalItemCount > previousTotalItemCount) {
            isLoading = false
            previousTotalItemCount = totalItemCount
        }

        if (!isLoading && lastVisibleItemPosition + visibleThreshold >= totalItemCount) {
            onLoadMore()
            isLoading = true
        }
    }

    // Переопределите метод onLoadMore
    open fun onLoadMore() {
        // Ваш код здесь
    }
}



