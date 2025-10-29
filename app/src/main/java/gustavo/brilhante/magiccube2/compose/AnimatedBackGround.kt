package gustavo.brilhante.magiccube2.compose

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedBackground(content: @Composable () -> Unit) {
    val transition = rememberInfiniteTransition(label = "")
    val color1 by transition.animateColor(
        initialValue = Color(0xFF141E30),
        targetValue = Color(0xFF243B55),
        animationSpec = infiniteRepeatable(
            tween(400000, easing = LinearEasing),
            RepeatMode.Reverse
        ), label = ""
    )
    val color2 by transition.animateColor(
        initialValue = Color(0xFF0F2027),
        targetValue = Color(0xFF203A43),
        animationSpec = infiniteRepeatable(
            tween(600000, easing = LinearEasing),
            RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(color1, color2)))
    ) {
        content()
    }
}
