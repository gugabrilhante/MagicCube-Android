package gustavo.brilhante.magiccube2.presentation

import gustavo.brilhante.magiccube2.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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

        viewModel.onStartClick()   // 800ms delay
        viewModel.onOptionsClick() // 600ms delay
        
        // Advance time enough for onOptionsClick (600ms) but not yet for onStartClick (800ms)
        advanceTimeBy(700)
        assertEquals(listOf(MainMenuUiEvent.NavigateToOptions), events)

        // Advance time for the remaining onStartClick
        advanceUntilIdle()

        val expectedEvents = listOf(
            MainMenuUiEvent.NavigateToOptions,
            MainMenuUiEvent.NavigateToStart
        )
        assertEquals(expectedEvents, events)
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
