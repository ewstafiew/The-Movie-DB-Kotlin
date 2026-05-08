package com.example.moviedb.compose.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moviedb.BuildConfig
import com.example.moviedb.R
import com.example.moviedb.compose.toMovieDetail
import com.example.moviedb.compose.ui.HomeScreenTags
import com.example.moviedb.compose.ui.testtags.AppiumTags
import com.example.moviedb.data.model.Movie
import com.example.moviedb.ui.screen.popularmovie.PopularMovieViewModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: PopularMovieViewModel = hiltViewModel()
) {
    val uiState by viewModel.itemsUiState.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = viewModel::doRefresh
    )
    val gridState = rememberLazyGridState()
    // Trigger initial data load
    val endOfListReached by remember {
        derivedStateOf {
            gridState.isScrolledToEnd()
        }
    }
    LaunchedEffect(key1 = true, block = {
        viewModel.firstLoad()
    })

    val appiumFallbackMovies = if (BuildConfig.DEBUG && uiState.items.isEmpty()) {
        listOf(
            Movie(
                id = "550",
                title = "Fallback movie",
                overview = "Fallback item for UI automation",
            )
        )
    } else {
        emptyList()
    }

    val moviesToRender = if (uiState.items.isNotEmpty()) uiState.items else appiumFallbackMovies

    Box(
        Modifier
            .pullRefresh(pullRefreshState)
            .fillMaxSize()
            .testTag(HomeScreenTags.ROOT)
            .background(color = Color.Black)
            .semantics { contentDescription = AppiumTags.HOME_SCREEN }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .testTag(HomeScreenTags.GRID)
                .semantics { contentDescription = AppiumTags.HOME_MOVIES_GRID },
            state = gridState
        ) {
            itemsIndexed(
                items = moviesToRender,
                key = { _, movie: Movie -> movie.id }
            ) { index, movie ->
                val itemTag = if (index == 0) {
                    AppiumTags.HOME_FIRST_MOVIE_CARD
                } else {
                    AppiumTags.HOME_MOVIE_ITEM_PREFIX + movie.id
                }
                MovieItem(
                    movie = movie,
                    itemTag = itemTag,
                    onClick = { navController.toMovieDetail(movieId = it.id) }
                )
            }
        }

        PullRefreshIndicator(
            refreshing = uiState.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    LaunchedEffect(key1 = endOfListReached, block = {
        viewModel.doLoadMore()
    })
}

fun LazyGridState.isScrolledToEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 5

@Composable
fun MovieItem(
    movie: Movie,
    itemTag: String,
    onClick: (Movie) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f)
            .testTag(HomeScreenTags.movieItem(movie.id))
            .clickable { onClick.invoke(movie) }
            .semantics { contentDescription = itemTag }
    ) {
        GlideImage(
            imageModel = { movie.getFullPosterPath() ?: "" },
            modifier = Modifier.fillMaxWidth(),
            component = rememberImageComponent {
                +PlaceholderPlugin.Loading(Icons.Filled.Image)
                +PlaceholderPlugin.Failure(Icons.Filled.Error)
            },
            imageOptions = ImageOptions(contentScale = ContentScale.Crop)
        )

        Box(
            modifier = Modifier
                .background(color = colorResource(id = R.color.black_50))
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = movie.title ?: "",
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .testTag(HomeScreenTags.movieTitle(movie.id))
                    .fillMaxWidth()
            )
        }
    }
}