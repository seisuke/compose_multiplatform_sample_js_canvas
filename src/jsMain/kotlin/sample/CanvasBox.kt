package sample

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

@Composable
actual inline fun CanvasBox(
    width: Dp,
    height: Dp,
    content: @Composable BoxScope.() -> Unit
) {
}
