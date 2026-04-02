package com.subenoeva.velvet.feature.home

import app.cash.turbine.test
import com.subenoeva.velvet.core.common.dispatcher.DispatcherProvider
import com.subenoeva.velvet.core.common.result.Result
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.usecase.NoParams
import com.subenoeva.velvet.core.domain.usecase.movie.GetPopularPreviewUseCase
import com.subenoeva.velvet.core.domain.usecase.movie.GetTopRatedPreviewUseCase
import com.subenoeva.velvet.core.domain.usecase.movie.GetTrendingMoviesUseCase
import com.subenoeva.velvet.core.domain.usecase.movie.GetUpcomingPreviewUseCase
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

    private val getTrending: GetTrendingMoviesUseCase = mockk()
    private val getPopularPreview: GetPopularPreviewUseCase = mockk()
    private val getTopRatedPreview: GetTopRatedPreviewUseCase = mockk()
    private val getUpcomingPreview: GetUpcomingPreviewUseCase = mockk()
    private val dispatchers: DispatcherProvider = mockk()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getTrending(NoParams) } returns flowOf<Result<List<Movie>>>(Result.Success(emptyList()))
        every { getPopularPreview(NoParams) } returns flowOf<Result<List<Movie>>>(Result.Success(emptyList()))
        every { getTopRatedPreview(NoParams) } returns flowOf<Result<List<Movie>>>(Result.Success(emptyList()))
        every { getUpcomingPreview(NoParams) } returns flowOf<Result<List<Movie>>>(Result.Success(emptyList()))
        every { dispatchers.io } returns testDispatcher
        viewModel = HomeViewModel(getTrending, getPopularPreview, getTopRatedPreview, getUpcomingPreview, dispatchers)
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

    @Test
    fun `initial load populates state from use cases`() = runTest {
        val trendingMovies = listOf(Movie(1, "Movie 1", "", null, null, 7.5, "2024-01-01", emptyList()))
        every { getTrending.invoke(NoParams) } returns flowOf<Result<List<Movie>>>(Result.Success(trendingMovies))

        val vm = HomeViewModel(getTrending, getPopularPreview, getTopRatedPreview, getUpcomingPreview, dispatchers)

        vm.state.test {
            val state = awaitItem()
            assertEquals(trendingMovies, state.trending)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
