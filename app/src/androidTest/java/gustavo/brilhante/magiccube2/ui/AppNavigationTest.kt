package gustavo.brilhante.magiccube2.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.performScrollTo
import gustavo.brilhante.magiccube2.activity.MainMenuActivity
import org.junit.Rule
import org.junit.Test

class AppNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainMenuActivity>()

    @Test
    fun givenMainMenuWhenOptionsClickedThenOptionsScreenIsDisplayed() {
        // Given - Main Menu is displayed
        composeRule.onNodeWithTag("options_button").assertIsDisplayed()

        // When
        composeRule.onNodeWithTag("options_button").performClick()
        waitForOptionsScreen()

        // Then
        composeRule.onNodeWithTag("slider_Shuffle").assertIsDisplayed()
    }

    @Test
    fun givenOptionsScreenWhenBackClickedThenMainMenuIsDisplayed() {
        // Given - In Options Screen
        composeRule.onNodeWithTag("options_button").performClick()
        waitForOptionsScreen()
        composeRule.onNodeWithTag("slider_Shuffle").assertIsDisplayed()

        // When - Click back
        composeRule.onNodeWithContentDescription("Back", ignoreCase = true).performClick()
        composeRule.waitForIdle()

        // Then
        composeRule.onNodeWithTag("start_button").assertIsDisplayed()
    }

    @Test
    fun givenOptionsScreenWhenShuffleChangedThenUIUpdates() {
        // Given
        composeRule.onNodeWithTag("options_button").performClick()
        waitForOptionsScreen()

        // When
        composeRule.onNodeWithTag("slider_Shuffle").performTouchInput {
            swipeRight()
        }

        // Then
        composeRule.onNodeWithTag("reset_button")
            .performScrollTo()
            .assertIsDisplayed()
    }

    // onOptionsClick() has a 600 ms delay before emitting the navigation event, which the
    // Compose IdlingResource does not track. We poll until the Options screen is in the tree,
    // then wait for any remaining animations to settle.
    private fun waitForOptionsScreen() {
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithTag("slider_Shuffle")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.waitForIdle()
    }
}
