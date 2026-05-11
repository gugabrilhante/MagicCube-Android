package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.domain.model.Vector3
import org.junit.Assert.assertEquals
import org.junit.Test

class CubeFaceGeometryResolverTest {

    private val resolver = CubeFaceGeometryResolver()

    @Test
    fun `given X normal when faceLocalTangents then returns Y and Z tangents`() {
        val normal = Vector3(1f, 0f, 0f)
        val tangents = resolver.faceLocalTangents(normal)
        assertEquals(Vector3(0f, 1f, 0f), tangents.t1)
        assertEquals(Vector3(0f, 0f, 1f), tangents.t2)
    }

    @Test
    fun `given Y normal when faceLocalTangents then returns X and Z tangents`() {
        val normal = Vector3(0f, 1f, 0f)
        val tangents = resolver.faceLocalTangents(normal)
        assertEquals(Vector3(1f, 0f, 0f), tangents.t1)
        assertEquals(Vector3(0f, 0f, 1f), tangents.t2)
    }

    @Test
    fun `given Z normal when faceLocalTangents then returns X and Y tangents`() {
        val normal = Vector3(0f, 0f, 1f)
        val tangents = resolver.faceLocalTangents(normal)
        assertEquals(Vector3(1f, 0f, 0f), tangents.t1)
        assertEquals(Vector3(0f, 1f, 0f), tangents.t2)
    }
}
