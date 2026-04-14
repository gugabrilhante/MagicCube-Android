package gustavo.brilhante.magiccube2.grafic

import android.content.Context
import android.content.res.Resources
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import gustavo.brilhante.magiccube2.presentation.cube.CubeViewModel

/**
 * Custom [GLSurfaceView] that owns touch-event dispatch and forwards events to
 * [CubeViewModel]. Keeping touch handling here (rather than in the Composable's
 * pointerInput) avoids fighting GL's raw event consumption and keeps the ViewModel
 * free of Android View references.
 */
class CubeSurfaceView(
    context: Context,
    private val viewModel: CubeViewModel,
) : GLSurfaceView(context) {

    private var previousX = 0f
    private var previousY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val metrics = Resources.getSystem().displayMetrics

        when (event.action) {
            MotionEvent.ACTION_DOWN -> viewModel.onActionDown(x, y, metrics.widthPixels, metrics.heightPixels)
            MotionEvent.ACTION_UP -> viewModel.onActionUp(x, y)
            MotionEvent.ACTION_MOVE -> viewModel.onActionMove(x, y, previousX, previousY)
        }

        previousX = x
        previousY = y
        return true
    }
}