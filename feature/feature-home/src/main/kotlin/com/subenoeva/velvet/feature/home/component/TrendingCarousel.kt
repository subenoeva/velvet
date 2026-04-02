package com.subenoeva.velvet.feature.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.ui.component.MovieCardLarge

@Composable
fun TrendingCarousel(
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(movies, key = { it.id }) { movie ->
            MovieCardLarge(
                modifier = Modifier.width(300.dp),
                backdropPath = movie.backdropPath,
                title = movie.title,
                overview = movie.overview,
                rating = movie.voteAverage,
                onClick = { onMovieClick(movie.id) }
            )
        }
    }
}
