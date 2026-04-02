package com.subenoeva.velvet.feature.detail.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.subenoeva.velvet.core.ui.theme.VelvetAccent
import com.subenoeva.velvet.core.ui.theme.VelvetTextSecondary

@Composable
fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
            tint = if (isFavorite) VelvetAccent else VelvetTextSecondary
        )
    }
}
