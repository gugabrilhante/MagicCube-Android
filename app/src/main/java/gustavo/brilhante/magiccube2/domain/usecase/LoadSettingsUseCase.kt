package gustavo.brilhante.magiccube2.domain.usecase

import gustavo.brilhante.magiccube2.domain.repository.SettingsRepository

class LoadSettingsUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke() = repository.load()
}
