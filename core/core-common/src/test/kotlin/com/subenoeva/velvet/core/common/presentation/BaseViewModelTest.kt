package com.subenoeva.velvet.core.common.presentation

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- test doubles ---

    data class TestState(val count: Int = 0)

    sealed interface TestIntent {
        data object Increment : TestIntent
        data object SendEvent : TestIntent
    }

    sealed interface TestEvent {
        data object Done : TestEvent
    }

    private inner class TestViewModel : BaseViewModel<TestState, TestIntent, TestEvent>(TestState()) {
        override suspend fun handleIntent(intent: TestIntent) {
            when (intent) {
                TestIntent.Increment -> updateState { copy(count = count + 1) }
                TestIntent.SendEvent -> sendEvent(TestEvent.Done)
            }
        }
    }

    // --- tests ---

    @Test
    fun `initial state is emitted`() = runTest {
        val vm = TestViewModel()
        vm.uiState.test {
            assertEquals(TestState(0), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setState updates uiState`() = runTest {
        val vm = TestViewModel()
        vm.uiState.test {
            assertEquals(TestState(0), awaitItem())
            vm.sendIntent(TestIntent.Increment)
            assertEquals(TestState(1), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendEvent emits to uiEvents`() = runTest {
        val vm = TestViewModel()
        vm.uiEvents.test {
            vm.sendIntent(TestIntent.SendEvent)
            assertEquals(TestEvent.Done, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `currentState reflects latest state`() = runTest {
        val vm = TestViewModel()
        vm.sendIntent(TestIntent.Increment)
        assertEquals(1, vm.uiState.value.count)
    }
}
