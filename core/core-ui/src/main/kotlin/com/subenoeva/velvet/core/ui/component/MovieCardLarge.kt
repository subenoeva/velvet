package com.subenoeva.velvet.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.subenoeva.velvet.core.ui.theme.VelvetCard
import com.subenoeva.velvet.core.ui.theme.VelvetText
import com.subenoeva.velvet.core.ui.theme.VelvetTextSecondary

private const val TMDB_BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w780"

@Composable
fun MovieCardLarge(
    backdropPath: String?,
    title: String,
    overview: String,
    rating: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        if (backdropPath.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(VelvetCard)
            )
        } else {
            AsyncImage(
                model = "$TMDB_BACKDROP_BASE_URL$backdropPath",
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        RatingBadge(
            rating = rating,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = VelvetText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = overview,
                style = MaterialTheme.typography.bodySmall,
                color = VelvetTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
