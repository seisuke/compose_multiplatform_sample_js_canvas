import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DefaultMonotonicFrameClock
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.isActive
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import org.jetbrains.skiko.currentNanoTime
import sample.ShaderSample

fun main() = application {
    val windowState = rememberWindowState()
    val time = remember { AnimationState(0f) }
    val fps = remember { mutableStateOf(0) }
    requestAnimationFrame(fps) {
        time.animateTo(
            targetValue = time.value + 0.01f,
            sequentialAnimation = true,
        )
    }

    Window(
        onCloseRequest = ::exitApplication,
        resizable = true,
        state = windowState,
    ) {

        val shaderBuilder = ShaderSample.shaderBuilder

        Text(text = fps.value.toString())

        Canvas(
            Modifier.width(windowState.size.width)
                .height(windowState.size.height)
        ) {
            shaderBuilder.uniform(
                "iResolution",
                windowState.size.width.value,
                windowState.size.height.value
            )
            shaderBuilder.uniform("iTime", time.value)

            Paint().use { paint ->
                drawIntoCanvas { canvas ->
                    paint.imageFilter = ImageFilter.makeRuntimeShader(
                        runtimeShaderBuilder = shaderBuilder,
                        shaderName = "content",
                        input = null
                    )
                    canvas.nativeCanvas.apply {
                        drawRect(
                            Rect(
                                0f,
                                0f,
                                windowState.size.width.value,
                                windowState.size.height.value
                            ),
                            paint
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun requestAnimationFrame(
    fps: MutableState<Int>,
    onFrame: suspend () -> Unit
) {
    val dt = remember { mutableStateOf(0L) }
    val previousTime = remember { mutableStateOf(currentNanoTime() / 1_000_000L) }
    val fpsCounter = ShaderSample.fpsCounter

    LaunchedEffect(Unit) {
        while (isActive) {
            DefaultMonotonicFrameClock.withFrameMillis { millSecond ->
                dt.value = millSecond - previousTime.value
                fpsCounter.tick()
                fps.value = fpsCounter.average
            }
            onFrame()
        }
    }
}
