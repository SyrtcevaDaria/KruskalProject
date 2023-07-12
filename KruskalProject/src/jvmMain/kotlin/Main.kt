import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import view.App
import view.GraphViewState

fun main() = application {
    val windowState = rememberWindowState(
        width = 1500.dp,
        height = 900.dp,
        position = WindowPosition.Aligned(Alignment.Center)
    )
    val state = remember { GraphViewState() }
    Window(state = windowState, onCloseRequest = ::exitApplication) {
        App(state)
    }
}
