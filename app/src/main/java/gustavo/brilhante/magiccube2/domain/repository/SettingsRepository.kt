package gustavo.brilhante.magiccube2.domain.repository

import gustavo.brilhante.magiccube2.domain.CubeSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settingsFlow: Flow<CubeSettings>
    val current: CubeSettings
    suspend fun load()
    suspend fun save(settings: CubeSettings)
}
