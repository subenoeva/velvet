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

abstract class BaseViewModel<State, Intent, Event>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _events = Channel<Event>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val intents = MutableSharedFlow<Intent>(extraBufferCapacity = 64)

    init {
        viewModelScope.launch {
            intents.collect { intent ->
                handleIntent(intent)
            }
        }
    }

    fun sendIntent(intent: Intent) { intents.tryEmit(intent) }

    protected fun setState(newState: State) { _state.value = newState }
    protected fun updateState(transform: State.() -> State) { _state.update { transform(it) } }
    protected fun sendEvent(event: Event) { _events.trySend(event) }

    protected abstract suspend fun handleIntent(intent: Intent)
    protected val currentState: State get() = _state.value
}