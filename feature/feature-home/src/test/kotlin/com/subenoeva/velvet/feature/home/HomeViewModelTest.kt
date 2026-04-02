package com.subenoeva.velvet.feature.home

import androidx.paging.PagingData
import app.cash.turbine.test
import com.subenoeva.velvet.core.domain.usecase.NoParams
import com.subenoeva.velvet.core.domain.usecase.movie.GetPopularMoviesUseCase
import com.subenoeva.velvet.feature.home.HomeViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.home.HomeViewContract.Intent.OnMovieClick
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var getPopularMoviesUseCase: GetPopularMoviesUseCase
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getPopularMoviesUseCase = mockk {
            every { invoke(NoParams) } returns flowOf(PagingData.empty())
        }
        viewModel = HomeViewModel(getPopularMoviesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `OnMovieClick emits NavigateToDetail event with correct id`() = runTest {
        viewModel.events.test {
            viewModel.sendIntent(OnMovieClick(42))
            assertEquals(NavigateToDetail(42), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
