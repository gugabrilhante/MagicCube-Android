package gustavo.brilhante.magiccubev2.grafic

import android.opengl.GLSurfaceView
import android.util.Log
import gustavo.brilhante.magiccubev2.activity.MagicCubeActivity
import gustavo.brilhante.magiccubev2.activity.OptionsActivity
import javax.microedition.khronos.opengles.GL10

class CubeRenderer(private val mTranslucentBackground: Boolean) : GLSurfaceView.Renderer {
    var fielOfView: Float = 0f
    var angleRotateX: Float = 0f
    var speedY: Float = 0.1.toFloat()
    var angleRotateY: Float = 0f
    var speedX: Float = 0.1.toFloat()
    var inc: Float = 5f
    var angleRotateXAux: Float = 0f
    var angleRotateYAux: Float = 0f
    var tamanhoCubo: Int = 0

    //public final static int SS_SUNLIGHT = GL10.GL_LIGHT0;
    private var rotatedAngle = 0f
    private var soma = 0f
    private val dist = 2.12.toFloat()
    private var xdist = 0f
    private var ydist = 0f
    private var zdist = 0f


    var e: Int = 0
    var cubeMatrixIndex: Int = 0
    var sinal: Int = 1
    var aux1: Int = 0
    var aux2: Int = 0
    var n: Int = 0
    var i: Int = 0
    var j: Int = 0
    var a: Int = 1
    var cont: Int = 0
    var j2: Int = 0
    var i2: Int = 0
    var n2: Int = 0
    var pos: Array<Array<IntArray>> = Array(3) { Array(3) { IntArray(3) } }
    var pos_init: Array<Array<IntArray>> = Array(3) { Array(3) { IntArray(3) } }
    var face_color: Array<IntArray?> = arrayOfNulls(6)
    var cubeSide: FloatArray = FloatArray(6)
    var cubeSideIndex: List<Pair<Int, CubeSide>> = listOf(
        Pair(4, CubeSide.YELLOW), //yellow
        Pair(10, CubeSide.GREEN), //green
        Pair(12, CubeSide.ORANGE), //orange
        Pair(14, CubeSide.RED),
        Pair(16, CubeSide.BLUE), //blue
        Pair(22, CubeSide.WHITE)  //white
    )
    var eixoy: IntArray = intArrayOf(0, 1, 2, 3)
    var eixoz: IntArray = intArrayOf(1, 5, 3, 4)
    var eixox: IntArray = intArrayOf(0, 4, 2, 5)


    var embaralhando: Boolean = true
    var num_embaralhar: Int = 10 * (OptionsActivity.shuffle)


    var cubeList: java.util.ArrayList<Cube> = java.util.ArrayList()
    var cubeList2: java.util.ArrayList<Cube> = java.util.ArrayList()

    private val matrixTracker = MatrixTracker()

