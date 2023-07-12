@file:OptIn(ExperimentalCoroutinesApi::class)

import androidx.compose.material.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import utils.strings
import view.App
import view.GraphViewState
import kotlin.properties.Delegates
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import utils.fromFile
import view.ControlPanel
import kotlin.io.path.Path

class ControlPanelTest {

    @get:Rule
    val compose = createComposeRule()

    lateinit var scope: TestScope
    lateinit var state: GraphViewState

    val buttonStart = hasText(strings.buttonStart) and hasClickAction()
    val buttonNext = hasText(strings.buttonNext) and hasClickAction()
    val buttonComplete = hasText(strings.buttonComplete) and hasClickAction()

    @Before
    fun before() {
        val dispatcher = StandardTestDispatcher()
        scope = TestScope(dispatcher)
        state = GraphViewState()
    }


    @Test
    fun `Should start button pressed`() = scope.runTest {
        compose.run {
            val graph = fromFile("C:\\Users\\dns\\projects\\kruskal1\\src\\jvmMain\\resources\\graph2.txt") ?: return@run
            state.replaceGraph(graph)
            setContent { ControlPanel(Modifier, state) }
            awaitIdle()
            onNode(buttonStart).assertIsDisplayed()
            onNode(buttonNext).assertDoesNotExist()
            onNode(buttonStart).performClick()
            awaitIdle()
            onNode(buttonNext).assertIsDisplayed()
            onNode(buttonStart).assertDoesNotExist()
        }
    }

    @Test
    fun `Should stop button pressed`() = scope.runTest {
        compose.run {
            val graph = fromFile("C:\\Users\\dns\\projects\\kruskal1\\src\\jvmMain\\resources\\graph2.txt") ?: return@run
            state.replaceGraph(graph)
            setContent { ControlPanel(Modifier, state) }
            awaitIdle()
            onNode(buttonStart).performClick()
            awaitIdle()
            onNode(buttonNext).performClick()
            awaitIdle()
            onNode(buttonNext).performClick()
            awaitIdle()
            onNode(buttonComplete).assertIsDisplayed()
        }
    }


}

