package gustavo.brilhante.magiccube2.presentation.options

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.domain.usecase.LoadSettingsUseCase
import gustavo.brilhante.magiccube2.domain.usecase.ObserveSettingsUseCase
import gustavo.brilhante.magiccube2.domain.usecase.SaveSettingsUseCase
import gustavo.brilhante.magiccube2.testutil.FakeSettingsRepository
import gustavo.brilhante.magiccube2.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OptionsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeSettingsRepository
    private lateinit var viewModel: OptionsViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeSettingsRepository()
        viewModel = OptionsViewModel(
            loadSettings = LoadSettingsUseCase(fakeRepository),
            saveSettings = SaveSettingsUseCase(fakeRepository),
            observeSettings = ObserveSettingsUseCase(fakeRepository),
        )
    }

    // --- Init ---

    @Test
    fun `load is called on init`() = runTest {
        advanceUntilIdle()
        assertTrue(fakeRepository.loadCalled)
    }

    @Test
    fun `initial uiState matches defaults`() {
        assertEquals(OptionsUiState(), viewModel.uiState.value)
    }

    @Test
    fun `uiState reflects persisted settings after repository emits`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        fakeRepository.emit(CubeSettings(shuffle = 8, speed = 2, size = 7))
        advanceUntilIdle()

        assertEquals(OptionsUiState(shuffle = 8, speed = 2, size = 7), viewModel.uiState.value)
    }

    // --- Shuffle ---

    @Test
    fun `increaseShuffle increments shuffle by 1`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.increaseShuffle()
        advanceUntilIdle()

        assertEquals(4, viewModel.uiState.value.shuffle) // default 3 + 1
    }

    @Test
    fun `decreaseShuffle decrements shuffle by 1`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.decreaseShuffle()
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.shuffle) // default 3 - 1
    }

    @Test
    fun `shuffle is clamped to max 10`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        repeat(20) { viewModel.increaseShuffle() }
        advanceUntilIdle()

        assertEquals(10, viewModel.uiState.value.shuffle)
    }

    @Test
    fun `shuffle is clamped to min 1`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        repeat(20) { viewModel.decreaseShuffle() }
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.shuffle)
    }

    // --- Speed ---

    @Test
    fun `increaseSpeed increments speed by 1`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.increaseSpeed()
        advanceUntilIdle()

        assertEquals(6, viewModel.uiState.value.speed) // default 5 + 1
    }

    @Test
    fun `decreaseSpeed decrements speed by 1`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.decreaseSpeed()
        advanceUntilIdle()

        assertEquals(4, viewModel.uiState.value.speed) // default 5 - 1
    }

    @Test
    fun `speed is clamped to max 10`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        repeat(20) { viewModel.increaseSpeed() }
        advanceUntilIdle()

        assertEquals(10, viewModel.uiState.value.speed)
    }

    @Test
    fun `speed is clamped to min 1`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        repeat(20) { viewModel.decreaseSpeed() }
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.speed)
    }

    // --- Size ---

    @Test
    fun `increaseSize increments size by 1`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.increaseSize()
        advanceUntilIdle()

        assertEquals(10, viewModel.uiState.value.size) // default 9 + 1
    }

    @Test
    fun `decreaseSize decrements size by 1`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.decreaseSize()
        advanceUntilIdle()

        assertEquals(8, viewModel.uiState.value.size) // default 9 - 1
    }

    @Test
    fun `size is clamped to max 10`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        repeat(20) { viewModel.increaseSize() }
        advanceUntilIdle()

        assertEquals(10, viewModel.uiState.value.size)
    }

    @Test
    fun `size is clamped to min 1`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        repeat(20) { viewModel.decreaseSize() }
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.size)
    }

    // --- Persistence ---

    @Test
    fun `updateState persists new settings to repository`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.increaseShuffle()
        advanceUntilIdle()

        assertEquals(CubeSettings(shuffle = 4), fakeRepository.lastSaved)
    }

    @Test
    fun `multiple updates apply sequentially`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.increaseShuffle()
        viewModel.increaseShuffle()
        viewModel.increaseSpeed()
        advanceUntilIdle()

        assertEquals(OptionsUiState(shuffle = 5, speed = 6, size = 9), viewModel.uiState.value)
    }
}
