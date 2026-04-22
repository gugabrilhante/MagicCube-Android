package gustavo.brilhante.magiccube2.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import gustavo.brilhante.magiccube2.R

@Composable
fun CubeSectionTitle(text: String, modifier: Modifier = Modifier) {
    val cubeColors = listOf(
        colorResource(R.color.cube_red),
        colorResource(R.color.cube_blue),
        colorResource(R.color.cube_yellow),
        colorResource(R.color.cube_green),
        colorResource(R.color.cube_white),
        colorResource(R.color.cube_orange)
    )
    // Stable random: picked once per composition instance, never changes on recompose
    val accentColor = remember { cubeColors.random() }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.size(18.dp)) {
            drawRect(color = accentColor)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
