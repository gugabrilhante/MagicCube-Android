package gustavo.brilhante.magiccube2.presentation.cube

import androidx.lifecycle.ViewModel
import gustavo.brilhante.magiccube2.data.SettingsRepository
import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.grafic.CubeGameEngine
import kotlin.math.abs

class CubeViewModel(private val repository: SettingsRepository) : ViewModel() {

    val settings: CubeSettings get() = repository.current
    val engine = CubeGameEngine(settings.shuffle)

    // Shared state read by the GL thread and written by the UI thread
    @Volatile var angleRotateX: Float = 0f
    @Volatile var angleRotateY: Float = 0f
    @Volatile var isInertiaActive: Boolean = false

    private var angleRotateXAux: Float = 0f
    private var angleRotateYAux: Float = 0f
    private var inertiaInc: Float = 5f

    private var startX: Float = 0f
    private var startY: Float = 0f
    private var startTime: Long = 0L
    private var horizontalOrientation: Int = 1
    private var verticalOrientation: Int = 1

    private val touchScaleFactor: Float
        get() = settings.speed * ((180f / 320) / 5)

    // --- Touch handlers (called from UI thread) ---

    fun onActionDown(x: Float, y: Float, screenWidth: Int, screenHeight: Int) {
        verticalOrientation = if (x > screenWidth / 2) 1 else -1
        horizontalOrientation = if (y < screenHeight / 2) -1 else 1
        startX = x
        startY = y
        startTime = System.currentTimeMillis()
    }

    fun onActionUp(x: Float, y: Float) {
        val dx = x - startX
        val dy = y - startY
        val dt = System.currentTimeMillis() - startTime

        isInertiaActive = true
        inertiaInc = 5f

        when (getMovementType(dt, dx, dy)) {
            MovementType.SWIPE_UP -> triggerRotation(-1 * verticalOrientation)
            MovementType.SWIPE_DOWN -> triggerRotation(1 * verticalOrientation)
            MovementType.SWIPE_LEFT -> triggerRotation(1 * horizontalOrientation)
            MovementType.SWIPE_RIGHT -> triggerRotation(-1 * horizontalOrientation)
            else -> Unit
        }
    }

    fun onActionMove(x: Float, y: Float, previousX: Float, previousY: Float) {
        val dt = System.currentTimeMillis() - startTime
        if (getMovementType(dt, x - startX, y - startY) == MovementType.DRAG) {
            angleRotateXAux = angleRotateX
            angleRotateYAux = angleRotateY
            angleRotateX += (x - previousX) * touchScaleFactor
            angleRotateY += (y - previousY) * touchScaleFactor
        }
    }

    // --- Called from GL thread by CubeRenderer ---

    fun tickInertia() {
        if (!isInertiaActive) return
        if (angleRotateX - angleRotateXAux < -2) angleRotateX -= inertiaInc
        if (angleRotateX - angleRotateXAux > 2) angleRotateX += inertiaInc
        if (angleRotateY - angleRotateYAux < -2) angleRotateY -= inertiaInc
        if (angleRotateY - angleRotateYAux > 2) angleRotateY += inertiaInc
        inertiaInc -= 0.1f
        if (inertiaInc < 0.5f) {
            isInertiaActive = false
            inertiaInc = 5f
        }
    }

    // --- Private helpers ---

    private fun triggerRotation(rotationSense: Int) {
        if (!engine.rotating) {
            engine.rotating = true
            engine.rotateClosestSideToScreen(rotationSense)
        }
    }

    private fun getMovementType(dt: Long, dx: Float, dy: Float): MovementType {
        val distThreshold = 100
        val timeThreshold = 250L
        return if (dt < timeThreshold) {
            when {
                abs(dx) > abs(dy) && abs(dx) > distThreshold -> if (dx > 0) MovementType.SWIPE_RIGHT else MovementType.SWIPE_LEFT
                abs(dy) > distThreshold -> if (dy > 0) MovementType.SWIPE_DOWN else MovementType.SWIPE_UP
                else -> MovementType.NONE
            }
        } else {
            MovementType.DRAG
        }
    }
}

private enum class MovementType {
    SWIPE_UP, SWIPE_DOWN, SWIPE_LEFT, SWIPE_RIGHT, DRAG, NONE
}
