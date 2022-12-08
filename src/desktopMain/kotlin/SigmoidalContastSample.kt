import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import sample.SigmoidalContrast

fun main() = application {
    val windowState = rememberWindowState()


    Window(
        onCloseRequest = ::exitApplication,
        resizable = true,
        state = windowState,
    ) {
        val originalImage = useResource("original_image.jpg") { loadImageBitmap(it) }
        val density = LocalDensity.current
        val (width, height) = with(density) {
            originalImage.width.toDp() to originalImage.height.toDp()
        }

        val shaderBuilder = SigmoidalContrast.shaderBuilder
        shaderBuilder.uniform(
            "palette0",
            1f, 0f,0f
        )
        shaderBuilder.uniform(
            "palette1",
            1f, 1f,0f
        )
        shaderBuilder.uniform(
            "palette2",
            0f, 1f,0f
        )
        shaderBuilder.uniform(
            "palette3",
            0f, 1f,1f
        )
        shaderBuilder.uniform(
            "palette4",
            0f, 0f,1f
        )
        shaderBuilder.uniform(
            "palette5",
            1f, 0f,1f
        )
        shaderBuilder.uniform(
            "palette6",
            1f, 1f,1f
        )
        shaderBuilder.uniform(
            "palette7",
            0f, 0f,0f
        )
        val imageShader = originalImage.asSkiaBitmap().makeShader()
        shaderBuilder.child("iImage", imageShader)

        Canvas(
            Modifier.width(width).height(height).border(2.dp, Color.Blue)
        ) {
            drawIntoCanvas { canvas ->
                Paint().use { paint ->
                    paint.imageFilter = ImageFilter.makeRuntimeShader(
                        runtimeShaderBuilder = shaderBuilder,
                        shaderName = "content",
                        input = null
                    )

                    canvas.nativeCanvas.apply {
                        drawRect(
                            Rect(0f, 0f, 0f, 0f,),
                            paint
                        )
                    }
                }
            }
        }
    }
}
