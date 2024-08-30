package com.example.moviedb.ui.screen.favoritemovie

import androidx.lifecycle.viewModelScope
import com.example.moviedb.data.model.Movie
import com.example.moviedb.data.repository.UserRepository
import com.example.moviedb.ui.base.loadmorerefresh.BaseLoadMoreRefreshViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteMovieViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseLoadMoreRefreshViewModel<Movie>() {

    override fun loadData(page: Int) {
        viewModelScope.launch {
            try {
                val items = userRepository.getFavoriteLocal(
                    pageSize = pageSize,
                    pageIndex = page
                )
                onLoadSuccess(page = page, items = items)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

}
