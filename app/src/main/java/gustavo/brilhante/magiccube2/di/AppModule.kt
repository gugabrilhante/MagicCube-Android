package gustavo.brilhante.magiccube2.di

import gustavo.brilhante.magiccube2.data.SettingsRepository
import gustavo.brilhante.magiccube2.presentation.MainMenuViewModel
import gustavo.brilhante.magiccube2.presentation.cube.CubeViewModel
import gustavo.brilhante.magiccube2.presentation.options.OptionsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    singleOf(::SettingsRepository)

    viewModelOf(::MainMenuViewModel)

    viewModelOf(::CubeViewModel)

    viewModelOf(::OptionsViewModel)
}
