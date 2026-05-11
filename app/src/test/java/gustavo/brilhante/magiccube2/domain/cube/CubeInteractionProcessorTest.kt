package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.domain.math.MatrixMath
import gustavo.brilhante.magiccube2.domain.model.FaceTangents
import gustavo.brilhante.magiccube2.domain.model.Vector2
import gustavo.brilhante.magiccube2.domain.model.Vector3
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class CubeInteractionProcessorTest {

    private val gestureClassifier = mockk<GestureClassifier>()
    private val rotationMath = mockk<RotationMath>()
    private val geometryResolver = mockk<FaceGeometryResolver>()
    private val sliceResolver = mockk<SliceInteractionResolver>()
    private val visibilityCalculator = mockk<VisibleFacesCalculator>()
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
    fun `computeDragOnFace should return normalized drag and axis`() {
        val normal = Vector3(0f, 0f, 1f)
        val t1 = Vector3(1f, 0f, 0f)
        val t2 = Vector3(0f, 1f, 0f)
        val screenDelta = Vector2(10f, 0f)

        every { geometryResolver.faceLocalTangents(normal) } returns FaceTangents(t1, t2)
        every { rotationMath.localToScreenSpace(t1, 0f, 0f) } returns Vector2(1f, 0f)
        every { rotationMath.localToScreenSpace(t2, 0f, 0f) } returns Vector2(0f, 1f)

        val result = processor.computeDragOnFace(screenDelta, normal, 0f, 0f)

        // w1 = 10*1 + 0*0 = 10
        // w2 = 10*0 + 0*1 = 0
        // rawDrag = (1,0,0)*10 + (0,1,0)*0 = (10, 0, 0)
        // drag = normalize(10,0,0) = (1,0,0)
        // axis = normalize(cross((0,0,1), (1,0,0))) = normalize(0,1,0) = (0,1,0)
        
        assertEquals(Vector3(1f, 0f, 0f), result.localDragVector.vector)
        assertEquals(Vector3(0f, 1f, 0f), result.rotationAxis.vector)
    }

    @Test
    fun `classifyMovement delegates to gestureClassifier`() {
        every { gestureClassifier.classifyMovement(100L, 10f, 20f) } returns MovementType.SWIPE_RIGHT
        assertEquals(MovementType.SWIPE_RIGHT, processor.classifyMovement(100L, 10f, 20f))
    }
}