    init {
        cubeList.add(Cube('K', 'Y', 'K', 'G', 'O', 'K')) //0
        cubeList.add(Cube('K', 'Y', 'K', 'G', 'K', 'K')) //1
        cubeList.add(Cube('K', 'Y', 'R', 'G', 'K', 'K')) //2

        cubeList.add(Cube('K', 'Y', 'K', 'K', 'O', 'K')) //3
        cubeList.add(Cube('K', 'Y', 'K', 'K', 'K', 'K')) //4 middle yellow
        cubeList.add(Cube('K', 'Y', 'R', 'K', 'K', 'K')) //5

        cubeList.add(Cube('B', 'Y', 'K', 'K', 'O', 'K')) //6
        cubeList.add(Cube('B', 'Y', 'K', 'K', 'K', 'K')) //7
        cubeList.add(Cube('B', 'Y', 'R', 'K', 'K', 'K')) //8

        cubeList.add(Cube('K', 'K', 'K', 'G', 'O', 'K')) //9
        cubeList.add(Cube('K', 'K', 'K', 'G', 'K', 'K')) //10 middle green
        cubeList.add(Cube('K', 'K', 'R', 'G', 'K', 'K')) //11

        cubeList.add(Cube('K', 'K', 'K', 'K', 'O', 'K')) //12 middle orange
        cubeList.add(Cube('K', 'K', 'K', 'K', 'K', 'K')) //13
        cubeList.add(Cube('K', 'K', 'R', 'K', 'K', 'K')) //14 middle red

        cubeList.add(Cube('B', 'K', 'K', 'K', 'O', 'K')) //15
        cubeList.add(Cube('B', 'K', 'K', 'K', 'K', 'K')) //16 middle blue
        cubeList.add(Cube('B', 'K', 'R', 'K', 'K', 'K')) //17

        cubeList.add(Cube('K', 'K', 'K', 'G', 'O', 'W')) //18
        cubeList.add(Cube('K', 'K', 'K', 'G', 'K', 'W')) //19
        cubeList.add(Cube('K', 'K', 'R', 'G', 'K', 'W')) //20

        cubeList.add(Cube('K', 'K', 'K', 'K', 'O', 'W')) //21
        cubeList.add(Cube('K', 'K', 'K', 'K', 'K', 'W')) //22 middle white
        cubeList.add(Cube('K', 'K', 'R', 'K', 'K', 'W')) //23

        cubeList.add(Cube('B', 'K', 'K', 'K', 'O', 'W')) //24
        cubeList.add(Cube('B', 'K', 'K', 'K', 'K', 'W')) //25
        cubeList.add(Cube('B', 'K', 'R', 'K', 'K', 'W')) //26

        for (j in 0..26) {
            //cubeList.add(new Cube());

            pos_init[j % 3][n % 3][i % 3] = j
            pos[j % 3][n % 3][i % 3] = j
            if (i % 3 == 2 && j % 3 == 2) n++
            if (j % 3 == 2) i++
        }
    }

    //front==0
    //right==1
    //back==2
    //left==3
    //down==4
    //up==5
    fun ChangeColor(cubo: Int, face: Int, letter: Char) {
        if (face == 0) cubeList[cubo].setfront(letter)
        if (face == 1) cubeList[cubo].setright(letter)
        if (face == 2) cubeList[cubo].setback(letter)
        if (face == 3) cubeList[cubo].setleft(letter)
        if (face == 4) cubeList[cubo].setdown(letter)
        if (face == 5) cubeList[cubo].setup(letter)
    }

    fun GetColor(cubo: Int, face: Int): Char {
        if (face == 0) return cubeList[cubo].getfront()
        if (face == 1) return cubeList[cubo].getright()
        if (face == 2) return cubeList[cubo].getback()
        if (face == 3) return cubeList[cubo].getleft()
        if (face == 4) return cubeList[cubo].getdown()
        if (face == 5) return cubeList[cubo].getup()
        return 0.toChar()
    }

    fun SaveRot(cubo: Int) {
        var cor1: Char
        var cor2 = 0.toChar()
        var indice: Int
        var t1: Int
        var t2: Int


        if (rot == 0 || rot == 1 || rot == 6) {
            for (q in 0..3) {
                if (sense == 1) {
                    t1 = 0
                    t2 = 1
                    indice = q
                } else {
                    t1 = 3
                    t2 = 0
                    indice = (3 - q)
                }

                cor1 = if (q == 0) GetColor(cubo, eixoy[indice])
                else cor2
                cor2 = GetColor(cubo, eixoy[((indice + t2) % 4 + t1) % 4])
                ChangeColor(cubo, eixoy[((indice + t2) % 4 + t1) % 4], cor1)
            }
        }
        if (rot == 2 || rot == 3 || rot == 7) {
            for (q in 0..3) {
                if (sense == 1) {
                    t1 = 0
                    t2 = 1
                    indice = q
                } else {
                    t1 = 3
                    t2 = 0
                    indice = (3 - q)
                }

                cor1 = if (q == 0) GetColor(cubo, eixoz[indice])
                else cor2
                cor2 = GetColor(cubo, eixoz[((indice + t2) % 4 + t1) % 4])
                ChangeColor(cubo, eixoz[((indice + t2) % 4 + t1) % 4], cor1)
            }
        }
        if (rot == 4 || rot == 5 || rot == 8) {
            for (q in 0..3) {
                if (sense == 1) {
                    t1 = 0
                    t2 = 1
                    indice = q
                } else {
                    t1 = 3
                    t2 = 0
                    indice = (3 - q)
                }

                cor1 = if (q == 0) GetColor(cubo, eixox[indice])
                else cor2
                cor2 = GetColor(cubo, eixox[((indice + t2) % 4 + t1) % 4])
                ChangeColor(cubo, eixox[((indice + t2) % 4 + t1) % 4], cor1)
            }
        }
    }

