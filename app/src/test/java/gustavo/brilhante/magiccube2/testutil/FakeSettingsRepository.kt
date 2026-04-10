package gustavo.brilhante.magiccube2.testutil

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingsRepository(
    initial: CubeSettings = CubeSettings()
) : SettingsRepository {

    private val _settings = MutableStateFlow(initial)

    override val settingsFlow: Flow<CubeSettings> = _settings.asStateFlow()
    override val current: CubeSettings get() = _settings.value

    var loadCalled = false
    var lastSaved: CubeSettings? = null

    override suspend fun load() {
        loadCalled = true
    }

    override suspend fun save(settings: CubeSettings) {
        lastSaved = settings
        _settings.value = settings
    }

    fun emit(settings: CubeSettings) {
        _settings.value = settings
    }
}
