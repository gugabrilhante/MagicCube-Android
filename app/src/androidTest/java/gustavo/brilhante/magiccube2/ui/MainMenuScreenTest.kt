package gustavo.brilhante.magiccube2.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import gustavo.brilhante.magiccube2.compose.MainMenuScreen
import gustavo.brilhante.magiccube2.presentation.MainMenuViewModel
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for [MainMenuScreen].
 *
 * We supply a real [MainMenuViewModel] and verify the composable renders
 * the expected buttons and fires the correct callbacks on interaction.
 */
class MainMenuScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun mainMenuScreen_showsStartButton() {
        composeRule.setContent {
            MainMenuScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = MainMenuViewModel(),
                onStartClick = {},
                onOptionsClick = {},
                onQuitClick = {},
            )
        }

        composeRule.onNodeWithTag("start_button").assertIsDisplayed()
    }

    @Test
    fun mainMenuScreen_showsOptionsButton() {
        composeRule.setContent {
            MainMenuScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = MainMenuViewModel(),
                onStartClick = {},
                onOptionsClick = {},
                onQuitClick = {},
            )
        }

        composeRule.onNodeWithTag("options_button").assertIsDisplayed()
    }

    @Test
    fun mainMenuScreen_showsQuitButton() {
        composeRule.setContent {
            MainMenuScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = MainMenuViewModel(),
                onStartClick = {},
                onOptionsClick = {},
                onQuitClick = {},
            )
        }

        composeRule.onNodeWithTag("quit_button").assertIsDisplayed()
    }

    @Test
    fun mainMenuScreen_quitButton_firesCallback() {
        var quitCalled = false

        composeRule.setContent {
            MainMenuScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = MainMenuViewModel(),
                onStartClick = {},
                onOptionsClick = {},
                onQuitClick = { quitCalled = true },
            )
        }

        composeRule.onNodeWithTag("quit_button").performClick()

        assertTrue(quitCalled)
    }
}
