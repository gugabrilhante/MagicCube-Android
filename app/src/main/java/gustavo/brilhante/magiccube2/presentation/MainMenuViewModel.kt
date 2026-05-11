package gustavo.brilhante.magiccube2.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MainMenuUiEvent {
    data object NavigateToStart : MainMenuUiEvent()
    data object NavigateToOptions : MainMenuUiEvent()
}

class MainMenuViewModel : ViewModel() {

    private val _uiEvent = MutableSharedFlow<MainMenuUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _cubeColors = MutableStateFlow(CUBE_FACE_COLORS.shuffled())
    val cubeColors: StateFlow<List<Long>> = _cubeColors.asStateFlow()

    private val _isShuffling = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            while (true) {
                if (_isShuffling.value) {
                    delay(SHUFFLE_INTERVAL_MS)
                    _cubeColors.value = CUBE_FACE_COLORS.shuffled()
                } else {
                    delay(100) // Small delay to avoid tight loop when not shuffling
                }
            }
        }
    }

    fun setShuffling(enabled: Boolean) {
        _isShuffling.value = enabled
    }

    fun onStartClick() {
        viewModelScope.launch {
            delay(800)
            _uiEvent.emit(MainMenuUiEvent.NavigateToStart)
        }
    }

    fun onOptionsClick() {
        viewModelScope.launch {
            delay(600)
            _uiEvent.emit(MainMenuUiEvent.NavigateToOptions)
        }
    }

    companion object {
        private const val SHUFFLE_INTERVAL_MS = 1500L
        val CUBE_FACE_COLORS: List<Long> = listOf(
            0xFFFF0000L,
            0xFF0000FFL,
            0xFFFFFF00L,
            0xFF00FF00L,
            0xFFFFFFFFL,
            0xFFFFA500L,
            0xFFFF0000L,
            0xFF0000FFL,
            0xFFFFA500L,
        )
    }
}
