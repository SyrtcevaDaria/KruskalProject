@file:OptIn(ExperimentalCoroutinesApi::class)

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import utils.strings
import view.App
import view.GraphViewState
import kotlin.properties.Delegates

class AppViewTest {

    @get:Rule
    val compose = createComposeRule()

    lateinit var scope: TestScope
    lateinit var state: GraphViewState

    val startScreen = hasTestTag("StartScreen")
    val graphScreen = hasTestTag("GraphScreen")
    val buttonManual = hasText(strings.buttonManual) and hasClickAction()

    @Before
    fun before() {
        val dispatcher = StandardTestDispatcher()
        scope = TestScope(dispatcher)
        state = GraphViewState()
    }

    @Test
    fun `Should display start screen on init`() = scope.runTest {
        compose.run {
            setContent { App(state) }
            awaitIdle()
            onNode(startScreen).assertIsDisplayed()
            onNode(graphScreen).assertDoesNotExist()
        }
    }

    @Test
    fun `Should display graph when manual button pressed`() = scope.runTest {
        compose.run {
            setContent { App(state) }
            onNode(buttonManual).performClick()
            awaitIdle()
            onNode(startScreen).assertDoesNotExist()
            onNode(graphScreen).assertIsDisplayed()
        }
    }


}

