package gustavo.brilhante.magiccube2.presentation.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.domain.usecase.LoadSettingsUseCase
import gustavo.brilhante.magiccube2.domain.usecase.ObserveSettingsUseCase
import gustavo.brilhante.magiccube2.domain.usecase.SaveSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OptionsViewModel(
    private val loadSettings: LoadSettingsUseCase,
    private val saveSettings: SaveSettingsUseCase,
    observeSettings: ObserveSettingsUseCase,
) : ViewModel() {

    val uiState: StateFlow<OptionsUiState> = observeSettings()
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, OptionsUiState())

    init {
        viewModelScope.launch { loadSettings() }
    }

    fun increaseShuffle() = updateState { it.copy(shuffle = (it.shuffle + 1).coerceAtMost(10)) }
    fun decreaseShuffle() = updateState { it.copy(shuffle = (it.shuffle - 1).coerceAtLeast(1)) }

    fun increaseSpeed() = updateState { it.copy(speed = (it.speed + 1).coerceAtMost(10)) }
    fun decreaseSpeed() = updateState { it.copy(speed = (it.speed - 1).coerceAtLeast(1)) }

    fun increaseSize() = updateState { it.copy(size = (it.size + 1).coerceAtMost(10)) }
    fun decreaseSize() = updateState { it.copy(size = (it.size - 1).coerceAtLeast(1)) }

    private fun updateState(transform: (OptionsUiState) -> OptionsUiState) {
        viewModelScope.launch {
            saveSettings(transform(uiState.value).toSettings())
        }
    }

    private fun CubeSettings.toUiState() = OptionsUiState(shuffle, speed, size)
    private fun OptionsUiState.toSettings() = CubeSettings(shuffle, speed, size)
}
