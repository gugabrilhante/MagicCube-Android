package gustavo.brilhante.magiccube2.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import gustavo.brilhante.magiccube2.compose.OptionsScreen
import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.domain.repository.SettingsRepository
import gustavo.brilhante.magiccube2.domain.usecase.ObserveSettingsUseCase
import gustavo.brilhante.magiccube2.domain.usecase.SaveSettingsUseCase
import gustavo.brilhante.magiccube2.presentation.options.OptionsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for [OptionsScreen].
 *
 * A lightweight inline [SettingsRepository] fake avoids a Koin or DataStore dependency,
 * keeping the test hermetic and fast.
 */
class OptionsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val fakeRepository = object : SettingsRepository {
        private val _flow = MutableStateFlow(CubeSettings())
        override val settingsFlow: StateFlow<CubeSettings> = _flow.asStateFlow()
        override suspend fun getCurrent() = _flow.value
        override suspend fun save(settings: CubeSettings) { _flow.value = settings }
    }

    private val viewModel = OptionsViewModel(
        saveSettings = SaveSettingsUseCase(fakeRepository),
        observeSettings = ObserveSettingsUseCase(fakeRepository),
    )

    @Test
    fun optionsScreen_showsShuffleLabel() {
        composeRule.setContent {
            OptionsScreen(viewModel = viewModel)
        }
        composeRule.onNodeWithText("Shuffle", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun optionsScreen_showsSpeedLabel() {
        composeRule.setContent {
            OptionsScreen(viewModel = viewModel)
        }
        composeRule.onNodeWithText("Speed", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun optionsScreen_showsSizeLabel() {
        composeRule.setContent {
            OptionsScreen(viewModel = viewModel)
        }
        composeRule.onNodeWithText("Size", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun optionsScreen_showsResetButton() {
        composeRule.setContent {
            OptionsScreen(viewModel = viewModel)
        }
        composeRule.onNodeWithText("Reset", ignoreCase = true).assertIsDisplayed()
    }
}
