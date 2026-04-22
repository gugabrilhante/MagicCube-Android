package gustavo.brilhante.magiccube2.compose

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import gustavo.brilhante.magiccube2.R
import gustavo.brilhante.magiccube2.presentation.options.OptionsViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsScreen(
    viewModel: OptionsViewModel = koinViewModel(),
    onBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    val cubeTransition   = LocalCubeTransition.current
    val isActive         = cubeTransition?.isActive  ?: false
    val isReverse        = cubeTransition?.isReverse ?: false
    val progress         = cubeTransition?.progress  ?: 0f

    // Mini-cube: hidden while traveling, crossfades in after landing (forward only).
    // During reverse it stays hidden — the overlay takes its role.
    val miniAlpha    = miniCubeAlpha(isActive, isReverse, progress)

    // Cards: fade out quickly as the cube departs (reverse only).
    val contentAlpha = optionsContentAlpha(isActive, isReverse, progress)

    // Colors for the mini-cube: live snapshot or standard fallback.
    val miniColors = cubeTransition?.cubeColors?.takeIf { it.size == 9 } ?: DefaultCubeColors

    AnimatedBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.options),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Landing zone ───────────────────────────────────────────────────────
                // Target of the FORWARD transition, departure point of the REVERSE.
                // Its alpha is driven by [miniCubeAlpha] so it is never visible at the
                // same time as the overlay cube (seamless crossfade).
                CubeFacePreview(
                    colors = miniColors,
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 4.dp)
                        .size(56.dp)
                        .graphicsLayer { alpha = miniAlpha }
                        .onGloballyPositioned { coords ->
                            val bounds = coords.boundsInRoot()
                            cubeTransition?.updateTarget(
                                left   = bounds.left,
                                top    = bounds.top,
                                sizePx = bounds.width
                            )
                        }
                )

                // ── Cards (fade out during reverse transition) ─────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { alpha = contentAlpha },
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // How to Play
                    CollapsibleCard(
                        title = stringResource(R.string.how_to_play),
                        initiallyExpanded = true
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            HintItem(text = stringResource(R.string.rotate_cube_hint))
                            HintItem(text = stringResource(R.string.rotate_face_hint))
                        }
                    }

                    // Settings
                    MagicCubeCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CubeSectionTitle(
                                text = stringResource(R.string.options_section),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            SettingSliderRow(
                                label        = stringResource(R.string.shuffle),
                                value        = state.shuffle,
                                displayValue = "${state.shuffleDisplay}",
                                onValueChange = viewModel::setShuffle,
                                valueRange   = 0f..10f,
                                steps        = 9,
                            )
                            SettingSliderRow(
                                label        = stringResource(R.string.speed),
                                value        = state.speed,
                                displayValue = "${state.speed}",
                                onValueChange = viewModel::setSpeed
                            )
                            SettingSliderRow(
                                label        = stringResource(R.string.size),
                                value        = state.size,
                                displayValue = "${state.size}",
                                onValueChange = viewModel::setSize
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = viewModel::resetSettings,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text(
                                    text  = stringResource(R.string.reset_to_default),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Private composables
// ---------------------------------------------------------------------------

@Composable
private fun SettingSliderRow(
    label: String,
    value: Int,
    displayValue: String,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 1f..10f,
    steps: Int = 8,
) {
    val animatedDisplay by animateIntAsState(
        targetValue = displayValue.toIntOrNull() ?: value,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "badge_$label"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text  = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text  = "$animatedDisplay",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value         = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange    = valueRange,
            steps         = steps,
            modifier      = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun HintItem(text: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .offset(y = 7.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
        Text(
            text  = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