    fun save() {
        var s1: Int
        var s2: Int


        if (rot == 0 || rot == 1 || rot == 6) {
            n2 = e
            s1 = 0
            while (s1 < 2) {
                i2 = 0
                j2 = 0
                s2 = 0
                while (s2 < 8) {
                    aux1 = if (sense == 1) {
                        if (s2 == 0) pos[j2][n2][i2]
                        else aux2
                    } else {
                        if (s2 == 0) pos[i2][n2][j2]
                        else aux2
                    }


                    if (i2 == 0 && j2 != 0) j2--
                    else if (j2 == 2 && i2 != 0) i2--
                    else if (i2 == 2 && j2 != 2) j2++
                    else if (j2 == 0 && i2 != 2) i2++

                    if (sense == 1) {
                        aux2 = pos[j2][n2][i2]
                        pos[j2][n2][i2] = aux1
                    } else {
                        aux2 = pos[i2][n2][j2]
                        pos[i2][n2][j2] = aux1
                    }

                    if (s1 == 1) SaveRot(aux1)
                    s2++
                }


                s1++
            }
        }

        if (rot == 2 || rot == 3 || rot == 7) {
            n2 = e
            s1 = 0
            while (s1 < 2) {
                i2 = 0
                j2 = 0
                s2 = 0
                while (s2 < 8) {
                    aux1 = if (sense == 1) {
                        if (s2 == 0) pos[j2][i2][n2]
                        else aux2
                    } else {
                        if (s2 == 0) pos[i2][j2][n2]
                        else aux2
                    }

                    if (i2 == 0 && j2 != 0) j2--
                    else if (j2 == 2 && i2 != 0) i2--
                    else if (i2 == 2 && j2 != 2) j2++
                    else if (j2 == 0 && i2 != 2) i2++

                    if (sense == 1) {
                        aux2 = pos[j2][i2][n2]
                        pos[j2][i2][n2] = aux1
                    } else {
                        aux2 = pos[i2][j2][n2]
                        pos[i2][j2][n2] = aux1
                    }

                    if (s1 == 1) SaveRot(aux1)
                    s2++
                }
                s1++
            }
        }

        if (rot == 4 || rot == 5 || rot == 8) {
            n2 = e
            s1 = 0
            while (s1 < 2) {
                i2 = 0
                j2 = 0
                s2 = 0
                while (s2 < 8) {
                    aux1 = if (sense == 1) {
                        if (s2 == 0) pos[n2][j2][i2]
                        else aux2
                    } else {
                        if (s2 == 0) pos[n2][i2][j2]
                        else aux2
                    }

                    if (i2 == 0 && j2 != 0) j2--
                    else if (j2 == 2 && i2 != 0) i2--
                    else if (i2 == 2 && j2 != 2) j2++
                    else if (j2 == 0 && i2 != 2) i2++

                    if (sense == 1) {
                        aux2 = pos[n2][j2][i2]
                        pos[n2][j2][i2] = aux1
                    } else {
                        aux2 = pos[n2][i2][j2]
                        pos[n2][i2][j2] = aux1
                    }

                    if (s1 == 1) SaveRot(aux1)
                    s2++
                }
                s1++
            }
        }


        //	
    }

