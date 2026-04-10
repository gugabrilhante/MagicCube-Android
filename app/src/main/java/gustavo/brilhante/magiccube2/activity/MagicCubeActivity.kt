package gustavo.brilhante.magiccube2.activity

import android.content.res.Resources
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import gustavo.brilhante.magiccube2.grafic.CubeRenderer
import gustavo.brilhante.magiccube2.presentation.cube.CubeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MagicCubeActivity : AppCompatActivity() {

    private val cubeViewModel: CubeViewModel by viewModel()
    private var glSurfaceView: GLSurfaceView? = null
    private var previousX = 0f
    private var previousY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        glSurfaceView = GLSurfaceView(this).apply {
            setEGLContextClientVersion(3)
            setRenderer(CubeRenderer(cubeViewModel))
        }
        setContentView(glSurfaceView)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val metrics = Resources.getSystem().displayMetrics

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                cubeViewModel.onActionDown(x, y, metrics.widthPixels, metrics.heightPixels)
            }
            MotionEvent.ACTION_UP -> {
                cubeViewModel.onActionUp(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                cubeViewModel.onActionMove(x, y, previousX, previousY)
            }
        }

        previousX = x
        previousY = y
        return true
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }
}
