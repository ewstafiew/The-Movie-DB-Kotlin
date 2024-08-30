package com.example.moviedb.ui.base.loadmorerefresh

import com.example.moviedb.data.constant.Constants
import com.example.moviedb.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * should use paging 3
 */
abstract class BaseLoadMoreRefreshViewModel<Item> : BaseViewModel() {

    // refresh flag
    val isRefreshing = MutableStateFlow(false)

    // load more flag
    private val isLoadMore = MutableStateFlow(false)
    private var loadMoreTimeMillis = 0L

    // item list
    val itemList = MutableStateFlow(arrayListOf<Item>())

    // empty list flag
    private val isEmptyList = MutableStateFlow(false)

    /**
     * override if first page is not 1
     */
    protected open val firstPage = Constants.DEFAULT_FIRST_PAGE

    /**
     * override if need change number visible threshold
     */
    protected open val loadMoreThreshold = Constants.DEFAULT_NUM_VISIBLE_THRESHOLD

    /**
     * override if need change number item per page
     */
    protected open val pageSize = Constants.DEFAULT_PAGE_SIZE

    private fun getCurrentPage(): Int {
        val itemSize = itemList.value.size
        return itemSize / pageSize + if (itemSize % pageSize == 0) 0 else 1
    }

    private fun isLastPage(): Boolean {
        return itemList.value.size % pageSize != 0
    }

    /**
     * load data
     */
    abstract fun loadData(page: Int)

    /**
     * first load
     */
    fun firstLoad() {
        if (getCurrentPage() == firstPage - 1 && itemList.value.isEmpty()) {
            showLoading()
            loadData(page = firstPage)
        }
    }

    fun doRefresh() {
        when {
            isLoading() || isRefreshing.value -> {}

            else -> {
                isRefreshing.value = true
                loadData(page = firstPage)
            }
        }
    }

    fun checkLoadMore(position: Int) {
//        Timber.v("Check load more on $position")
        if (itemList.value.size - position < loadMoreThreshold) {
            when {
                isLoading()
                        || isRefreshing.value
                        || isLoadMore.value
                        || isLastPage()
                        || System.currentTimeMillis() - loadMoreTimeMillis < 2_000 -> {
                }

                else -> {
                    isLoadMore.value = true
                    loadMoreTimeMillis = System.currentTimeMillis()
                    loadData(page = getCurrentPage() + 1)
                }
            }
        }
    }

    /**
     * handle load success
     */
    fun onLoadSuccess(page: Int, items: List<Item>?) {
        // case load first page then clear data from listItem
        if (page == firstPage) itemList.value.clear()
        // add new data to listItem
        if (items?.isNotEmpty() == true) {
            itemList.value = arrayListOf<Item>().apply {
                addAll(itemList.value)
                addAll(items)
            }
        }
        // reset load
        isRefreshing.value = false
        isLoadMore.value = false
        // check empty list
        checkEmptyList()
    }

    /**
     * handle load fail
     */
    override fun onError(throwable: Throwable) {
        super.onError(throwable)
        // reset load
        isRefreshing.value = false
        isLoadMore.value = false
        // check empty list
        checkEmptyList()
    }

    /**
     * check list is empty
     */
    private fun checkEmptyList() {
        isEmptyList.value = itemList.value.isEmpty()
    }
}
