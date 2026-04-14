package gustavo.brilhante.magiccube2.presentation

import gustavo.brilhante.magiccube2.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainMenuViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MainMenuViewModel

    @Before
    fun setUp() {
        viewModel = MainMenuViewModel()
    }

    @Test
    fun `onStartClick emits NavigateToStart after delay`() = runTest {
        val events = mutableListOf<MainMenuUiEvent>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.onStartClick()
        advanceUntilIdle()

        assertTrue(events.contains(MainMenuUiEvent.NavigateToStart))
        job.cancel()
    }

    @Test
    fun `onOptionsClick emits NavigateToOptions after delay`() = runTest {
        val events = mutableListOf<MainMenuUiEvent>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.onOptionsClick()
        advanceUntilIdle()

        assertTrue(events.contains(MainMenuUiEvent.NavigateToOptions))
        job.cancel()
    }

    @Test
    fun `multiple clicks emit corresponding events in order`() = runTest {
        val events = mutableListOf<MainMenuUiEvent>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.onStartClick()
        viewModel.onOptionsClick()
        advanceUntilIdle()

        assertTrue(events.contains(MainMenuUiEvent.NavigateToStart))
        assertTrue(events.contains(MainMenuUiEvent.NavigateToOptions))
        job.cancel()
    }

    @Test
    fun `uiEvent starts with no events`() = runTest {
        val events = mutableListOf<MainMenuUiEvent>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        advanceUntilIdle()

        assertTrue(events.isEmpty())
        job.cancel()
    }
}
