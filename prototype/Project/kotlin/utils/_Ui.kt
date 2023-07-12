package utils

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.IntOffset
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

fun Offset.toInt() = IntOffset(x.toInt(), y.toInt())

context(BoxWithConstraintsScope)
fun Offset.toAbsolute() = Offset(x * maxWidth.value, y * maxHeight.value)

fun Offset.angle(other: Offset) = atan2(other.y - y, other.x - x)

fun Float.toDegrees() = (this * 180 / PI).toFloat()

fun Offset.distance(other: Offset) = sqrt((other.x - x).pow(2) + (other.y - y).pow(2))

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.onEnter(
    onEnter: () -> Unit
): Modifier = onPreviewKeyEvent {
    if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
        onEnter()
    }
    false
}

val TransformOriginStart = TransformOrigin(0.0f, 0.5f)