package view

import Edge
import Graph
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.min
import kruskal
import kotlin.math.cos
import kotlin.math.sin

const val vertexRadius = 0.1f
const val strokeWidth = 0.01f
val BoxWithConstraintsScope.minDimension get() = min(maxWidth, maxHeight)
val BoxWithConstraintsScope.vertexSize get() = minDimension * vertexRadius

val BoxWithConstraintsScope.vertexSizeHalf get() = vertexSize / 2

class GraphViewState(private val graph: Graph = Graph()) {

    private val angle: Double get() = 2 * Math.PI / graph.size

    var coords by mutableStateOf(listOf<Offset>())

    var coloredEdges by mutableStateOf(listOf<Edge>())

    var isConnected by mutableStateOf(false)

    var selectedVertex by mutableStateOf(-1)

    var edges by mutableStateOf(listOf<Edge>())

    var openDialog by mutableStateOf<DialogState?>(null)

    var screen by mutableStateOf(Screen.Start)

    private var steps: Iterator<Edge>? = null
        set(value) {
            field = value
            coloredEdges = listOf()
            onStepsUpdated()
        }

    var isRunning by mutableStateOf(false)

    var isStarted by mutableStateOf(false)

    init {
        onGraphUpdated()
    }

    fun replaceGraph(graph: Graph) {
        complete()
        this.graph.replace(graph)
        coords = listOf()
        onGraphUpdated()
    }

    fun startKruskal() {
        steps = graph.kruskal().onEach { edge ->
            coloredEdges += edge
            onStepsUpdated()
        }.iterator()
    }

    fun next() {
        val s = steps
        if (s?.hasNext() == true) {
            s.next()
        }
    }

    fun complete() {
        steps = null
    }

    private fun onStepsUpdated() {
        val steps = steps
        isRunning = steps.let { it != null && it.hasNext() }
        isStarted = steps != null
    }

    private fun resetCoords(radius: Float = 0.5f) {
        coords = (0 until graph.size).map { index ->
            val angleReal = index * angle
            Offset(
                (radius * cos(angleReal) + radius).toFloat()
                    .coerceIn(2 * vertexRadius, 2 * (radius - vertexRadius)),
                (radius * sin(angleReal) + radius).toFloat()
                    .coerceIn(2 * vertexRadius, 2 * (radius - vertexRadius)),
            )
        }
    }

    fun addVertex() {
        graph.addVertex()
        if (coords.isEmpty()) {
            resetCoords()
        } else {
            coords += Offset(
                (coords.sumOf { it.x.toDouble() } / coords.size).toFloat(),
                (coords.sumOf { it.y.toDouble() } / coords.size).toFloat(),
            )
        }
        onGraphUpdated()
    }

    fun vertex(index: Int) = coords[index]

    fun connect(src: Int, dest: Int, value: Int) {
        graph[src, dest] = value
        onGraphUpdated()
    }

    fun removeEdge(i: Int, j: Int) {
        graph[i, j] = 0 // delete edge
        onGraphUpdated()
    }

    private fun onGraphUpdated() {
        isConnected = graph.isConnected()
        complete()
        if (coords.isEmpty()) {
            resetCoords()
        }
        edges = graph.edges().toList()
        println(graph)
        onStepsUpdated()
    }

    private fun removeVertex(index: Int) {
        graph.removeVertex(index)
        coords = coords.filterIndexed { i, _ -> index != i }
        onGraphUpdated()
    }

    fun drag(selected: Int, dragOffset: Offset) {
        coords = coords.mapIndexed { index, offset ->
            if (index == selected) (offset + dragOffset).coerced()
            else offset
        }
    }

    fun vertexClicked(index: Int) {
        selectedVertex = if (selectedVertex == -1) index
        else {
            if (selectedVertex == index) {
                removeVertex(index)
            } else {
                connect(selectedVertex, index, 1)
                openDialog = DialogState(selectedVertex, index)
            }
            -1
        }
    }

    fun cancelSelection() {
        selectedVertex = -1
    }

    fun closeDialog() {
        openDialog = null
    }

    fun openGraphScreen() {
        screen = Screen.Graph
    }

}

data class DialogState(val src: Int, val dest: Int)

fun Offset.coerced() = Offset(
    x.coerceIn(0f, 1f),
    y.coerceIn(0f, 1f),
)