package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.domain.model.FaceTangents
import gustavo.brilhante.magiccube2.domain.model.Vector3
import kotlin.math.abs

interface FaceGeometryResolver {
    fun faceLocalTangents(normal: Vector3): FaceTangents
}

class CubeFaceGeometryResolver : FaceGeometryResolver {
    override fun faceLocalTangents(normal: Vector3): FaceTangents = when {
        abs(normal.x) > 0.5f -> FaceTangents(Vector3(0f, 1f, 0f), Vector3(0f, 0f, 1f))
        abs(normal.y) > 0.5f -> FaceTangents(Vector3(1f, 0f, 0f), Vector3(0f, 0f, 1f))
        else -> FaceTangents(Vector3(1f, 0f, 0f), Vector3(0f, 1f, 0f))
    }
}
