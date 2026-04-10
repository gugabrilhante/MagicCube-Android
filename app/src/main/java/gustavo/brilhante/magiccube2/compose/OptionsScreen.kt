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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

            CubeSectionTitle(stringResource(R.string.como_jogar))
            InfoText(stringResource(R.string.clique_na_tela_e_arraste_para_girar_o_cubo_por_inteiro))
            InfoText(stringResource(R.string.swipe_quickly_to))
            InfoText(stringResource(R.string.a_cor_do_bot_o_representa_o_quadrado_que_est_no_centro_da_face_a_ser_girada))
            InfoText(stringResource(R.string.existem_bot_es_nas_duas_laterais_cada_lateral_indica_o_sentido_em_que_a_face_do_cubo_girar))

            CubeSectionTitle(stringResource(R.string.op_es))
            InfoText(stringResource(R.string.quot_embaralhar_quot_indica_a_quantidade_de_giros_que_o_cubo_sofrer_para_ser_embaralhado_antes_de_o_jogo_iniciar))
            InfoText(stringResource(R.string.quot_velocidade_quot_indica_a_velocidade_que_o_cubo_girar_quando_a_tela_for_tocada_e_arrastada))
            InfoText(stringResource(R.string.quot_tamanho_quot_indica_o_tamanho_do_cubo_que_ser_exibido_na_tela))

            Spacer(modifier = Modifier.height(8.dp))

            SettingRow(
                label = stringResource(R.string.embaralhar),
                value = "${state.shuffleDisplay}",
                onIncrease = viewModel::increaseShuffle,
                onDecrease = viewModel::decreaseShuffle
            )
            SettingRow(
                label = stringResource(R.string.velocidade),
                value = "${state.speed}",
                onIncrease = viewModel::increaseSpeed,
                onDecrease = viewModel::decreaseSpeed
            )
            SettingRow(
                label = stringResource(R.string.tamanho),
                value = "${state.size}",
                onIncrease = viewModel::increaseSize,
                onDecrease = viewModel::decreaseSize
            )
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
            Text(text = "+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        IconButton(onClick = onDecrease) {
            Text(text = "−", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 18.sp, modifier = Modifier.weight(1f))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}
