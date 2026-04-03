package com.subenoeva.velvet.feature.search

import app.cash.turbine.test
import com.subenoeva.velvet.core.domain.usecase.movie.SearchMoviesUseCase
import com.subenoeva.velvet.feature.search.SearchViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.search.SearchViewContract.Intent.OnMovieClick
import com.subenoeva.velvet.feature.search.SearchViewContract.Intent.UpdateQuery
import io.mockk.mockk
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
class SearchViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val searchMovies: SearchMoviesUseCase = mockk(relaxed = true)

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchViewModel(searchMovies)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty query`() {
        assertEquals("", viewModel.state.value.query)
    }

    @Test
    fun `UpdateQuery updates state query`() = runTest {
        viewModel.sendIntent(UpdateQuery("action"))
        assertEquals("action", viewModel.state.value.query)
    }

    @Test
    fun `OnMovieClick emits NavigateToDetail event`() = runTest {
        viewModel.events.test {
            viewModel.sendIntent(OnMovieClick(42))
            assertEquals(NavigateToDetail(42), awaitItem())
        }
    }
}
