package gustavo.brilhante.magiccube2.di

import gustavo.brilhante.magiccube2.data.DataStoreSettingsDataSource
import gustavo.brilhante.magiccube2.data.SettingsLocalDataSource
import gustavo.brilhante.magiccube2.data.SettingsRepositoryImpl
import gustavo.brilhante.magiccube2.domain.repository.SettingsRepository
import gustavo.brilhante.magiccube2.domain.usecase.LoadSettingsUseCase
import gustavo.brilhante.magiccube2.domain.usecase.ObserveSettingsUseCase
import gustavo.brilhante.magiccube2.domain.usecase.SaveSettingsUseCase
import gustavo.brilhante.magiccube2.presentation.MainMenuViewModel
import gustavo.brilhante.magiccube2.presentation.cube.CubeViewModel
import gustavo.brilhante.magiccube2.presentation.options.OptionsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    // Data layer
    single<SettingsLocalDataSource> { DataStoreSettingsDataSource(androidContext()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    // Domain — use cases
    singleOf(::LoadSettingsUseCase)
    singleOf(::SaveSettingsUseCase)
    singleOf(::ObserveSettingsUseCase)

    // Presentation
    viewModelOf(::MainMenuViewModel)
    viewModelOf(::CubeViewModel)
    viewModelOf(::OptionsViewModel)
}
