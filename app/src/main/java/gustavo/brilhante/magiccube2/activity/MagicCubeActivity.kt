package gustavo.brilhante.magiccube2.activity

import android.content.res.Resources
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import gustavo.brilhante.magiccube2.grafic.CubeRenderer
import kotlin.math.abs

class MagicCubeActivity : AppCompatActivity() {
    var mRenderer: CubeRenderer? = null
    private var mPreviousX = 0f
    private var mPreviousY = 0f
    private val TOUCH_SCALE_FACTOR = OptionsActivity.speed * ((180.0f / 320) / 5)
    var view: GLSurfaceView? = null
    var displayWidth: Int = 0
    var displayHeight: Int = 0
    var actionBarHeight: Int = 0
    var buttonSize: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        view = GLSurfaceView(this)

        mRenderer = CubeRenderer(true)

        view!!.setRenderer(mRenderer)

        setContentView(view)

        val display = windowManager.defaultDisplay
        displayWidth = display.width
        displayHeight = display.height
        buttonSize = displayWidth / 6

        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }


    }

    private var startX = 0f
    private var startY = 0f
    private var startTime = 0L
    private var horizontalOrientation = 1
    private var verticalOrientation = 1

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x = e.x
        val y = e.y

        if (e.action == MotionEvent.ACTION_DOWN) {
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels

            val top = y < screenHeight / 2
            val right = x > screenWidth / 2

            verticalOrientation = if (right) 1 else -1
            horizontalOrientation = if (top) -1 else 1

            //Log.d("DOWN","TESTE");
            startX = x
            startY = y
            startTime = System.currentTimeMillis()
        }
        if (e.action == MotionEvent.ACTION_UP) {
            val endX = x
            val endY = y
            val endTime = System.currentTimeMillis()

            val dx = endX - startX
            val dy = endY - startY
            val dt = endTime - startTime

            isActivated = true

            Log.d("XDist", "Finger $endX X")
            Log.d("YDist", "Finger $endY Y")

            when(getMovementType(dt, dx, dy)){
                MovementType.SWIPE_UP -> startRotationOfClosestSide(-1 * verticalOrientation)
                MovementType.SWIPE_DOWN -> startRotationOfClosestSide(1 * verticalOrientation)
                MovementType.SWIPE_LEFT -> startRotationOfClosestSide(1 * horizontalOrientation)
                MovementType.SWIPE_RIGHT -> startRotationOfClosestSide(-1 * horizontalOrientation)
                MovementType.DRAG -> {}
                MovementType.NONE -> {}
            }
        }
        if (e.action == MotionEvent.ACTION_POINTER_UP) {
            zoom = false
        }
        if (e.action == MotionEvent.ACTION_POINTER_DOWN) {
            zoom = true
        }
        if (e.action == MotionEvent.ACTION_MOVE) {
            val endX = x
            val endY = y
            val endTime = System.currentTimeMillis()

            val movementType = getMovementType(endTime - startTime, endX - startX, endY - startY)

            if (movementType == MovementType.DRAG) {
                val dx = x - mPreviousX
                val dy = y - mPreviousY

                mRenderer!!.angleRotateXAux = mRenderer!!.angleRotateX
                mRenderer!!.angleRotateYAux = mRenderer!!.angleRotateY
                mRenderer!!.angleRotateX += dx * TOUCH_SCALE_FACTOR
                mRenderer!!.angleRotateY += dy * TOUCH_SCALE_FACTOR
            }
        }

        mPreviousX = x
        mPreviousY = y

        return true
    }

    private fun startRotationOfClosestSide(rotationSense: Int) {
        if (!CubeRenderer.rotating) {
            CubeRenderer.rotating = true
            mRenderer?.rotateClosestSideToScreen(rotationSense)
        }
    }

    private fun getMovementType(
        distanceTime: Long,
        distanceX: Float,
        distanceY: Float,
        timeThreshold: Int = 250,
        distanceThreshold: Int = 100
    ): MovementType {
        // thresholds para considerar swipe
        val distanceThreshold = 100      // px
        val timeThreshold = 250          // ms

        return if (distanceTime < timeThreshold) {
            if (abs(distanceX) > abs(distanceY) && abs(distanceX) > distanceThreshold) {
                if (distanceX > 0) {
                    Log.d("Swipe", "→ Swipe para a direita")
                    MovementType.SWIPE_RIGHT
                    // ação personalizada
                } else {
                    Log.d("Swipe", "← Swipe para a esquerda")
                    MovementType.SWIPE_LEFT
                }
            } else if (abs(distanceY) > distanceThreshold) {
                if (distanceY > 0) {
                    Log.d("Swipe", "↓ Swipe para baixo")
                    MovementType.SWIPE_DOWN
                } else {
                    Log.d("Swipe", "↑ Swipe para cima")
                    MovementType.SWIPE_UP
                }
            } else {
                Log.d("Swipe", "None movement - distance")
                MovementType.NONE
            }
        } else {
            Log.d("Swipe", "Drag - time")
            MovementType.DRAG
        }
    }

    companion object {
        @JvmField
        var isActivated: Boolean = false
        var zoom: Boolean = false
        var zoomOut: Boolean = false
    }
}

internal enum class MovementType() {
    SWIPE_UP, SWIPE_DOWN, SWIPE_LEFT, SWIPE_RIGHT, DRAG, NONE
}

