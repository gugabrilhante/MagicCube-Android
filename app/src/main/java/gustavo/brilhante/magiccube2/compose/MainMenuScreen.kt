package gustavo.brilhante.magiccube2.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
    val cubeTransition = LocalCubeTransition.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is MainMenuUiEvent.NavigateToStart -> onStartClick()
                is MainMenuUiEvent.NavigateToOptions -> onOptionsClick()
            }
        }
    }

    // Keep the transition state's color snapshot up-to-date so the overlay
    // always has the correct face colors ready when play() is triggered.
    LaunchedEffect(cubeColors) {
        cubeTransition?.cubeColors = cubeColors
    }

    AnimatedBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(72.dp))

                CubeFacePreview(
                    colors = cubeColors,
                    modifier = Modifier
                        .size(200.dp)
                        // Keep the transition state up-to-date with the cube's current position.
                        // onGloballyPositioned fires after every layout pass, so the state is
                        // always accurate even after rotation or resizing.
                        .onGloballyPositioned { coords ->
                            val bounds = coords.boundsInRoot()
                            cubeTransition?.updateSource(
                                left = bounds.left,
                                top = bounds.top,
                                sizePx = bounds.width
                            )
                        }
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MagicCubeButton(
                    text = stringResource(id = R.string.start),
                    onClick = viewModel::onStartClick
                )
                MagicCubeButton(
                    text = stringResource(id = R.string.options),
                    onClick = viewModel::onOptionsClick
                )
                MagicCubeButton(
                    text = stringResource(id = R.string.quit),
                    onClick = onQuitClick
                )
            }
        }
    }
}
