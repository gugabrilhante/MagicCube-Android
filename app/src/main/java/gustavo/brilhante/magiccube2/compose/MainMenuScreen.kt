package gustavo.brilhante.magiccube2.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gustavo.brilhante.magiccube2.R
import gustavo.brilhante.magiccube2.presentation.MainMenuUiEvent
import gustavo.brilhante.magiccube2.presentation.MainMenuViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainMenuScreen(
    modifier: Modifier,
    viewModel: MainMenuViewModel = koinViewModel(),
    onStartClick: () -> Unit,
    onOptionsClick: () -> Unit,
    onQuitClick: () -> Unit
) {
    val context = LocalContext.current
    val cubeColors = listOf(Color.Red, Color.Blue, Color.Yellow, Color.Green, Color.White, Color(0xFFFFA500),Color.Red, Color.Blue, Color(0xFFFFA500)) // 6 cores clássicas

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is MainMenuUiEvent.NavigateToStart -> {
                    onStartClick()
                }

                is MainMenuUiEvent.NavigateToOptions -> {
                    onOptionsClick()
                }
            }
        }
    }

    AnimatedBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Título + Logo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = FontFamily.Cursive
                    )
                )

                Spacer(modifier = Modifier.height(96.dp))

                // Cubo simples estilizado
                Canvas(modifier = Modifier.size(200.dp)) {
                    val cubeSize = size.minDimension / 3
                    val paint = Paint()
                    cubeColors.shuffled().forEachIndexed { index, color ->
                        paint.color = color
                        val row = index / 3
                        val col = index % 3
                        drawRect(
                            color = color,
                            topLeft = Offset(col * cubeSize, row * cubeSize),
                            size = Size(cubeSize - 4, cubeSize - 4)
                        )
                    }
                }
            }

            // Botões no rodapé
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MagicCubeButton(
                    text = stringResource(id = R.string.start),
                    onClick = {
                        viewModel.onStartClick()
                    }
                )
                MagicCubeButton(
                    text = stringResource(id = R.string.options),
                    onClick = {
                        viewModel.onOptionsClick()
                    }
                )
                MagicCubeButton(
                    text = stringResource(id = R.string.quit),
                    onClick = onQuitClick
                )
            }
        }
    }
}

@Composable
fun MagicCubeButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, Color.Gray),
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(0.8f)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(Color.LightGray)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Mini cubo colorido à esquerda
            Canvas(modifier = Modifier.size(24.dp)) {
                val cubeColors = listOf(Color.Red, Color.Blue, Color.Yellow, Color.Green, Color.White, Color(0xFFFFA500))
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
}
