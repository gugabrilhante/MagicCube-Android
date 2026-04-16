package gustavo.brilhante.magiccube2.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val cubeColors by viewModel.cubeColors.collectAsState()

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

                CubeFacePreview(
                    colors = cubeColors,
                    modifier = Modifier.size(200.dp),
                )
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
