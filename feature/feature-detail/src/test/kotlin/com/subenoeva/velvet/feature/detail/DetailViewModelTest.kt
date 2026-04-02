package com.subenoeva.velvet.feature.detail

import app.cash.turbine.test
import com.subenoeva.velvet.core.common.dispatcher.DispatcherProvider
import com.subenoeva.velvet.core.common.result.Result
import com.subenoeva.velvet.core.domain.model.Cast
import com.subenoeva.velvet.core.domain.model.Genre
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.model.MovieDetail
import com.subenoeva.velvet.core.domain.model.Video
import com.subenoeva.velvet.core.domain.usecase.favorite.IsFavoriteUseCase
import com.subenoeva.velvet.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.subenoeva.velvet.core.domain.usecase.movie.GetMovieCastUseCase
import com.subenoeva.velvet.core.domain.usecase.movie.GetMovieDetailUseCase
import com.subenoeva.velvet.core.domain.usecase.movie.GetSimilarMoviesUseCase
import com.subenoeva.velvet.feature.detail.DetailViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.detail.DetailViewContract.Event.OpenTrailer
import com.subenoeva.velvet.feature.detail.DetailViewContract.Intent.LoadDetail
import com.subenoeva.velvet.feature.detail.DetailViewContract.Intent.PlayTrailer
import com.subenoeva.velvet.feature.detail.DetailViewContract.Intent.SimilarMovieClicked
import com.subenoeva.velvet.feature.detail.DetailViewContract.Intent.ToggleFavorite
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getMovieDetail: GetMovieDetailUseCase = mockk()
    private val getMovieCast: GetMovieCastUseCase = mockk()
    private val getSimilarMovies: GetSimilarMoviesUseCase = mockk()
    private val isFavorite: IsFavoriteUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk()
    private val dispatchers: DispatcherProvider = mockk()

    private val movieDetail = MovieDetail(
        id = 1,
        title = "Test Movie",
        tagline = "A tagline",
        overview = "An overview",
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        voteAverage = 8.0,
        releaseDate = "2024-01-01",
        runtime = 120,
        genres = listOf(Genre(28, "Action")),
        videos = emptyList()
    )

    private val castList = listOf(Cast(10, "Actor One", "Character One", "/profile.jpg"))
    private val similarMovies = listOf(Movie(2, "Similar Movie", "", null, null, 7.0, "2024-02-01", emptyList()))

    private lateinit var viewModel: DetailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { dispatchers.io } returns testDispatcher
        every { getMovieDetail(any()) } returns flowOf(Result.Success(movieDetail))
        every { getMovieCast(any()) } returns flowOf(Result.Success(castList))
        every { getSimilarMovies(any()) } returns flowOf(Result.Success(similarMovies))
        coEvery { isFavorite(any()) } returns false
        coEvery { toggleFavoriteUseCase(any()) } returns Unit
        viewModel = DetailViewModel(
            getMovieDetail, getMovieCast, getSimilarMovies,
            isFavorite, toggleFavoriteUseCase, dispatchers
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadDetail populates state from use cases`() = runTest {
        viewModel.state.test {
            viewModel.sendIntent(LoadDetail(1))
            val state = expectMostRecentItem()
            assertEquals(movieDetail, state.movie)
            assertEquals(castList, state.cast)
            assertEquals(similarMovies, state.similarMovies)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadDetail sets isFavorite from IsFavoriteUseCase`() = runTest {
        coEvery { isFavorite(1) } returns true

        viewModel.state.test {
            viewModel.sendIntent(LoadDetail(1))
            val state = expectMostRecentItem()
            assertTrue(state.isFavorite)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SimilarMovieClicked emits NavigateToDetail event with correct id`() = runTest {
        viewModel.events.test {
            viewModel.sendIntent(SimilarMovieClicked(99))
            assertEquals(NavigateToDetail(99), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `PlayTrailer emits OpenTrailer event when YouTube trailer exists`() = runTest {
        val youtubeVideo = Video("v1", "abc123", "Official Trailer", "YouTube", "Trailer")
        val detailWithTrailer = movieDetail.copy(videos = listOf(youtubeVideo))
        every { getMovieDetail(any()) } returns flowOf(Result.Success(detailWithTrailer))

        // Load the detail first so state has a movie with videos
        viewModel.sendIntent(LoadDetail(1))

        viewModel.events.test {
            viewModel.sendIntent(PlayTrailer)
            assertEquals(OpenTrailer("abc123"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `PlayTrailer emits no event when no YouTube trailer exists`() = runTest {
        val nonTrailerVideo = Video("v1", "abc123", "Behind the Scenes", "Vimeo", "Featurette")
        val detailWithoutTrailer = movieDetail.copy(videos = listOf(nonTrailerVideo))
        every { getMovieDetail(any()) } returns flowOf(Result.Success(detailWithoutTrailer))

        viewModel.sendIntent(LoadDetail(1))

        viewModel.events.test {
            viewModel.sendIntent(PlayTrailer)
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `PlayTrailer emits no event when movie is null`() = runTest {
        // ViewModel with no LoadDetail called — movie stays null
        val freshVm = DetailViewModel(
            getMovieDetail, getMovieCast, getSimilarMovies,
            isFavorite, toggleFavoriteUseCase, dispatchers
        )

        freshVm.events.test {
            freshVm.sendIntent(PlayTrailer)
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ToggleFavorite calls toggleFavoriteUseCase and updates isFavorite state`() = runTest {
        // isFavorite returns false on LoadDetail call, then true after toggle
        coEvery { isFavorite(any()) } returnsMany listOf(false, true)

        // Load detail so state.movie is populated
        viewModel.sendIntent(LoadDetail(1))
        assertFalse(viewModel.state.value.isFavorite)

        // Toggle — isFavorite now returns true on next call
        viewModel.sendIntent(ToggleFavorite)

        assertTrue(viewModel.state.value.isFavorite)
        coVerify(exactly = 1) { toggleFavoriteUseCase(any()) }
    }
}
