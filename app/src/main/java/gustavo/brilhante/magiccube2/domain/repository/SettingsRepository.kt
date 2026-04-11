package gustavo.brilhante.magiccube2.domain.repository

import gustavo.brilhante.magiccube2.domain.CubeSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settingsFlow: StateFlow<CubeSettings>
    suspend fun getCurrent(): CubeSettings
    suspend fun save(settings: CubeSettings)
}