    fun rotateClosestSideToScreen(rotationSense: Int = 1) {
        val indexMin = cubeSide.withIndex().minByOrNull { it.value }?.index ?: -1
        if (indexMin >= 0) {
            rot = cubeSideIndex[indexMin].second.rotation ?: 20
            sense = rotationSense * cubeSideIndex[indexMin].second.orientation
        }
    }

    fun verificar() {
        for (k in 0..5) {
            for (i in 0..2) {
                for (j in 0..2) {
                    if (k == 0) {
                        //pos[i][j][0];
                    }
                }
            }
        }
    }

    override fun onDrawFrame(gl: GL10) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

        gl.glClearColor(0.0f, 0.5f, 0.5f, 1.0f)

        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()

        matrixTracker.reset()

        tamanhoCubo = -20 + OptionsActivity.size //MODIFICADO!

        gl.translate(0.0f, 0.0f, tamanhoCubo.toFloat())

        xdist = 0f
        ydist = 0f
        zdist = 0f

        android.util.Log.d("TESTE", "TESTE")

        if (rotatedAngle >= 0 && sense == -1) {
            rotatedAngle *= -1f
        }

        if (MagicCubeActivity.isActivated) {
            if (angleRotateX - angleRotateXAux < -2) angleRotateX -= inc
            if (angleRotateX - angleRotateXAux > 2) angleRotateX += inc

            if (angleRotateY - angleRotateYAux < -2) angleRotateY -= inc
            if (angleRotateY - angleRotateYAux > 2) angleRotateY += inc

            inc -= 0.1.toFloat()

            if (inc < 0.5) {
                MagicCubeActivity.isActivated = false
                android.util.Log.d("teste", "zerou")
                inc = 5f
            }
        }
        gl.rotate(angleRotateX, 0f, 1f, 0f)
        gl.rotate(angleRotateY, 1f, 0f, 0f)

