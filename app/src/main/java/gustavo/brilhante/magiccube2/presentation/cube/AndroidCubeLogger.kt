package gustavo.brilhante.magiccube2.presentation.cube

import android.util.Log
import gustavo.brilhante.magiccube2.domain.cube.CubeLogger

class AndroidCubeLogger : CubeLogger {
    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
}
