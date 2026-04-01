package com.subenoeva.velvet.core.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.subenoeva.velvet.core.ui.theme.VelvetAccent
import com.subenoeva.velvet.core.ui.theme.VelvetSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VelvetTopBar(
    title: String = "Velvet",
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = VelvetAccent
            )
        },
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = VelvetSurface
        ),
        modifier = modifier
    )
}
