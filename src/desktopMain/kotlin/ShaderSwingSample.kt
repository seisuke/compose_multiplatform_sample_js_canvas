import org.jetbrains.skiko.GenericSkikoView
import org.jetbrains.skiko.SkiaLayer
import sample.ShaderSample
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

fun main() {
    val skiaLayer = SkiaLayer()
    skiaLayer.addView(GenericSkikoView(skiaLayer, ShaderSample(skiaLayer)))
    SwingUtilities.invokeLater {
        val window = JFrame("Skiko example").apply {
            defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            preferredSize = Dimension(800, 600)
        }
        skiaLayer.attachTo(window.contentPane)
        skiaLayer.needRedraw()
        window.pack()
        window.isVisible = true
    }
}
