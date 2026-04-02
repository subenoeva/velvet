package com.subenoeva.velvet.core.common.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun <E> ObserveEvents(
    flow: Flow<E>,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    onEvent: (E) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(flow, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            flow.collect { event ->
                onEvent(event)
            }
        }
    }
}