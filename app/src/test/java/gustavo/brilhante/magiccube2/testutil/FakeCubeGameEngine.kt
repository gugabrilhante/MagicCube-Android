package gustavo.brilhante.magiccube2.testutil

import gustavo.brilhante.magiccube2.domain.model.Vector3
import gustavo.brilhante.magiccube2.grafic.Cube
import gustavo.brilhante.magiccube2.grafic.CubePosition
import gustavo.brilhante.magiccube2.grafic.CubeSide
import gustavo.brilhante.magiccube2.grafic.ICubeGameEngine
import gustavo.brilhante.magiccube2.grafic.RotationState

/**
 * In-memory test double for [ICubeGameEngine].
 * Records calls so tests can assert on engine interactions without touching
 * any real GL or cube-logic code.
 */
class FakeCubeGameEngine : ICubeGameEngine {

    override val cubes: List<Cube> = List(27) { Cube() }
    override val cubeGrid: Array<Array<IntArray>> = Array(3) { x -> Array(3) { z -> IntArray(3) { y -> x * 9 + z * 3 + y } } }

    @Volatile override var rotation: RotationState = RotationState(isAnimating = false)
    override var rotatedAngle: Float = 0f

    override val faceCenterPositions: Array<CubePosition> = Array(6) { CubePosition() }
    override val faceCenterCubes: List<Pair<Int, CubeSide>> = emptyList()

    // --- Call recording ---
    var rotateClosestSideCallCount: Int = 0
        private set

    var lastRotationSense: Int = 0
        private set

    var prepareFrameRotationCallCount: Int = 0
        private set

    var postFrameAdvanceCallCount: Int = 0
        private set

    var updateRotationFromDragCallCount: Int = 0
        private set

    override fun rotateClosestSideToScreen(rotationSense: Int) {
        rotateClosestSideCallCount++
        lastRotationSense = rotationSense
    }

    override fun updateRotationFromDrag(
        cubelet: Cube,
        normal: Vector3,
        dragVector: Vector3,
    ) {
        updateRotationFromDragCallCount++
    }

    override fun prepareFrameRotation() {
        prepareFrameRotationCallCount++
    }

    override fun postFrameAdvance() {
        postFrameAdvanceCallCount++
    }

    fun reset() {
        rotation = RotationState(isAnimating = false)
        rotatedAngle = 0f
        rotateClosestSideCallCount = 0
        lastRotationSense = 0
        prepareFrameRotationCallCount = 0
        postFrameAdvanceCallCount = 0
        updateRotationFromDragCallCount = 0
    }
}
