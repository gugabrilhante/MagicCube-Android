package gustavo.brilhante.magiccube2.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.size(24.dp)) {
            drawRect(color = cubeColors.random())
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}