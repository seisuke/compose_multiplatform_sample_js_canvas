import kotlinx.browser.document
import org.jetbrains.skiko.GenericSkikoView
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.onContentScaleChanged
import org.w3c.dom.HTMLCanvasElement
import sample.ShaderSample

fun shaderCanvasSample() {
    val skiaLayer = SkiaLayer()
    onContentScaleChanged = { scale -> println(scale) }
    val sample = ShaderSample(skiaLayer)
    skiaLayer.skikoView = GenericSkikoView(skiaLayer, sample)
    val canvas = document.getElementById("ComposeTarget") as HTMLCanvasElement
    canvas.setAttribute("tabindex", "0")
    skiaLayer.attachTo(canvas)
    skiaLayer.needRedraw()
}
