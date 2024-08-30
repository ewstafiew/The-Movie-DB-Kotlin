package com.example.moviedb.ui.base.items

import com.example.moviedb.data.constant.Constants
import com.example.moviedb.ui.base.BaseViewModel
import com.example.moviedb.ui.base.ErrorType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class ItemsUiState<Item>(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadMore: Boolean = false,
    val items: List<Item> = emptyList(),
    val errorType: ErrorType? = null
)

abstract class ItemsViewModel<Item> : BaseViewModel() {
    private val _itemsUiState = MutableStateFlow(ItemsUiState<Item>())
    val itemsUiState: StateFlow<ItemsUiState<Item>> = _itemsUiState
    private var loadMoreTimeMillis = 0L

    // override if first page is not 1
    private val firstPage = Constants.DEFAULT_FIRST_PAGE

    // empty list flag
    private val isEmptyList = MutableStateFlow(false)

    // override if need change number visible threshold
    protected open val loadMoreThreshold = Constants.DEFAULT_NUM_VISIBLE_THRESHOLD

    // override if need change number item per page
    protected open val pageSize = Constants.DEFAULT_PAGE_SIZE

    private fun getCurrentPage(): Int {
        val itemSize = _itemsUiState.value.items.size
        return itemSize / pageSize + if (itemSize % pageSize == 0) 0 else 1
    }

    private fun isLastPage(): Boolean {
        return _itemsUiState.value.items.size % pageSize != 0
    }

    /**
     * load data
     */
    abstract fun loadData(page: Int)

    /**
     * first load
     */
    fun firstLoad() {
        if (getCurrentPage() == firstPage - 1
            && _itemsUiState.value.items.isEmpty()
        ) {
            _itemsUiState.update {
                it.copy(isLoading = true)
            }
            loadData(page = firstPage)
        }
    }

    fun doRefresh() {
        when {
            _itemsUiState.value.isLoading
                    || _itemsUiState.value.isRefreshing -> {
            }

            else -> {
                _itemsUiState.update {
                    it.copy(isRefreshing = true)
                }
                loadData(page = firstPage)
            }
        }
    }

    fun checkLoadMore(position: Int) {
//        Timber.v("Check load more on $position")
        if (_itemsUiState.value.items.size - position <= loadMoreThreshold) {
            when {
                _itemsUiState.value.isLoading
                        || _itemsUiState.value.isRefreshing
                        || _itemsUiState.value.isLoadMore
                        || isLastPage()
                        || System.currentTimeMillis() - loadMoreTimeMillis < 2000 -> {
                }

                else -> {
                    _itemsUiState.update {
                        it.copy(isLoadMore = true)
                    }
                    loadMoreTimeMillis = System.currentTimeMillis()
                    doLoadMore()
                }
            }
        }
    }

    fun doLoadMore() {
        loadData(page = getCurrentPage() + 1)
    }

    /**
     * handle load success
     */
    fun onLoadSuccess(page: Int, items: List<Item>?) {
        _itemsUiState.update {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                isLoadMore = false,
                items = arrayListOf<Item>().apply {
                    if (page != firstPage) {
                        addAll(_itemsUiState.value.items)
                    }
                    if (items?.isNotEmpty() == true) {
                        addAll(items)
                    }
                },
                errorType = null
            )
        }
        checkEmptyList()
    }

    /**
     * handle load fail
     */
    override fun onError(throwable: Throwable) {
        _itemsUiState.update {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                isLoadMore = false,
                errorType = toErrorType(throwable = throwable)
            )
        }
        checkEmptyList()
    }

    override fun hideError() {
        _itemsUiState.update {
            it.copy(errorType = null)
        }
    }

    /**
     * check list is empty
     */
    private fun checkEmptyList() {
        isEmptyList.value = _itemsUiState.value.items.isEmpty()
    }
}