package com.subenoeva.velvet.feature.favorites

import app.cash.turbine.test
import com.subenoeva.velvet.core.domain.model.Movie
import com.subenoeva.velvet.core.domain.usecase.NoParams
import com.subenoeva.velvet.core.domain.usecase.favorite.GetFavoritesUseCase
import com.subenoeva.velvet.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Event.NavigateToDetail
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnExitSelectionMode
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnMovieClick
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnMovieLongPress
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnRemoveFavorite
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnRemoveSelected
import com.subenoeva.velvet.feature.favorites.FavoritesViewContract.Intent.OnToggleSelection
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
class FavoritesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getFavorites: GetFavoritesUseCase = mockk()
    private val toggleFavorite: ToggleFavoriteUseCase = mockk()

    private val movie1 = Movie(1, "Movie One", "", "/p1.jpg", null, 7.5, "2024-01-01", emptyList())
    private val movie2 = Movie(2, "Movie Two", "", "/p2.jpg", null, 8.0, "2024-02-01", emptyList())

    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getFavorites(NoParams) } returns flowOf(listOf(movie1, movie2))
        coEvery { toggleFavorite(any()) } returns Unit
        viewModel = FavoritesViewModel(getFavorites, toggleFavorite)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads favorites and clears isLoading`() = runTest {
        val state = viewModel.state.value
        assertEquals(listOf(movie1, movie2), state.movies)
        assertFalse(state.isLoading)
    }

    @Test
    fun `OnMovieClick in normal mode emits NavigateToDetail`() = runTest {
        viewModel.events.test {
            viewModel.sendIntent(OnMovieClick(1))
            assertEquals(NavigateToDetail(1), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnMovieClick in selection mode toggles selectedIds and does not navigate`() = runTest {
        viewModel.sendIntent(OnMovieLongPress(movie1))

        viewModel.events.test {
            viewModel.sendIntent(OnMovieClick(movie2.id))
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }

        assertTrue(viewModel.state.value.selectedIds.contains(movie2.id))
    }

    @Test
    fun `OnMovieLongPress enters selection mode with that movie selected`() = runTest {
        viewModel.sendIntent(OnMovieLongPress(movie1))
        assertEquals(setOf(movie1.id), viewModel.state.value.selectedIds)
    }

    @Test
    fun `OnToggleSelection adds and removes from selectedIds`() = runTest {
        viewModel.sendIntent(OnMovieLongPress(movie1))

        viewModel.sendIntent(OnToggleSelection(movie2))
        assertTrue(viewModel.state.value.selectedIds.containsAll(setOf(movie1.id, movie2.id)))

        viewModel.sendIntent(OnToggleSelection(movie1))
        assertEquals(setOf(movie2.id), viewModel.state.value.selectedIds)
    }

    @Test
    fun `OnRemoveFavorite calls toggleFavorite with correct movie`() = runTest {
        viewModel.sendIntent(OnRemoveFavorite(movie1))
        coVerify(exactly = 1) { toggleFavorite(movie1) }
    }

    @Test
    fun `OnRemoveSelected calls toggleFavorite for each selected movie and clears selection`() = runTest {
        viewModel.sendIntent(OnMovieLongPress(movie1))
        viewModel.sendIntent(OnToggleSelection(movie2))

        viewModel.sendIntent(OnRemoveSelected)

        coVerify(exactly = 1) { toggleFavorite(movie1) }
        coVerify(exactly = 1) { toggleFavorite(movie2) }
        assertTrue(viewModel.state.value.selectedIds.isEmpty())
    }

    @Test
    fun `OnExitSelectionMode clears selectedIds`() = runTest {
        viewModel.sendIntent(OnMovieLongPress(movie1))
        viewModel.sendIntent(OnExitSelectionMode)
        assertTrue(viewModel.state.value.selectedIds.isEmpty())
    }

    @Test
    fun `empty favorites list does not crash`() = runTest {
        every { getFavorites(NoParams) } returns flowOf(emptyList())
        val vm = FavoritesViewModel(getFavorites, toggleFavorite)
        assertTrue(vm.state.value.movies.isEmpty())
        assertFalse(vm.state.value.isLoading)
    }
}
