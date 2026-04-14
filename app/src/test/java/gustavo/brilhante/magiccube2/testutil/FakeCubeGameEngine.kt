package gustavo.brilhante.magiccube2.testutil

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

    override val cubes: List<Cube> = emptyList()
    override val cubeGrid: Array<Array<IntArray>> = Array(3) { Array(3) { IntArray(3) } }

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

    override fun rotateClosestSideToScreen(rotationSense: Int) {
        rotateClosestSideCallCount++
        lastRotationSense = rotationSense
    }

    override fun prepareFrameRotation() {
        prepareFrameRotationCallCount++
    }

    override fun postFrameAdvance() {
        postFrameAdvanceCallCount++
    }

    private fun reset() {
        rotation = RotationState(isAnimating = false)
        rotatedAngle = 0f
        rotateClosestSideCallCount = 0
        lastRotationSense = 0
        prepareFrameRotationCallCount = 0
        postFrameAdvanceCallCount = 0
    }
}
