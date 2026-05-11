package gustavo.brilhante.magiccube2.app

import android.app.Application
import gustavo.brilhante.magiccube2.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

class MagicCubeApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (GlobalContext.getOrNull() == null) {
            GlobalContext.startKoin {
                androidContext(this@MagicCubeApp)
                modules(appModule)
            }
        }
    }
}