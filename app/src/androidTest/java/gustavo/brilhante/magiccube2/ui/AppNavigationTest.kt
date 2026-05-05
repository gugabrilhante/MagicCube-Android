package gustavo.brilhante.magiccube2.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import gustavo.brilhante.magiccube2.activity.MainMenuActivity
import org.junit.Rule
import org.junit.Test

class AppNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainMenuActivity>()

    @Test
    fun givenMainMenuWhenOptionsClickedThenOptionsScreenIsDisplayed() {
        // Given - Main Menu is displayed
        composeRule.onNodeWithText("Options", ignoreCase = true).assertIsDisplayed()

        // When
        composeRule.onNodeWithText("Options", ignoreCase = true).performClick()
        waitForOptionsScreen()

        // Then
        composeRule.onNodeWithText("Shuffle", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun givenOptionsScreenWhenBackClickedThenMainMenuIsDisplayed() {
        // Given - In Options Screen
        composeRule.onNodeWithText("Options", ignoreCase = true).performClick()
        waitForOptionsScreen()
        composeRule.onNodeWithText("Shuffle", ignoreCase = true).assertIsDisplayed()

        // When - Click back
        composeRule.onNodeWithContentDescription("Back", ignoreCase = true).performClick()
        composeRule.waitForIdle()

        // Then
        composeRule.onNodeWithText("Start", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun givenOptionsScreenWhenShuffleChangedThenUIUpdates() {
        // Given
        composeRule.onNodeWithText("Options", ignoreCase = true).performClick()
        waitForOptionsScreen()

        // When
        composeRule.onNodeWithTag("slider_Shuffle").performTouchInput {
            swipeRight()
        }

        // Then
        composeRule.onNodeWithText("Reset to default", ignoreCase = true).assertIsDisplayed()
    }

    // onOptionsClick() has a 600 ms delay before emitting the navigation event, which the
    // Compose IdlingResource does not track. We poll until the Options screen is in the tree,
    // then wait for any remaining animations to settle.
    private fun waitForOptionsScreen() {
        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText("Shuffle", ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.waitForIdle()
    }
}
