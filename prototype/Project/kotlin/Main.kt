import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import view.App

fun main() = application {
    val windowState = rememberWindowState(
        width = 1000.dp,
        height = 600.dp,
        position = WindowPosition.Aligned(Alignment.Center)
    )
    Window(state = windowState, onCloseRequest = ::exitApplication) {
        App()
    }
}
