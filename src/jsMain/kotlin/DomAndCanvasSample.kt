import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposable

@Composable
fun DomAndCanvas() {
    var text by remember { mutableStateOf("text") }
    TextField(
        value = text,
        onValueChange = { text = it }
    )
    LaunchedEffect(Unit) {
        renderComposable(rootElementId = "root") {
            Div { org.jetbrains.compose.web.dom.Text(text) }
        }
    }
}
