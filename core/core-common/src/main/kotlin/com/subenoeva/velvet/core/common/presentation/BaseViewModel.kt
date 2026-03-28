package com.subenoeva.velvet.core.common.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<UiState, UiIntent, UiEvent>(
    initialState: UiState
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    protected fun setState(newState: UiState) { _uiState.value = newState }
    protected fun updateState(transform: UiState.() -> UiState) { _uiState.update { transform(it) } }
    protected fun sendEvent(event: UiEvent) { _uiEvents.trySend(event) }

    private val uiIntents = MutableSharedFlow<UiIntent>(extraBufferCapacity = 64)

    init {
        viewModelScope.launch { uiIntents.collect { intent -> handleIntent(intent) } }
    }

    fun sendIntent(intent: UiIntent) { uiIntents.tryEmit(intent) }
    protected abstract suspend fun handleIntent(intent: UiIntent)
    protected val currentState: UiState get() = _uiState.value
}
