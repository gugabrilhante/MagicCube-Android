package gustavo.brilhante.magiccube2.presentation

import androidx.lifecycle.viewModelScope
import gustavo.brilhante.magiccube2.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// MainMenuViewModel.init runs `while (true) { delay(1500); shuffle() }` on viewModelScope.
// runTest shares the same TestCoroutineScheduler as Dispatchers.Main, so its internal
// advanceUntilIdle() at the end of each test drives the loop forever, hanging the suite.
// Fix: cancel viewModelScope before runTest exits so the loop coroutine is removed from
// the scheduler and advanceUntilIdle() finds no pending work.
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
        advanceTimeBy(900) // covers 800 ms delay

        assertTrue(events.contains(MainMenuUiEvent.NavigateToStart))
        job.cancel()
        viewModel.viewModelScope.cancel() // stop the infinite shuffle loop before runTest drains
    }

    @Test
    fun `onOptionsClick emits NavigateToOptions after delay`() = runTest {
        val events = mutableListOf<MainMenuUiEvent>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.onOptionsClick()
        advanceTimeBy(700) // covers 600 ms delay

        assertTrue(events.contains(MainMenuUiEvent.NavigateToOptions))
        job.cancel()
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun `multiple clicks emit corresponding events in order`() = runTest {
        val events = mutableListOf<MainMenuUiEvent>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.onStartClick()   // fires after 800 ms
        viewModel.onOptionsClick() // fires after 600 ms

        advanceTimeBy(700)
        assertEquals(listOf(MainMenuUiEvent.NavigateToOptions), events)

        advanceTimeBy(200) // total 900 ms — Start event fires
        assertEquals(
            listOf(MainMenuUiEvent.NavigateToOptions, MainMenuUiEvent.NavigateToStart),
            events
        )
        job.cancel()
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun `uiEvent starts with no events`() = runTest {
        val events = mutableListOf<MainMenuUiEvent>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        assertTrue(events.isEmpty())
        job.cancel()
        viewModel.viewModelScope.cancel()
    }
}
