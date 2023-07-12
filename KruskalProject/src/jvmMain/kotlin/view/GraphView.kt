@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class,
)

package view

import Edge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import utils.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GraphView(state: GraphViewState) {
    var mousePosition by remember { mutableStateOf(Offset(0f, 0f)) }
    BoxWithConstraints(Modifier
        .fillMaxSize()
        .onPointerEvent(Move) { mousePosition = it.changes[0].position }
        .clickableHidden{state.cancelSelection()}
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
        val color = if (isColored) Color.Red else Color(128,49,167)
        EdgeLine(vertex1, vertex2, color, Modifier
            .clickableHidden { state.removeEdge(src, dest) }
            .testTag("EdgeView $edge")
        )
        val textOffset = lerp(vertex2, vertex1, 0.4f).toInt()
        Text(strings.edgeWeight(weight), Modifier.offset { textOffset }, fontSize = 25.sp, color = colors.onSurface, fontWeight = FontWeight.Bold)
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
        modifier = modifier
            .offset { vertex1.toInt() }
            .graphicsLayer(rotationZ = angle, scaleX = scaleX, transformOrigin = TransformOriginStart)
            .background(color)
            .height(strokeSize)
            .width(1.dp)
            .then(modifier)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoxWithConstraintsScope.VertexView(
    state: GraphViewState,
    index: Int,
    offset: Offset,
) {
    val color = Color(198,161,207)
    val pressedColor = remember { Color(0, 0, 0, 0x40) }
    Box(
        modifier = Modifier
            .size(vertexSize)
            .testTag("VertexView $index")
            .offset(offset.x * maxWidth - vertexSizeHalf, offset.y * maxHeight - vertexSizeHalf)
            .drawBehind {
                drawCircle(color)
                if (index == state.selectedVertex) drawCircle(pressedColor)
            }
            .clickableHidden { state.vertexClicked(index) }
            .onDrag { state.drag(index, Offset(it.x / maxWidth.value, it.y / maxHeight.value)) }
    ) {
        Text(
            text = strings.vertexNumber(index),
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
        EdgeLine(vertex, mousePosition, Color(128,49,167))
    }
}


@OptIn(ExperimentalMaterialApi::class)
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
                    onValueChange = { text = it },
                )
                if (isError) {
                    Text(
                        text = strings.enterNumber,
                        color = colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                    )
                }
                Button(modifier = Modifier.padding(top = 8.dp), enabled = text.isNotBlank() && !isError, onClick = {
                    parentState.connect(state.src, state.dest, text.toInt())
                    parentState.closeDialog()
                }) { Text(strings.buttonAccept) }
            }
        },
    )
}
