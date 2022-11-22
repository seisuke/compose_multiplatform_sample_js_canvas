import androidx.compose.ui.window.Window
import kotlinx.browser.window
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    val hash = window.location.hash
    onWasmReady {
        if (hash.isBlank()) {
            Window("sample") {
                DomAndCanvas()
            }
        }
        when (hash) {
            "#LazyListView" -> Window("sample") {
                LazyListView()
            }
            "#ShaderCanvas" -> shaderCanvasSample()
            "#MaterialIcon" -> Window("sample") {
                MaterialIcon()
            }
            else -> throw Error("Unknown Sample ${hash}")
        }
    }

}
