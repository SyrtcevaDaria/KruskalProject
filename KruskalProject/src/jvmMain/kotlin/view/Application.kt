package view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import theme.MainTheme
import utils.clickableHidden
import utils.strings
import utils.fromFile
import java.awt.FileDialog
import java.awt.Frame
import java.nio.file.Paths
import kotlin.io.path.Path

@Composable
fun App(state: GraphViewState) {
    MainTheme {
        Surface {
            if (state.screen == Screen.Start) {
                StartScreen(state)
            }
            if (state.screen == Screen.Graph) {
                GraphScreen(state)
            }
        }
    }
}


@Composable
private fun StartScreen(state: GraphViewState) {
    var showFilePicker by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize().testTag("StartScreen"),
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
        Button(onClick = { showFilePicker = true }) { Text(strings.buttonFromFile) }
        Button(onClick = { state.openGraphScreen() }) { Text(strings.buttonManual) }
        Spacer(Modifier.weight(1f))
    }
}


@Composable
private fun GraphScreen(state: GraphViewState) {
    Column(modifier = Modifier.fillMaxSize().testTag("GraphScreen")) {
        Box(Modifier.clickableHidden { state.openStartScreen() }) {
            Image(
                modifier = Modifier.padding(16.dp).size(36.dp),
                painter = painterResource("exit-2-svgrepo-com.svg"),
                contentDescription = "back",
                colorFilter = ColorFilter.tint(colors.onSurface),
            )
        }
        Box(Modifier.padding(top = 20.dp)){
            if (state.isActive && !state.hasNextStep) {
                Text(
                    text = "Построено МОД с весом ${state.curWeight}",
                    color = colors.onSurface,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth().padding(start = 200.dp),
                    textAlign = TextAlign.Justify
                )
            }
        }
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 16.dp)) {
            Box(Modifier.weight(4f)) {GraphView(state)}
            ControlPanel(Modifier.weight(4f), state)
        }
    }
}


@Composable
fun ControlPanel(modifier: Modifier, state: GraphViewState) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.align(alignment = BiasAlignment.Horizontal(1f)).border(BorderStroke(4.dp, SolidColor(colors.onSurface)), RoundedCornerShape(16.dp))
        ) {
            Text(
                text = strings.instructions,
                modifier = Modifier.padding(24.dp),
                textAlign = TextAlign.Justify
            )
        }
        Spacer(Modifier.height(48.dp))

        Button(onClick = { state.addVertex() }) { Text(strings.buttonAddVertex) }
        if (state.hasNextStep) {
            Button(onClick = { state.next() }) { Text(strings.buttonNext) }
        }
        if (state.isActive && !state.hasNextStep) {
            Button(onClick = { state.complete() }) { Text(strings.buttonComplete) }
        }
        if (state.hasPreviousStep) {
            Button(onClick = { state.previous() }) { Text(strings.buttonPrevious) }
        }
        if (!state.isActive) {
            Button(
                onClick = {
                    state.startKruskal()
                    state.next()
                },
                enabled = state.isConnected,
            ) { Text(strings.buttonStart) }
        }
        if (state.isActive) {
            LazyColumn(Modifier.weight(2f).padding(vertical = 24.dp)) {
                itemsIndexed(state.coloredEdges) { index, edge ->
                    Text(strings.stepDescription(edge))
                    if (index == state.coloredEdges.size - 1) {
                        Text(text = strings.stepBottom)
                        Text("Общий вес = ${state.curWeight}")
                    }
                }
            }
        }
    }
}