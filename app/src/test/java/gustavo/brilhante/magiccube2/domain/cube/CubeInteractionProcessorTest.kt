package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.domain.math.MatrixMath
import gustavo.brilhante.magiccube2.domain.model.Vector2
import gustavo.brilhante.magiccube2.domain.model.Vector3
import org.junit.Assert.assertEquals
import org.junit.Test

class CubeInteractionProcessorTest {

    private val gestureClassifier = GestureClassifier()
    private val rotationMath = CubeRotationMath()
    private val geometryResolver = CubeFaceGeometryResolver()
    private val sliceResolver = CubeSliceInteractionResolver(geometryResolver, rotationMath)
    private val visibilityCalculator = CubeVisibleFacesCalculator()
    private val matrixMath = MatrixMath()

    private val processor = CubeInteractionProcessor(
        gestureClassifier,
        rotationMath,
        geometryResolver,
        sliceResolver,
        visibilityCalculator,
        matrixMath
    )

    @Test
    fun `should classify movement via delegated component`() {
        val result = processor.classifyMovement(100L, 150f, 0f)
        assertEquals(MovementType.SWIPE_RIGHT, result)
    }

    @Test
    fun `should compute slice delta via delegated component`() {
        val normal = Vector3(0f, 0f, 1f)
        val screenDelta = Vector2(100f, 0f)
        val result = processor.computeSliceDelta(screenDelta, normal, 0f, 0f)
        assertEquals(100f, result, 0.001f)
    }

    @Test
    fun `should get visible faces via delegated component`() {
        val result = processor.getVisibleFaces(0f, 0f)
        // At least one face should be visible
        assertTrue(result.isNotEmpty())
    }
}

private fun assertTrue(condition: Boolean) {
    assert(condition)
}
