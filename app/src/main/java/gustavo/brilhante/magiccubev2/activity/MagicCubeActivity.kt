package gustavo.brilhante.magiccubev2.activity

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import gustavo.brilhante.magiccubev2.R
import gustavo.brilhante.magiccubev2.grafic.CubeRenderer

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

        //criaÁ„o de botoes.
        val ll2 = LinearLayout(this)
        ll2.orientation = LinearLayout.HORIZONTAL
        ll2.translationY = (actionBarHeight - buttonSize/3).toFloat()
        ll2.translationX = 0f

        val b_yellow = Button(this)
        //      b_yellow.setText("yellow");
        b_yellow.setBackgroundDrawable(resources.getDrawable(R.drawable.btn_yellow3))
        b_yellow.layoutParams = ViewGroup.LayoutParams(buttonSize, buttonSize)

        b_yellow.setOnClickListener {
            if (CubeRenderer.rotating == false) {
                CubeRenderer.rotating = true
                CubeRenderer.rot = 0
                CubeRenderer.sense = -1
            }
        }

        ll2.addView(b_yellow)

        val b_red = Button(this)
        //b_red.setText("red");
        b_red.setBackgroundDrawable(resources.getDrawable(R.drawable.btn_red3))
        b_red.layoutParams = ViewGroup.LayoutParams(buttonSize, buttonSize)

        b_red.setOnClickListener {
            if (CubeRenderer.rotating == false) {
                CubeRenderer.rotating = true
                CubeRenderer.rot = 5
                CubeRenderer.sense = -1
            }
        }

        ll2.addView(b_red)

        val b_blue = Button(this)
        //b_blue.setText("blue");
        b_blue.setBackgroundDrawable(resources.getDrawable(R.drawable.btn_blue3))
        b_blue.layoutParams = ViewGroup.LayoutParams(buttonSize, buttonSize)

        b_blue.setOnClickListener {
            if (CubeRenderer.rotating == false) {
                CubeRenderer.rotating = true
                CubeRenderer.rot = 3
                CubeRenderer.sense = -1
            }
        }

        ll2.addView(b_blue)

        val b_green = Button(this)
        // b_green.setText("green");
        b_green.setBackgroundDrawable(resources.getDrawable(R.drawable.btn_green3))
        b_green.layoutParams = ViewGroup.LayoutParams(buttonSize, buttonSize)

        b_green.setOnClickListener {
            if (CubeRenderer.rotating == false) {
                CubeRenderer.rotating = true
                CubeRenderer.rot = 2
                CubeRenderer.sense = -1
            }
        }

        ll2.addView(b_green)

        val b_orange = Button(this)
        //b_orange.setText("orange");
        b_orange.setBackgroundDrawable(resources.getDrawable(R.drawable.btn_orange3))
        b_orange.layoutParams = ViewGroup.LayoutParams(buttonSize, buttonSize)

        b_orange.setOnClickListener {
            if (CubeRenderer.rotating == false) {
                CubeRenderer.rotating = true
                CubeRenderer.rot = 4
                CubeRenderer.sense = -1
            }
        }

        ll2.addView(b_orange)

        val b_white = Button(this)
        //b_white.setText("white");
        b_white.setBackgroundDrawable(resources.getDrawable(R.drawable.btn_white3))
        b_white.layoutParams = ViewGroup.LayoutParams(buttonSize, buttonSize)

        b_white.setOnClickListener {
            if (CubeRenderer.rotating == false) {
                CubeRenderer.rotating = true
                CubeRenderer.rot = 1
                CubeRenderer.sense = -1
            }
        }

        ll2.addView(b_white)

        this.addContentView(ll2, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        val ll = LinearLayout(this)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.translationY = (displayHeight - actionBarHeight - 2 * buttonSize).toFloat()
        ll.translationX = 0f

        val b2_yellow = Button(this)
        // b2_yellow.setText("yellow");
        b2_yellow.setBackgroundDrawable(resources.getDrawable(R.drawable.btn_yellow3))
        b2_yellow.layoutParams = ViewGroup.LayoutParams(buttonSize, buttonSize)

        b2_yellow.setOnClickListener {
            if (CubeRenderer.rotating == false) {
                CubeRenderer.rotating = true
                CubeRenderer.rot = 0
                CubeRenderer.sense = 1
            }
        }

        ll.addView(b2_yellow)

        val b2_red = Button(this)
        //b2_red.setText("red");
        b2_red.setBackgroundDrawable(resources.getDrawable(R.drawable.btn_red3))
        b2_red.layoutParams = ViewGroup.LayoutParams(buttonSize, buttonSize)

        b2_red.setOnClickListener {
            if (CubeRenderer.rotating == false) {
                CubeRenderer.rotating = true
                CubeRenderer.rot = 5
                CubeRenderer.sense = 1
            }
        }

        ll.addView(b2_red)

        val b2_blue = Button(this)
        //b2_blue.setText("blue");
        b2_blue.setBackgroundDrawable(resources.getDrawable(R.drawable.btn_blue3))
        b2_blue.layoutParams = ViewGroup.LayoutParams(buttonSize, buttonSize)

        b2_blue.setOnClickListener {
            if (CubeRenderer.rotating == false) {
                CubeRenderer.rotating = true
                CubeRenderer.rot = 3
                CubeRenderer.sense = 1
            }
        }

        ll.addView(b2_blue)

        val b2_green = Button(this)
        //b2_green.setText("green");
        b2_green.setBackgroundDrawable(resources.getDrawable(R.drawable.btn_green3))
        b2_green.layoutParams = ViewGroup.LayoutParams(buttonSize, buttonSize)

        b2_green.setOnClickListener {
            if (CubeRenderer.rotating == false) {
                CubeRenderer.rotating = true
                CubeRenderer.rot = 2
                CubeRenderer.sense = 1
            }
        }

        ll.addView(b2_green)

        val b2_orange = Button(this)
        //b2_orange.setText("orange");
        b2_orange.setBackgroundDrawable(resources.getDrawable(R.drawable.btn_orange3))
        b2_orange.layoutParams = ViewGroup.LayoutParams(buttonSize, buttonSize)

        b2_orange.setOnClickListener {
            if (CubeRenderer.rotating == false) {
                CubeRenderer.rotating = true
                CubeRenderer.rot = 4
                CubeRenderer.sense = 1
            }
        }

        ll.addView(b2_orange)

        val b2_white = Button(this)
        //b2_white.setText("white");
        b2_white.setBackgroundDrawable(resources.getDrawable(R.drawable.btn_white3))
        b2_white.layoutParams = ViewGroup.LayoutParams(buttonSize, buttonSize)

        b2_white.setOnClickListener {
            if (CubeRenderer.rotating == false) {
                CubeRenderer.rotating = true
                CubeRenderer.rot = 1
                CubeRenderer.sense = 1
            }
        }

        ll.addView(b2_white)

        this.addContentView(ll, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x = e.x
        val y = e.y

        if (e.action == MotionEvent.ACTION_DOWN) {
            //Log.d("DOWN","TESTE");
        }
        if (e.action == MotionEvent.ACTION_UP) {
            //Log.d("UP","TESTE");
            isActivated = true
        }
        if (e.action == MotionEvent.ACTION_POINTER_UP) {
            zoom = false
        }
        if (e.action == MotionEvent.ACTION_POINTER_DOWN) {
            zoom = true
        }
        if (e.action == MotionEvent.ACTION_MOVE) {
            val dx = x - mPreviousX
            val dy = y - mPreviousY

            mRenderer!!.angleTestAux = mRenderer!!.angleTest
            mRenderer!!.angleTest2Aux = mRenderer!!.angleTest2
            mRenderer!!.angleTest += dx * TOUCH_SCALE_FACTOR
            mRenderer!!.angleTest2 += dy * TOUCH_SCALE_FACTOR
        }

        mPreviousX = x
        mPreviousY = y

        return true
    }

    companion object {
        @JvmField
        var isActivated: Boolean = false
        var zoom: Boolean = false
        var zoomOut: Boolean = false
    }
}

