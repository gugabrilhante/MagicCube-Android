package gustavo.brilhante.magiccube2.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

@Composable
fun CubeFacePreview(
    colors: List<Long>,
    modifier: Modifier = Modifier,
) {
    require(colors.size == 9)

    // mapIndexed sobre lista de tamanho fixo (sempre 9) é válido em Compose:
    // os slots de remember são alocados na mesma ordem a cada recomposição.
    val animatedColors = colors.mapIndexed { index, argb ->
        animateColorAsState(
            targetValue = Color(argb),
            animationSpec = tween(durationMillis = 600, delayMillis = index * 333),
        ).value
    }

    Canvas(modifier = modifier) {
        val cellSize = size.minDimension / 3
        animatedColors.forEachIndexed { index, color ->
            val row = index / 3
            val col = index % 3
            drawRect(
                color = color,
                topLeft = Offset(col * cellSize, row * cellSize),
                size = Size(cellSize - 4f, cellSize - 4f),
            )
        }
    }
}
