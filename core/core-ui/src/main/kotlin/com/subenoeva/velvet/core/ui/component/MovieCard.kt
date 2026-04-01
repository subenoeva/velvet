package com.subenoeva.velvet.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.subenoeva.velvet.core.ui.theme.VelvetCard
import com.subenoeva.velvet.core.ui.theme.VelvetText

private const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

@Composable
fun MovieCard(
    posterPath: String?,
    title: String,
    rating: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(140.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(VelvetCard)
            .clickable { onClick() }
    ) {
        Box {
            if (posterPath.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .background(VelvetCard)
                )
            } else {
                AsyncImage(
                    model = "$TMDB_IMAGE_BASE_URL$posterPath",
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                )
            }
            RatingBadge(
                rating = rating,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-8).dp, y = 8.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = VelvetText,
            maxLines = 2,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 10.dp)
        )
    }
}
