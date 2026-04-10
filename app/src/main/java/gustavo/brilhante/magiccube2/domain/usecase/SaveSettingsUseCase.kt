package gustavo.brilhante.magiccube2.domain.usecase

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.domain.repository.SettingsRepository

class SaveSettingsUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(settings: CubeSettings) = repository.save(settings)
}
