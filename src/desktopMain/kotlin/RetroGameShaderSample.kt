import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import org.jetbrains.skia.RuntimeShaderBuilder
import sample.CanvasBox
import sample.RetroGameShader

fun main() = application {
    val windowState = rememberWindowState()
    val originalImage = useResource("original_image.jpg") {
        loadImageBitmap(it)
    }
    val (width, height) = with(LocalDensity.current) {
        originalImage.width.toDp() to originalImage.height.toDp()
    }
    val shaderBuilder = RetroGameShader.shaderBuilder
    val imageShader = originalImage.asSkiaBitmap().makeShader()
    shaderBuilder.child("iImage", imageShader)
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

    Window(
        onCloseRequest = ::exitApplication,
        resizable = true,
        state = windowState,
    ) {
        Column {
            CanvasBox(width, height) {
                ShaderCanvas(
                    shaderBuilder,
                    width,
                    height
                )
            }

            var contrastValue by remember { mutableStateOf(8f) }
            var midPointValue by remember { mutableStateOf(0.5f) }
            var saturationValue by remember { mutableStateOf(1.0f) }

            shaderBuilder.uniform(
                "contrast",
                contrastValue
            )
            shaderBuilder.uniform(
                "midPoint",
                midPointValue
            )
            shaderBuilder.uniform(
                "saturation",
                saturationValue
            )

            Column (
                modifier = Modifier.width(300.dp)
            ){
                Text(text = "contrast $contrastValue")
                Slider(
                    value = contrastValue,
                    onValueChange = {
                        contrastValue = it
                    },
                    steps = 20,
                    valueRange = 0.5f..10f,
                )

                Text(text = "midPoint $midPointValue")
                Slider(
                    value = midPointValue,
                    onValueChange = {
                        midPointValue = it
                    },
                    steps = 20,
                    valueRange = 0.1f..0.8f,
                )

                Text(text = "saturation $saturationValue")
                Slider(
                    value = saturationValue,
                    onValueChange = {
                        saturationValue = it
                    },
                    steps = 20,
                    valueRange = 1f..5f,
                )
            }


        }
    }
}

@Composable
fun ShaderCanvas(
    shaderBuilder: RuntimeShaderBuilder,
    width: Dp,
    height: Dp,
) {
    Canvas(
        Modifier.width(width).height(height).border(2.dp, Color.Blue)
    ) {
        drawIntoCanvas { canvas ->
            Paint().use { paint ->
                println("drawIntoCanvas")
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
