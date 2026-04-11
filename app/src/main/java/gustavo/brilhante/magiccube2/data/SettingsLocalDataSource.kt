package gustavo.brilhante.magiccube2.data

import gustavo.brilhante.magiccube2.domain.CubeSettings
import kotlinx.coroutines.flow.Flow

interface SettingsLocalDataSource {
    val settingsFlow: Flow<CubeSettings>
    suspend fun save(settings: CubeSettings)
}
