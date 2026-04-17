package gustavo.brilhante.magiccube2.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gustavo.brilhante.magiccube2.R
import gustavo.brilhante.magiccube2.presentation.options.OptionsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun OptionsScreen(
    viewModel: OptionsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    AnimatedBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .background(Color.LightGray)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            CollapsibleCard(title = stringResource(R.string.how_to_play)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoText(stringResource(R.string.rotate_cube_hint))
                    InfoText(stringResource(R.string.rotate_face_hint))
                }
            }

            CollapsibleCard(
                title = stringResource(R.string.options_section),
                initiallyExpanded = false
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoText(stringResource(R.string.shuffle_description))
                    InfoText(stringResource(R.string.speed_description))
                    InfoText(stringResource(R.string.size_description))
                }
            }

            MagicCubeCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SettingRow(
                        label = stringResource(R.string.shuffle),
                        value = "${state.shuffleDisplay}",
                        onIncrease = viewModel::increaseShuffle,
                        onDecrease = viewModel::decreaseShuffle
                    )
                    SettingRow(
                        label = stringResource(R.string.speed),
                        value = "${state.speed}",
                        onIncrease = viewModel::increaseSpeed,
                        onDecrease = viewModel::decreaseSpeed
                    )
                    SettingRow(
                        label = stringResource(R.string.size),
                        value = "${state.size}",
                        onIncrease = viewModel::increaseSize,
                        onDecrease = viewModel::decreaseSize
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    MagicCubeButton(
                        text = stringResource(R.string.reset_to_default),
                        onClick = viewModel::resetSettings,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .height(60.dp)
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .shadow(4.dp, RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoText(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun SettingRow(
    label: String,
    value: String,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onIncrease) {
            Text(text = "+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.cube_blue))
        }
        IconButton(onClick = onDecrease) {
            Text(text = "−", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.cube_red))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 18.sp, color = Color.Black, modifier = Modifier.weight(1f))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    }
}
