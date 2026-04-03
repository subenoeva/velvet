package com.subenoeva.velvet.feature.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.subenoeva.velvet.core.common.presentation.ObserveEvents
import com.subenoeva.velvet.core.ui.component.ErrorState
import com.subenoeva.velvet.core.ui.component.LoadingShimmer
import com.subenoeva.velvet.core.ui.component.RatingBadge
import com.subenoeva.velvet.core.ui.component.VelvetTopBar
import com.subenoeva.velvet.core.ui.theme.VelvetAccent
import com.subenoeva.velvet.core.ui.theme.VelvetBlack
import com.subenoeva.velvet.core.ui.theme.VelvetCard
import com.subenoeva.velvet.core.ui.theme.VelvetSurface
import com.subenoeva.velvet.core.ui.theme.VelvetText
import com.subenoeva.velvet.core.ui.theme.VelvetTextSecondary
import com.subenoeva.velvet.feature.detail.DetailViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.detail.DetailViewContract.Event.OpenTrailer
import com.subenoeva.velvet.feature.detail.DetailViewContract.Intent.LoadDetail
import com.subenoeva.velvet.feature.detail.DetailViewContract.Intent.PlayTrailer
import com.subenoeva.velvet.feature.detail.DetailViewContract.Intent.SimilarMovieClicked
import com.subenoeva.velvet.feature.detail.DetailViewContract.Intent.ToggleFavorite
import com.subenoeva.velvet.feature.detail.DetailViewContract.State
import com.subenoeva.velvet.feature.detail.component.CastRow
import com.subenoeva.velvet.feature.detail.component.FavoriteButton
import com.subenoeva.velvet.feature.detail.component.SimilarMoviesRow
import android.content.Intent as AndroidIntent

private const val TMDB_BACKDROP_URL = "https://image.tmdb.org/t/p/w780"
private const val TMDB_POSTER_URL = "https://image.tmdb.org/t/p/w500"

@Composable
fun DetailScreen(
    movieId: Int,
    onBack: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(movieId) {
        viewModel.sendIntent(LoadDetail(movieId))
    }

    ObserveEvents(viewModel.events) { event ->
        when (event) {
            is OpenTrailer -> {
                val intent = AndroidIntent(
                    AndroidIntent.ACTION_VIEW,
                    "https://www.youtube.com/watch?v=${event.youtubeKey}".toUri()
                )
                runCatching { context.startActivity(intent) }
            }

            is NavigateToDetail -> onNavigateToDetail(event.movieId)
        }
    }

    Scaffold(
        topBar = {
            VelvetTopBar(
                title = state.movie?.title ?: "",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = VelvetText
                        )
                    }
                }
            )
        },
        containerColor = VelvetBlack
    ) { padding ->
        DetailContent(
            state = state,
            modifier = Modifier.padding(padding),
            onRetry = { viewModel.sendIntent(LoadDetail(movieId)) },
            onToggleFavorite = { viewModel.sendIntent(ToggleFavorite) },
            onPlayTrailer = { viewModel.sendIntent(PlayTrailer) },
            onSimilarMovieClick = { viewModel.sendIntent(SimilarMovieClicked(it)) }
        )
    }
}

@Composable
private fun DetailContent(
    state: State,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    onToggleFavorite: () -> Unit,
    onPlayTrailer: () -> Unit,
    onSimilarMovieClick: (Int) -> Unit
) {
    when {
        state.isLoading && state.movie == null -> DetailShimmer(modifier)

        state.error != null && state.movie == null -> ErrorState(
            message = state.error,
            onRetry = onRetry,
            modifier = modifier.fillMaxSize()
        )

        else -> state.movie?.let { movie ->
            LazyColumn(
                modifier = modifier.fillMaxSize()
            ) {
                // Backdrop header
                item {
                    Box {
                        val backdropUrl = movie.backdropPath?.let { "$TMDB_BACKDROP_URL$it" }
                        if (backdropUrl != null) {
                            AsyncImage(
                                model = backdropUrl,
                                contentDescription = movie.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .background(VelvetSurface)
                            )
                        }
                    }
                }

                // Poster + title + actions row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Poster
                        val posterUrl = movie.posterPath?.let { "$TMDB_POSTER_URL$it" }
                        if (posterUrl != null) {
                            AsyncImage(
                                model = posterUrl,
                                contentDescription = movie.title,
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VelvetCard)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = movie.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = VelvetText
                            )
                            if (movie.tagline.isNotBlank()) {
                                Text(
                                    text = movie.tagline,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = VelvetTextSecondary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RatingBadge(rating = movie.voteAverage)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${movie.releaseDate.take(4)} · ${movie.runtime} min",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = VelvetTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row {
                                FavoriteButton(
                                    isFavorite = state.isFavorite,
                                    onClick = onToggleFavorite
                                )
                                val hasTrailer = movie.videos.any { it.isYouTubeTrailer }
                                if (hasTrailer) {
                                    IconButton(onClick = onPlayTrailer) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Ver tráiler",
                                            tint = VelvetAccent
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Genres
                if (movie.genres.isNotEmpty()) {
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            items(movie.genres) { genre ->
                                SuggestionChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            text = genre.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = VelvetText
                                        )
                                    },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = VelvetCard,
                                        labelColor = VelvetText
                                    ),
                                    border = SuggestionChipDefaults.suggestionChipBorder(
                                        enabled = true,
                                        borderColor = VelvetSurface
                                    )
                                )
                            }
                        }
                    }
                }

                // Overview
                if (movie.overview.isNotBlank()) {
                    item {
                        Text(
                            text = movie.overview,
                            style = MaterialTheme.typography.bodyMedium,
                            color = VelvetTextSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                // Cast
                if (state.cast.isNotEmpty()) {
                    item { CastRow(cast = state.cast) }
                }

                // Similar movies
                if (state.similarMovies.isNotEmpty()) {
                    item {
                        SimilarMoviesRow(
                            movies = state.similarMovies,
                            onMovieClick = onSimilarMovieClick
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun DetailShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        LoadingShimmer(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )
        Row(modifier = Modifier.padding(16.dp)) {
            LoadingShimmer(
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                LoadingShimmer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LoadingShimmer(
                    modifier = Modifier
                        .width(160.dp)
                        .height(14.dp)
                )
            }
        }
    }
}
