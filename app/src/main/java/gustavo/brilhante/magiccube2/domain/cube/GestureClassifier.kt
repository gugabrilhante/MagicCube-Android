package gustavo.brilhante.magiccube2.domain.cube

import kotlin.math.abs

class GestureClassifier {
    companion object {
        private const val DIST_THRESHOLD = 100
        private const val TIME_THRESHOLD = 250L
    }

    /**
     * Classifies a touch gesture as a swipe, drag, or no-op based on elapsed time and
     * total displacement since the gesture started.
     */
    fun classifyMovement(dt: Long, dx: Float, dy: Float): MovementType {
        return if (dt < TIME_THRESHOLD) {
            when {
                abs(dx) > abs(dy) && abs(dx) > DIST_THRESHOLD ->
                    if (dx > 0) MovementType.SWIPE_RIGHT else MovementType.SWIPE_LEFT
                abs(dy) > DIST_THRESHOLD ->
                    if (dy > 0) MovementType.SWIPE_DOWN else MovementType.SWIPE_UP
                else -> MovementType.NONE
            }
        } else {
            MovementType.DRAG
        }
    }
}
