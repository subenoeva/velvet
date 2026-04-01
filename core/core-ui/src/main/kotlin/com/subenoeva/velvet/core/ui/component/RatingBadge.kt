package com.subenoeva.velvet.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.subenoeva.velvet.core.ui.theme.RatingHigh
import com.subenoeva.velvet.core.ui.theme.RatingLow
import com.subenoeva.velvet.core.ui.theme.RatingMid

@Composable
fun RatingBadge(rating: Double, modifier: Modifier = Modifier) {
    val color = when {
        rating >= 7.0 -> RatingHigh
        rating >= 5.0 -> RatingMid
        else -> RatingLow
    }
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = String.format("%.1f", rating),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
