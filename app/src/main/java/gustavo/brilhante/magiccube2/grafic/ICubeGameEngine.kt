package gustavo.brilhante.magiccube2.grafic

import gustavo.brilhante.magiccube2.domain.model.Vector3

/**
 * Abstraction over the cube game engine.
 * Allows mocking in unit tests of [gustavo.brilhante.magiccube2.presentation.cube.CubeViewModel].
 */
interface ICubeGameEngine {
    val cubes: List<Cube>
    val cubeGrid: Array<Array<IntArray>>
    var rotation: RotationState
    var rotatedAngle: Float
    val faceCenterPositions: Array<CubePosition>
    val faceCenterCubes: List<Pair<Int, CubeSide>>

    /** Select the visible face closest to the screen and queue its rotation. */
    fun rotateClosestSideToScreen(rotationSense: Int = 1)

    /** Updates the rotation of a slice based on a drag gesture. */
    fun updateRotationFromDrag(cubelet: Cube, normal: Vector3, dragVector: Vector3)

    /** Corrects the angle sign at the start of each frame. Called before rendering. */
    fun prepareFrameRotation()

    /** Advances the game state after rendering a frame. */
    fun postFrameAdvance()
}
