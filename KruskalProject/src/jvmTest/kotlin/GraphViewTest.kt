@file:OptIn(ExperimentalCoroutinesApi::class)

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import view.GraphView
import view.GraphViewState
import kotlin.test.assertNotNull

class GraphViewTest {

    @get:Rule
    val compose = createComposeRule()

    lateinit var scope: TestScope
    lateinit var state: GraphViewState

    val vertex0 = hasTestTag("VertexView 0")
    val edgeNode = hasTestTag("EdgeView ${Edge(0, 1, 2)}")

    @Before
    fun before() {
        val dispatcher = StandardTestDispatcher()
        scope = TestScope(dispatcher)
        state = GraphViewState()
    }


    @Test
    fun `Double click removes vertex`() = scope.runTest {
        compose.run {
            state.addVertex()
            setContent { GraphView(state) }
            awaitIdle()
            onNode(vertex0).assertIsDisplayed()
            onNode(vertex0).performClick()
            onNode(vertex0).performClick()
            awaitIdle()
            onNode(vertex0).assertDoesNotExist()
        }
    }

    @Test
    fun `Click removes edge`() = scope.runTest {
        compose.run {
            state.addVertex()
            state.addVertex()
            state.connect(0, 1, 2)
            setContent { GraphView(state) }
            awaitIdle()
            onNode(edgeNode).assertIsDisplayed()
            onNode(edgeNode).performClick()
            awaitIdle()
            onNode(edgeNode).assertDoesNotExist()
        }
    }


}

