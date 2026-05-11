package gustavo.brilhante.magiccube2.presentation.cube.engine

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class CubeRotationEngine : ICubeRotationEngine {
    private val lock = ReentrantLock()
    private var state = CubeRotationState()

    override val angleX: Float get() = lock.withLock { state.angleX }
    override val angleY: Float get() = lock.withLock { state.angleY }
    override val isInertiaActive: Boolean get() = lock.withLock { state.isInertiaActive }

    override fun updateRotation(screenDx: Float, screenDy: Float, scale: Float) {
        lock.withLock {
            state = state.copy(
                isInertiaActive = false,
                angleXAux = state.angleX,
                angleYAux = state.angleY,
                angleX = state.angleX + screenDx * scale,
                angleY = state.angleY + screenDy * scale
            )
        }
    }

    override fun startInertia() {
        lock.withLock {
            state = state.copy(
                isInertiaActive = true,
                inertiaInc = 1f
            )
        }
    }

    override fun stopInertia() {
        lock.withLock {
            state = state.copy(isInertiaActive = false)
        }
    }

    override fun tickInertia() {
        lock.withLock {
            if (!state.isInertiaActive) return
            
            var newAngleX = state.angleX
            var newAngleY = state.angleY
            var newInertiaInc = state.inertiaInc
            var newIsActive = state.isInertiaActive

            if (state.angleX - state.angleXAux < -2) newAngleX -= state.inertiaInc
            if (state.angleX - state.angleXAux > 2) newAngleX += state.inertiaInc
            if (state.angleY - state.angleYAux < -2) newAngleY -= state.inertiaInc
            if (state.angleY - state.angleYAux > 2) newAngleY += state.inertiaInc
            
            newInertiaInc -= 0.1f
            if (newInertiaInc < 0.5f) {
                newIsActive = false
                newInertiaInc = 5f
            }
            
            state = state.copy(
                angleX = newAngleX,
                angleY = newAngleY,
                inertiaInc = newInertiaInc,
                isInertiaActive = newIsActive
            )
        }
    }

    override fun getRotationState(): CubeRotationState = lock.withLock { state }
}
