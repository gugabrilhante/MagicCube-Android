package gustavo.brilhante.magiccube2.integration

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.domain.usecase.ObserveSettingsUseCase
import gustavo.brilhante.magiccube2.domain.usecase.SaveSettingsUseCase
import gustavo.brilhante.magiccube2.presentation.options.OptionsUiState
import gustavo.brilhante.magiccube2.presentation.options.OptionsViewModel
import gustavo.brilhante.magiccube2.testutil.FakeSettingsRepository
import gustavo.brilhante.magiccube2.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Integration test — exercises the full vertical slice:
 * [FakeSettingsRepository] → [SaveSettingsUseCase] / [ObserveSettingsUseCase] → [OptionsViewModel]
 *
 * No mocking framework needed: we use [FakeSettingsRepository] as a real in-memory
 * implementation that satisfies the domain contract.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsFlowIntegrationTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeSettingsRepository
    private lateinit var viewModel: OptionsViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeSettingsRepository()
        val observe = ObserveSettingsUseCase(fakeRepository)
        val save = SaveSettingsUseCase(fakeRepository)
        viewModel = OptionsViewModel(saveSettings = save, observeSettings = observe)
    }

    @Test
    fun `changes made through ViewModel are persisted to the repository`() = runTest {
        viewModel.increaseShuffle()
        advanceUntilIdle()

        val saved = fakeRepository.lastSaved
        assertEquals(CubeSettings(shuffle = 4), saved)
    }

    @Test
    fun `repository emission propagates to ViewModel uiState`() = runTest {
        val injected = CubeSettings(shuffle = 9, speed = 2, size = 4)
        fakeRepository.emit(injected)
        advanceUntilIdle()

        assertEquals(OptionsUiState(shuffle = 9, speed = 2, size = 4), viewModel.uiState.value)
    }

    @Test
    fun `reset restores both ViewModel state and persisted data to defaults`() = runTest {
        viewModel.increaseShuffle()
        viewModel.increaseSpeed()
        viewModel.increaseSize()
        advanceUntilIdle()

        viewModel.resetSettings()
        advanceUntilIdle()

        assertEquals(OptionsUiState(), viewModel.uiState.value)
        assertEquals(CubeSettings(), fakeRepository.lastSaved)
    }

    @Test
    fun `sequential modifications accumulate correctly`() = runTest {
        repeat(3) { viewModel.increaseShuffle() }
        repeat(2) { viewModel.increaseSpeed() }
        advanceUntilIdle()

        val expected = OptionsUiState(shuffle = 6, speed = 7, size = 9)
        assertEquals(expected, viewModel.uiState.value)
    }
}
