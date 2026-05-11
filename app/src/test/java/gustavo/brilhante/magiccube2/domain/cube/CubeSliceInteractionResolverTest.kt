package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.domain.model.FaceTangents
import gustavo.brilhante.magiccube2.domain.model.Vector2
import gustavo.brilhante.magiccube2.domain.model.Vector3
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class CubeSliceInteractionResolverTest {

    private val geometryResolver = mockk<FaceGeometryResolver>()
    private val rotationMath = mockk<RotationMath>()
    private val resolver = CubeSliceInteractionResolver(geometryResolver, rotationMath)

    @Test
    fun `computeSliceDelta should return correct delta`() {
        val normal = Vector3(0f, 0f, 1f)
        val t1 = Vector3(1f, 0f, 0f)
        val t2 = Vector3(0f, 1f, 0f)
        
        every { geometryResolver.faceLocalTangents(normal) } returns FaceTangents(t1, t2)
        every { rotationMath.localToScreenSpace(t1, 0f, 0f) } returns Vector2(1f, 0f)
        every { rotationMath.localToScreenSpace(t2, 0f, 0f) } returns Vector2(0f, 1f)

        val screenDelta = Vector2(10f, 0f)
        val delta = resolver.computeSliceDelta(screenDelta, normal, 0f, 0f)
        
        // gestureIsHorizontal = true, t1IsHorizontal = true -> useT1 = true
        // w1 = 10*1 + 0*0 = 10
        // signCorrection = 1 (t1.y=0 <= 0.5)
        assertEquals(10f, delta, 0.001f)
    }

    @Test
    fun `computeSliceDelta with sign correction`() {
        val normal = Vector3(1f, 0f, 0f) // normal.x - normal.z = 1 - 0 = 1 >= 0
        val t1 = Vector3(0f, 1f, 0f) // chosenTangent.y = 1 > 0.5
        val t2 = Vector3(0f, 0f, 1f)
        
        every { geometryResolver.faceLocalTangents(normal) } returns FaceTangents(t1, t2)
        every { rotationMath.localToScreenSpace(t1, 0f, 0f) } returns Vector2(0f, 1f)
        every { rotationMath.localToScreenSpace(t2, 0f, 0f) } returns Vector2(1f, 0f)

        val screenDelta = Vector2(0f, 10f) // gestureIsHorizontal = false
        val delta = resolver.computeSliceDelta(screenDelta, normal, 0f, 0f)
        
        // gestureIsHorizontal = false, t1IsHorizontal = false -> useT1 = true
        // w1 = 0*0 + 10*1 = 10
        // signCorrection: chosenTangent=t1, t1.y=1 > 0.5. normal.x - normal.z = 1 >= 0 -> 1f
        assertEquals(10f, delta, 0.001f)
    }
}
