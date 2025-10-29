package gustavo.brilhante.magiccube2.app

import android.app.Application
import gustavo.brilhante.magiccube2.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MagicCubeApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MagicCubeApp)
            modules(appModule)
        }
    }
}