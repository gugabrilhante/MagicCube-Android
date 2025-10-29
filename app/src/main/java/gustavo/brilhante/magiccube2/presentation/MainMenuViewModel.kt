package gustavo.brilhante.magiccube2.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class MainMenuUiEvent {
    data object NavigateToStart : MainMenuUiEvent()
    data object NavigateToOptions : MainMenuUiEvent()
}

class MainMenuViewModel : ViewModel() {

    private val _uiEvent = MutableSharedFlow<MainMenuUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onStartClick() {
        viewModelScope.launch {
            delay(800) // delay de 0.8s antes de navegar
            _uiEvent.emit(MainMenuUiEvent.NavigateToStart)
        }
    }

    fun onOptionsClick() {
        viewModelScope.launch {
            delay(600)
            _uiEvent.emit(MainMenuUiEvent.NavigateToOptions)
        }
    }
}
