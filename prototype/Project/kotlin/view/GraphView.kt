@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class,
)

package view

import Edge
import Graph
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Move
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import utils.*

@Composable
fun GraphView(state: GraphViewState) {
    var mousePosition by remember { mutableStateOf(Offset(0f, 0f)) }
    BoxWithConstraints(Modifier
        .fillMaxSize()
        .onPointerEvent(Move) { mousePosition = it.changes[0].position }
        .onClick(onClick = state::cancelSelection)
    ) {
        state.edges.forEach { EdgeView(state, it) }
        SelectLine(state, mousePosition)
        state.coords.forEachIndexed { index, offset -> VertexView(state, index, offset) }
    }
    WeightDialog(state)
}

@Composable
private fun BoxWithConstraintsScope.EdgeView(state: GraphViewState, edge: Edge) {
    val strokeSize = minDimension * strokeWidth

    val (src, dest, weight) = edge
    val vertex1 = state.vertex(src).toAbsolute() - Offset(0f, strokeSize.value / 2)
    val vertex2 = state.vertex(dest).toAbsolute() - Offset(0f, strokeSize.value / 2)

    if (weight > 0) {
        val isColored = state.coloredEdges.any { (s, d) -> (s == src && d == dest) || (d == src && s == dest) }
        val color = if (isColored) Color.Red else colors.primary
        EdgeLine(vertex1, vertex2, color, Modifier.onClick { state.removeEdge(src, dest) })
        val textOffset = lerp(vertex2, vertex1, 0.3f).toInt()
        Text(weight.toString(), Modifier.offset { textOffset }, color = colors.onSurface)
    }
}

@Composable
private fun BoxWithConstraintsScope.EdgeLine(
    vertex1: Offset,
    vertex2: Offset,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val strokeSize = minDimension * strokeWidth
    val angle = vertex1.angle(vertex2).toDegrees()
    val scaleX = vertex1.distance(vertex2)
    Box(
        modifier = Modifier
            .offset { vertex1.toInt() }
            .graphicsLayer(rotationZ = angle, scaleX = scaleX, transformOrigin = TransformOriginStart)
            .background(color)
            .height(strokeSize)
            .width(1.dp)
            .then(modifier)
    )
}

@Composable
private fun BoxWithConstraintsScope.VertexView(
    state: GraphViewState,
    index: Int,
    offset: Offset,
) {
    val color = colors.secondaryVariant
    val pressedColor = remember { Color(0, 0, 0, 0x40) }
    Box(
        modifier = Modifier
            .size(vertexSize)
            .offset(offset.x * maxWidth - vertexSizeHalf, offset.y * maxHeight - vertexSizeHalf)
            .drawBehind {
                drawCircle(color)
                if (index == state.selectedVertex) drawCircle(pressedColor)
            }
            .onClick { state.vertexClicked(index) }
            .onDrag { state.drag(index, Offset(it.x / maxWidth.value, it.y / maxHeight.value)) }
    ) {
        Text(
            text = index.toString(),
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            textAlign = TextAlign.Center,
            color = colors.onSecondary,
            style = MaterialTheme.typography.button,
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.SelectLine(state: GraphViewState, mousePosition: Offset) {
    val selected = state.selectedVertex
    if (selected != -1) {
        val vertex = state.vertex(selected).toAbsolute()
        EdgeLine(vertex, mousePosition, colors.primary)
    }
}


@Composable
private fun WeightDialog(parentState: GraphViewState) {
    val state = parentState.openDialog ?: return
    AlertDialog(
        onDismissRequest = { parentState.closeDialog() },
        buttons = {
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) { focusRequester.requestFocus() }
            var text by remember { mutableStateOf("") }
            val isError = text.isNotBlank() && text.toIntOrNull().let { it == null || it < 0 }
            Column(Modifier.padding(24.dp)) {
                TextField(
                    value = text,
                    isError = isError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester).onEnter {
                        if (!isError) {
                            parentState.connect(state.src, state.dest, text.toInt())
                            parentState.closeDialog()
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onValueChange = { text = it },
                )
                if (isError) {
                    Text(
                        text = "Введите число",
                        color = colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                    )
                }
                Button(modifier = Modifier.padding(top = 8.dp), enabled = text.isNotBlank() && !isError, onClick = {
                    parentState.connect(state.src, state.dest, text.toInt())
                    parentState.closeDialog()
                }) { Text("Подтвердить") }
            }
        },
    )
}