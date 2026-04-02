package com.subenoeva.velvet.feature.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.ui.component.MovieCard
import com.subenoeva.velvet.core.ui.theme.VelvetText

@Composable
fun SimilarMoviesRow(movies: List<Movie>, onMovieClick: (Int) -> Unit) {
    Column {
        Text(
            text = "Películas similares",
            style = MaterialTheme.typography.titleMedium,
            color = VelvetText,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(movies, key = { it.id }) { movie ->
                MovieCard(
                    modifier = Modifier.width(120.dp),
                    posterPath = movie.posterPath,
                    title = movie.title,
                    rating = movie.voteAverage,
                    onClick = { onMovieClick(movie.id) }
                )
            }
        }
    }
}
