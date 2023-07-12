package utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.IntOffset
import utils.mutableStateOf
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
    onEnter: () -> Unit,
): Modifier = onPreviewKeyEvent {
    if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
        onEnter()
    }
    false
}

fun Modifier.clickableHidden(
    onClick: () -> Unit,
): Modifier = composed {
    Modifier.clickable(
        onClick = onClick,
        indication = null, //откл подсветка
        interactionSource = remember { MutableInteractionSource() }
    )
}

fun <T> mutableStateOf(
    value: T,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
    onChange: (T) -> Unit,
): MutableState<T> {
    val delegate = mutableStateOf(value, policy)
    return object : MutableState<T> by delegate {
        override var value: T
            get() = delegate.value
            set(value) {
                delegate.value = value
                onChange(value)
            }

    }
}

val TransformOriginStart = TransformOrigin(0.0f, 0.5f)