        if (sinal < 0) sinal = -sinal
        cubeMatrixIndex = 0
        n = 0
        if (MagicCubeActivity.isActivated) Log.d("ZDist", " ")
        while (n < 3) {
            if (rot == 0 && n == 0) {
                gl.translate(-xdist, -ydist, -zdist)
                gl.rotate(rotatedAngle, 0f, 1f, 0f)
                gl.translate(xdist, ydist, zdist)
                e = 0
            }
            if (rot == 6 && n == 1) {
                gl.translate(-xdist, -ydist, -zdist)
                gl.rotate(rotatedAngle, 0f, 1f, 0f)
                gl.translate(xdist, ydist, zdist)
                e = 1
            }
            if (rot == 1 && n == 2) {
                gl.translate(-xdist, -ydist, -zdist)
                gl.rotate(rotatedAngle, 0f, 1f, 0f)
                gl.translate(xdist, ydist, zdist)
                e = 2
            }
            gl.translate(0.0f, sinal * dist, 0.0f)
            ydist += sinal * dist
            if (n > 0) {
                gl.translate(-dist, 0.0f, -dist)
                xdist += -dist
                zdist += -dist
            }
            gl.translate(0.0f, 0.0f, -2 * dist)
            gl.translate(dist, 0.0f, 0.0f)
            zdist += -2 * dist
            xdist += dist
            i = 0
            while (i < 3) {
                if (rot == 2 && i == 0) {
                    gl.translate(-xdist, -ydist, -zdist)
                    gl.rotate(rotatedAngle, 0f, 0f, 1f)
                    gl.translate(xdist, ydist, zdist)
                    e = 0
                }
                if (rot == 7 && i == 1) {
                    gl.translate(-xdist, -ydist, -zdist)
                    gl.rotate(rotatedAngle, 0f, 0f, 1f)
                    gl.translate(xdist, ydist, zdist)
                    e = 1
                }
                if (rot == 3 && i == 2) {
                    gl.translate(-xdist, -ydist, -zdist)
                    gl.rotate(rotatedAngle, 0f, 0f, 1f)
                    gl.translate(xdist, ydist, zdist)
                    e = 2
                }
                gl.translate(0.0f, 0.0f, dist)
                zdist += dist
                gl.translate(-3 * dist, 0.0f, 0.0f)
                xdist += -3 * dist

                j = 0
                while (j < 3) {
                    if (rot == 4 && j == 0) {
                        gl.translate(-xdist, -ydist, -zdist)
                        gl.rotate(rotatedAngle, 1f, 0f, 0f)
                        gl.translate(xdist, ydist, zdist)
                        e = 0
                    }
                    if (rot == 8 && j == 1) {
                        gl.translate(-xdist, -ydist, -zdist)
                        gl.rotate(rotatedAngle, 1f, 0f, 0f)
                        gl.translate(xdist, ydist, zdist)
                        e = 1
                    }

                    if (rot == 5 && j == 2) {
                        gl.translate(-xdist, -ydist, -zdist)
                        gl.rotate(rotatedAngle, 1f, 0f, 0f)
                        gl.translate(xdist, ydist, zdist)
                        e = 2
                    }
                    gl.translate(dist, 0.0f, 0.0f)
                    xdist += dist
                    cubeMatrixIndex = pos[j][n][i]
                    //
                    cubeList[cubeMatrixIndex].draw(gl) //aqui que desenha cada cubinho

                    if (MagicCubeActivity.isActivated) {
                        cubeSideIndex.forEachIndexed { index, cubeIndex ->
                            if (cubeMatrixIndex == cubeIndex.first) {
                                val zDistance = -matrixTracker.getZ()  // Negativo → quanto mais longe da tela
                                cubeSide[index] = zDistance
                                Log.d("ZDist", "Cube $cubeMatrixIndex color ${cubeIndex.second.name} Z = $zDistance")
                            }
                        }
                    }
                    //
                    if (rot == 5 && j == 2) {
                        gl.translate(-xdist, -ydist, -zdist)
                        gl.rotate(-rotatedAngle, 1f, 0f, 0f)
                        gl.translate(xdist, ydist, zdist)
                    }
                    if (rot == 8 && j == 1) {
                        gl.translate(-xdist, -ydist, -zdist)
                        gl.rotate(-rotatedAngle, 1f, 0f, 0f)
                        gl.translate(xdist, ydist, zdist)
                        e = 1
                    }
                    if (rot == 4 && j == 0) {
                        gl.translate(-xdist, -ydist, -zdist)
                        gl.rotate(-rotatedAngle, 1f, 0f, 0f)
                        gl.translate(xdist, ydist, zdist)
                    }
                    j++
                }
                if (rot == 3 && i == 2) {
                    gl.translate(-xdist, -ydist, -zdist)
                    gl.rotate(-rotatedAngle, 0f, 0f, 1f)
                    gl.translate(xdist, ydist, zdist)
                }
                if (rot == 7 && i == 1) {
                    gl.translate(-xdist, -ydist, -zdist)
                    gl.rotate(-rotatedAngle, 0f, 0f, 1f)
                    gl.translate(xdist, ydist, zdist)
                    e = 1
                }
                if (rot == 2 && i == 0) {
                    gl.translate(-xdist, -ydist, -zdist)
                    gl.rotate(-rotatedAngle, 0f, 0f, 1f)
                    gl.translate(xdist, ydist, zdist)
                }

                i++
            }
            if (n == 0) sinal = -sinal
            if (rot == 1 && n == 2) {
                gl.translate(-xdist, -ydist, -zdist)
                gl.rotate(-rotatedAngle, 0f, 1f, 0f)
                gl.translate(xdist, ydist, zdist)
            }
            if (rot == 6 && n == 1) {
                gl.translate(-xdist, -ydist, -zdist)
                gl.rotate(-rotatedAngle, 0f, 1f, 0f)
                gl.translate(xdist, ydist, zdist)
                e = 1
            }
            if (rot == 0 && n == 0) {
                gl.translate(-xdist, -ydist, -zdist)
                gl.rotate(-rotatedAngle, 0f, 1f, 0f)
                gl.translate(xdist, ydist, zdist)
            }
            n++
        }



        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY) //Habilitar na renderiza��o
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY) //Habilitar na renderiza��o

        soma = 9.0.toFloat()

        if (rotatedAngle == 90f || rotatedAngle == -90f) {
            rotatedAngle = 0f
            save()
            if (!embaralhando) {
                rotating = false
                rot = 20
            } else {
                rot = ((Math.random() * 10000) % 12).toInt() - 6
                if (rot < 0) {
                    rot++
                    rot = -rot
                    sense = -1
                } else {
                    sense = 1
                }
                cont++
                if (cont == num_embaralhar) {
                    embaralhando = false
                    rotating = false
                    rot = 20
                    //cont=0;
                }
            }
        }

        if (rotating) {
            if (rotatedAngle >= 0) rotatedAngle += soma
            else rotatedAngle -= soma
        }
    }

    private fun GL10.translate(p0: Float, p1: Float, p2: Float) {
        this.glTranslatef(p0, p1, p2)
        matrixTracker.translate(p0, p1, p2)
    }

    private fun GL10.rotate(p0: Float, p1: Float, p2: Float, p3: Float) {
        this.glRotatef(p0, p1, p2, p3)
        matrixTracker.rotate(p0, p1, p2, p3)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        gl.glViewport(0, 0, width, height)
        val zNear = .1f
        val zFar = 1000f
        fielOfView = 80.0f / 57.3f

        gl.glEnable(GL10.GL_NORMALIZE)

        val aspectRatio = width.toFloat() / height

        gl.glMatrixMode(GL10.GL_PROJECTION)

        val size = zNear * (kotlin.math.tan((fielOfView / 2.0f).toDouble())).toFloat()

        gl.glFrustumf(
            -size, size, -size / aspectRatio,  //VIEWPORT
            size / aspectRatio, zNear, zFar
        )

        gl.glMatrixMode(GL10.GL_MODELVIEW)
    }

    override fun onSurfaceCreated(gl: GL10, config: javax.microedition.khronos.egl.EGLConfig) {
        gl.glDisable(GL10.GL_DITHER)

        //gl.glEnable(GL10.GL_DITHER);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST)


        if (mTranslucentBackground) {
            gl.glClearColor(0f, 0f, 0f, 0f)
        } else {
            gl.glClearColor(1f, 1f, 1f, 1f)
        }

        gl.glCullFace(GL10.GL_BACK)
        gl.glEnable(GL10.GL_POINT_SMOOTH)
        gl.glEnable(GL10.GL_CULL_FACE)
        //gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glShadeModel(GL10.GL_FLAT) //Mostra apenas a cor do �ltimo vertice especificado.
        gl.glEnable(GL10.GL_DEPTH_TEST)

        gl.glClearDepthf(1f)
        gl.glDepthFunc(GL10.GL_LEQUAL)
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST)


        //initLighting(gl);
    } //	private void initLighting(GL10 gl){
    //		float[] diffuse = {1.0f, 1.0f, 1.0f, 1.0f}; //1
    //		float[] pos = {0.0f, 10.0f, -3.0f, 1.0f}; //2
    //		gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, Cube.makeFloatBuffer(pos)); //3
    //		gl.glLightfv(SS_SUNLIGHT, GL10.GL_DIFFUSE, Cube.makeFloatBuffer(diffuse)); //4
    //		gl.glShadeModel(GL10.GL_SMOOTH); //5
    //		gl.glEnable(GL10.GL_LIGHTING); //6
    //		gl.glEnable(SS_SUNLIGHT); //7
    //	}

    companion object {
        var rot: Int = 20
        var sense: Int = -1
        var rotating: Boolean = true
    }
}
