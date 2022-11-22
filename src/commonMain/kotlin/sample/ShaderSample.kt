package sample

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder
import org.jetbrains.skia.paragraph.Alignment
import org.jetbrains.skia.paragraph.Direction
import org.jetbrains.skia.paragraph.FontCollection
import org.jetbrains.skia.paragraph.ParagraphBuilder
import org.jetbrains.skia.paragraph.ParagraphStyle
import org.jetbrains.skiko.FPSCounter
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoView

// https://github.com/dima-avdeev-jb/skiko-mpp-shader/blob/main/src/commonMain/kotlin/org/jetbrains/skiko/sample/ShaderSample.kt
class ShaderSample(private val layer: SkiaLayer): SkikoView {

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        fpsCounter.tick()
        val w = minOf(width, height).toFloat()
        val h = minOf(width, height).toFloat()
        shaderBuilder.uniform("iTime", (nanoTime % 1_000_000_000_000) / 1e9f)
        shaderBuilder.uniform("iResolution", w, h)
        val imageFilter2 = ImageFilter.makeRuntimeShader(
            runtimeShaderBuilder = shaderBuilder,
            shaderName = "content",
            input = null
        )

        val watchFill = Paint().apply {
            imageFilter = imageFilter2
        }
        canvas.drawRect(Rect(0f, 0f, w, h), watchFill)

        val builder = ParagraphBuilder(style, font)
        builder.addText(fpsCounter.average.toString())
        val paragraph = builder.build()
        paragraph.layout(800f)
        paragraph.paint(canvas, 10f, 10f)

        canvas.resetMatrix()
    }

    companion object {
        private const val sksl = """
            uniform float2 iResolution;
            uniform float iTime;
            uniform shader content;
            vec4 main(vec2 FC) {
              vec4 o = vec4(0);
              vec2 p = vec2(0), c=p, u=FC.xy*2.-iResolution.xy;
              float a;
              for (float i=0; i<4e2; i++) {
                a = i/2e2-1.;
                p = cos(i*2.4+iTime+vec2(0,11))*sqrt(1.-a*a);
                c = u/iResolution.y+vec2(p.x,a)/(p.y+2.);
                o += (cos(i+vec4(0,2,4,0))+1.)/dot(c,c)*(1.-p.y)/3e4;
              }
              return o;
            }
            """

        private val runtimeEffect = RuntimeEffect.makeForShader(sksl)
        val shaderBuilder = RuntimeShaderBuilder(runtimeEffect)
        val fpsCounter = FPSCounter()
        val style = ParagraphStyle().apply {
            alignment = Alignment.START
            direction = Direction.LTR
            textStyle = textStyle.apply {
                fontSize = 40.0f
                maxLinesCount = 1
                color = 0xFFFF0000.toInt()
            }
        }
        val font = FontCollection().apply {
            setDefaultFontManager(FontMgr.default)
        }

    }
}
