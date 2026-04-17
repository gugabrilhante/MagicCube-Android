package gustavo.brilhante.magiccube2.presentation.cube

import gustavo.brilhante.magiccube2.domain.cube.MovementType

/**
 * Application-service boundary for all cube touch interaction.
 *
 * Implementations own drag state, snap decisions, and swipe routing.
 * They return [CubeControllerEffect]s — the ViewModel applies them to render
 * and engine state, keeping those two concerns cleanly separated.
 */
interface ICubeInteractor {

    /** Returns the movement classification — exposed for backward-compat ViewModel delegation. */
    fun classifyMovement(dt: Long, dx: Float, dy: Float): MovementType

    fun onActionDown(
        x: Float,
        y: Float,
        screenWidth: Int,
        screenHeight: Int,
        drawCommands: List<CubeDrawCommand>,
    ): List<CubeControllerEffect>

    fun onActionMove(
        x: Float,
        y: Float,
        previousX: Float,
        previousY: Float,
        angleRotateX: Float,
        angleRotateY: Float,
        touchScaleFactor: Float,
    ): List<CubeControllerEffect>

    fun onActionUp(x: Float, y: Float): List<CubeControllerEffect>
}

/**
 * Side-effects produced by [ICubeInteractor] and applied by [CubeViewModel].
 *
 * Splitting commands from execution keeps the controller free of Android/GL imports
 * and makes every interaction path unit-testable by asserting on the effect list.
 */
sealed interface CubeControllerEffect {
    /** Rotate the whole cube (no cubelet selected). */
    data class RotateWholeCube(val screenDx: Float, val screenDy: Float, val scale: Float) : CubeControllerEffect

    /** Update the in-progress drag angle on the active slice. */
    data class UpdateSliceAngle(val angle: Float) : CubeControllerEffect

    /** Commit or snap back the active slice at gesture end. */
    data class SnapSlice(val snapAngle: Float) : CubeControllerEffect

    /** Activate inertia on gesture end (no active slice). */
    data object StartInertia : CubeControllerEffect

    /** Trigger a face rotation from a recognised swipe. */
    data class TriggerSwipeRotation(val sense: Int) : CubeControllerEffect

    /** Reflect dragging-slice status in UI state. */
    data class SetDraggingSlice(val isDragging: Boolean) : CubeControllerEffect
}
