package gustavo.brilhante.magiccube2.di

import gustavo.brilhante.magiccube2.presentation.MainMenuViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel{
        MainMenuViewModel()
    }

}