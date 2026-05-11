package gustavo.brilhante.magiccube2.presentation.cube.engine

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.grafic.ICubeGameEngine
import gustavo.brilhante.magiccube2.grafic.IMatrixTracker
import gustavo.brilhante.magiccube2.presentation.cube.CubeDrawCommand

class CubeTraversalEngine(
    private val matrixTracker: IMatrixTracker,
    private val sliceResolver: CubeSliceResolver,
    private val commandFactory: CubeDrawCommandFactory
) : ICubeTraversalEngine {

    private val dist = 2.12f

    override fun buildFrame(
        engine: ICubeGameEngine,
        settings: CubeSettings,
        rotationState: CubeRotationState,
        projectionMatrix: FloatArray
    ): List<CubeDrawCommand> {
        matrixTracker.reset()

        matrixTracker.translate(0f, 0f, (-20 + settings.size).toFloat())

        matrixTracker.rotate(rotationState.angleX, 0f, 1f, 0f)
        matrixTracker.rotate(rotationState.angleY, 1f, 0f, 0f)

        engine.prepareFrameRotation()

        val rotState = engine.rotation
        val commands = mutableListOf<CubeDrawCommand>()

        for (z in 0..2) {
            matrixTracker.push()
            
            val rotateY = sliceResolver.shouldRotateY(rotState.activeSlice, z)
            if (rotateY) matrixTracker.rotate(engine.rotatedAngle, 0f, 1f, 0f)
            
            val worldY = (z - 1) * dist
            matrixTracker.translate(0f, worldY, 0f)

            for (y in 0..2) {
                matrixTracker.push()
                
                val rotateZ = sliceResolver.shouldRotateZ(rotState.activeSlice, y)
                if (rotateZ) matrixTracker.rotate(engine.rotatedAngle, 0f, 0f, 1f)
                
                val worldZ = (y - 1) * dist
                matrixTracker.translate(0f, 0f, worldZ)

                for (x in 0..2) {
                    matrixTracker.push()
                    
                    val rotateX = sliceResolver.shouldRotateX(rotState.activeSlice, x)
                    if (rotateX) matrixTracker.rotate(engine.rotatedAngle, 1f, 0f, 0f)
                    
                    val worldX = (x - 1) * dist
                    matrixTracker.translate(worldX, 0f, 0f)

                    val cubeIndex = engine.cubeGrid[x][z][y]
                    commands.add(commandFactory.createCommand(engine.cubes[cubeIndex], projectionMatrix, matrixTracker))

                    if (rotationState.isInertiaActive) {
                        updateFaceCenterPositions(engine, cubeIndex)
                    }

                    matrixTracker.pop()
                }
                matrixTracker.pop()
            }
            matrixTracker.pop()
        }

        return commands
    }

    private fun updateFaceCenterPositions(engine: ICubeGameEngine, cubeIndex: Int) {
        engine.faceCenterCubes.forEachIndexed { idx, entry ->
            if (cubeIndex == entry.first) {
                engine.faceCenterPositions[idx].z = -matrixTracker.getZ()
                engine.faceCenterPositions[idx].y = matrixTracker.getY()
                engine.faceCenterPositions[idx].x = matrixTracker.getX()
            }
        }
    }
}
