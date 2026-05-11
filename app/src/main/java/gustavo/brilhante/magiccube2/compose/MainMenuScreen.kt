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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import gustavo.brilhante.magiccube2.R
import gustavo.brilhante.magiccube2.presentation.MainMenuUiEvent
import gustavo.brilhante.magiccube2.presentation.MainMenuViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
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

    LifecycleResumeEffect(Unit) {
        viewModel.setShuffling(true)
        onPauseOrDispose {
            viewModel.setShuffling(false)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is MainMenuUiEvent.NavigateToStart -> onStartClick()
                is MainMenuUiEvent.NavigateToOptions -> onOptionsClick()
            }
        }
    }

    // Keep color snapshot fresh so the overlay always starts with the correct face.
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
                        // During the REVERSE transition the large cube hides while the
                        // overlay mini-cube is flying toward it, then crossfades in at
                        // handoff — exactly mirroring the forward landing behaviour.
                        .graphicsLayer {
                            alpha = largeCubeAlpha(
                                isActive = cubeTransition?.isActive ?: false,
                                isReverse = cubeTransition?.isReverse ?: false,
                                progress = cubeTransition?.progress ?: 0f
                            )
                        }
                        // Continuously report position so the overlay always has the
                        // correct start/end coordinates regardless of layout changes.
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
                    onClick = viewModel::onStartClick,
                    modifier = Modifier.testTag("start_button")
                )
                MagicCubeButton(
                    text = stringResource(id = R.string.options),
                    onClick = viewModel::onOptionsClick,
                    modifier = Modifier.testTag("options_button")
                )
                MagicCubeButton(
                    text = stringResource(id = R.string.quit),
                    onClick = onQuitClick,
                    modifier = Modifier.testTag("quit_button")
                )
            }
        }
    }
}
