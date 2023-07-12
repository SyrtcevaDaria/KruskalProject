package view

import Graph
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import theme.MainTheme
import utils.fromFile
import java.awt.FileDialog
import java.awt.Frame
import kotlin.io.path.Path

@Composable
fun App() {
    val graph = remember { Graph() }
    val state = remember { GraphViewState(graph) }


    MainTheme {
        Surface {
            if (state.screen == Screen.Start) {
                StartScreen(Modifier.fillMaxSize(), state)
            }
            if (state.screen == Screen.Graph) {
                GraphScreen(state)
            }
        }
    }
}


@Composable
private fun StartScreen(modifier: Modifier, state: GraphViewState) {
    var showFilePicker by remember { mutableStateOf(false) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))

        if (showFilePicker) {
            LaunchedEffect(Unit) {
                showFilePicker = false
                FileDialog(null as Frame?, "Select File to Open").apply {
                    mode = FileDialog.LOAD
                    isVisible = true
                    directory ?: file ?: return@apply
                    val graph = fromFile(Paths.get(directory, file).toString()) ?: return@apply
                    state.replaceGraph(graph)
                    state.openGraphScreen()
                }
            }
        }
        Button(onClick = { showFilePicker = true }) { Text("Загрузить из файла") }
        Button(onClick = { state.openGraphScreen() }) { Text("Создать вручную") }
        Spacer(Modifier.weight(1f))
    }
}


@Composable
private fun GraphScreen(state: GraphViewState) {
    Row(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.weight(4f)) { GraphView(state) }
        ControlPanel(Modifier.weight(4f), state)
    }
}


@Composable
private fun ControlPanel(modifier: Modifier, state: GraphViewState) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.fillMaxWidth().border(BorderStroke(2.dp, SolidColor(colors.onSurface)), RoundedCornerShape(16.dp))
        ) {
            Text(
                text = "2 нажатия на вершину = удаление\n" +
                        "1 нажатие на ребро = удаление\n" +
                        "При нажатии на вершину вы можете построить ребро",
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.End
            )
        }
        Spacer(Modifier.weight(1f))

        Button(onClick = { state.addVertex() }) { Text("Добавить вершину") }
        if (state.isRunning) {
            Button(onClick = { state.next() }) { Text("Дальше") }
        } else if (state.isStarted) {
            Button(onClick = { state.complete() }) { Text("Закончить") }
        } else {
            Button(
                onClick = {
                    state.startKruskal()
                    state.next()
                },
                enabled = state.isConnected,
            ) { Text("Начать") }
        }
        if (state.isStarted) {
            LazyColumn(Modifier.weight(2f).padding(vertical = 24.dp)) {
                itemsIndexed(state.coloredEdges) { index, edge ->
                    Text(text = "Ребро ${edge.src} - ${edge.dest} с минимальным весом ${edge.weight}")
                    if (index == state.coloredEdges.size - 1) {
                        Text(text = "Поэтому добавляем в каркас")
                    }
                }
            }
        } else {
            Spacer(Modifier.weight(2f))
        }
    }
}