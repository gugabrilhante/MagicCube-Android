package gustavo.brilhante.magiccube2.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import gustavo.brilhante.magiccube2.activity.ui.theme.MagicCubeAndroidTheme
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
            MagicCubeAndroidTheme {
                OptionsScreen(viewModel = viewModel)
            }
        }
        composeRule.onNodeWithText("Shuffle", ignoreCase = true)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun optionsScreen_showsSpeedLabel() {
        composeRule.setContent {
            MagicCubeAndroidTheme {
                OptionsScreen(viewModel = viewModel)
            }
        }
        composeRule.onNodeWithText("Speed", ignoreCase = true)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun optionsScreen_showsSizeLabel() {
        composeRule.setContent {
            MagicCubeAndroidTheme {
                OptionsScreen(viewModel = viewModel)
            }
        }
        composeRule.onNodeWithText("Size", ignoreCase = true)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun optionsScreen_showsResetButton() {
        composeRule.setContent {
            MagicCubeAndroidTheme {
                OptionsScreen(viewModel = viewModel)
            }
        }
        
        // Wait for the screen to settle
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("reset_button")
            .performScrollTo()
            .assertIsDisplayed()

        composeRule.onNodeWithText("Reset to default", ignoreCase = true)
            .assertIsDisplayed()
    }
}
