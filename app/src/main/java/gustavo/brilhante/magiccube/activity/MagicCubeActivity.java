package gustavo.brilhante.magiccube.activity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import gustavo.brilhante.magiccube.R;
import gustavo.brilhante.magiccube.grafic.CubeRenderer;

public class MagicCubeActivity extends AppCompatActivity {

    CubeRenderer mRenderer;
    private float mPreviousX = 0;
    private float mPreviousY = 0;
    private final float TOUCH_SCALE_FACTOR = OpcoesActivity.velocidade*((180.0f / 320)/5);
    GLSurfaceView view ;
    public static Boolean ativar = false;
    public static Boolean zoom = false;
    public static Boolean zoomOut = false;

    int displayWidth, displayHeight,actionBarHeight, buttonSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        view = new GLSurfaceView(this);

        mRenderer = new CubeRenderer(true);

        view.setRenderer(mRenderer);

        setContentView(view);

        Display display = getWindowManager().getDefaultDisplay();
        displayWidth = display.getWidth();
        displayHeight = display.getHeight();
        buttonSize = displayWidth/6;

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        //criaÁ„o de botoes.

        LinearLayout ll2 = new LinearLayout(this);
        ll2.setOrientation(LinearLayout.HORIZONTAL);
        ll2.setTranslationX(0);

        Button b_yellow = new Button(this);
//      b_yellow.setText("yellow");
        b_yellow.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_yellow3));
        b_yellow.setLayoutParams(new ViewGroup.LayoutParams(buttonSize, buttonSize));

        b_yellow.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                if(CubeRenderer.rotating == false){
                    CubeRenderer.rotating = true;
                    CubeRenderer.rot = 0;
                    CubeRenderer.sense = -1;
                }
            }
        });

        ll2.addView(b_yellow);

        Button b_red = new Button(this);
        //b_red.setText("red");
        b_red.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_red3));
        b_red.setLayoutParams(new ViewGroup.LayoutParams(buttonSize, buttonSize));

        b_red.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                if(CubeRenderer.rotating == false){
                    CubeRenderer.rotating = true;
                    CubeRenderer.rot = 5;
                    CubeRenderer.sense = -1;
                }
            }
        });

        ll2.addView(b_red);

        Button b_blue = new Button(this);
        //b_blue.setText("blue");
        b_blue.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blue3));
        b_blue.setLayoutParams(new ViewGroup.LayoutParams(buttonSize, buttonSize));

        b_blue.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                if(CubeRenderer.rotating == false){
                    CubeRenderer.rotating = true;
                    CubeRenderer.rot = 3;
                    CubeRenderer.sense = -1;
                }
            }
        });

        ll2.addView(b_blue);

        Button b_green = new Button(this);
        // b_green.setText("green");
        b_green.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_green3));
        b_green.setLayoutParams(new ViewGroup.LayoutParams(buttonSize, buttonSize));

        b_green.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                if(CubeRenderer.rotating == false){
                    CubeRenderer.rotating = true;
                    CubeRenderer.rot = 2;
                    CubeRenderer.sense = -1;
                }
            }
        });

        ll2.addView(b_green);

        Button b_orange = new Button(this);
        //b_orange.setText("orange");
        b_orange.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_orange3));
        b_orange.setLayoutParams(new ViewGroup.LayoutParams(buttonSize, buttonSize));

        b_orange.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                if(CubeRenderer.rotating == false){
                    CubeRenderer.rotating = true;
                    CubeRenderer.rot = 4;
                    CubeRenderer.sense = -1;
                }
            }
        });

        ll2.addView(b_orange);

        Button b_white = new Button(this);
        //b_white.setText("white");
        b_white.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_white3));
        b_white.setLayoutParams(new ViewGroup.LayoutParams(buttonSize, buttonSize));

        b_white.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                if(CubeRenderer.rotating == false){
                    CubeRenderer.rotating = true;
                    CubeRenderer.rot = 1;
                    CubeRenderer.sense = -1;
                }
            }
        });

        ll2.addView(b_white);

        this.addContentView(ll2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setTranslationY(displayHeight - actionBarHeight - buttonSize);
        ll.setTranslationX(0);

        Button b2_yellow = new Button(this);
        // b2_yellow.setText("yellow");
        b2_yellow.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_yellow3));
        b2_yellow.setLayoutParams(new ViewGroup.LayoutParams(buttonSize, buttonSize));

        b2_yellow.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                if(CubeRenderer.rotating == false){
                    CubeRenderer.rotating = true;
                    CubeRenderer.rot = 0;
                    CubeRenderer.sense = 1;
                }
            }
        });

        ll.addView(b2_yellow);

        Button b2_red = new Button(this);
        //b2_red.setText("red");
        b2_red.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_red3));
        b2_red.setLayoutParams(new ViewGroup.LayoutParams(buttonSize, buttonSize));

        b2_red.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                if(CubeRenderer.rotating == false){
                    CubeRenderer.rotating = true;
                    CubeRenderer.rot = 5;
                    CubeRenderer.sense = 1;
                }
            }
        });

        ll.addView(b2_red);

        Button b2_blue = new Button(this);
        //b2_blue.setText("blue");
        b2_blue.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blue3));
        b2_blue.setLayoutParams(new ViewGroup.LayoutParams(buttonSize, buttonSize));

        b2_blue.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                if(CubeRenderer.rotating == false){
                    CubeRenderer.rotating = true;
                    CubeRenderer.rot = 3;
                    CubeRenderer.sense = 1;
                }
            }
        });

        ll.addView(b2_blue);

        Button b2_green = new Button(this);
        //b2_green.setText("green");
        b2_green.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_green3));
        b2_green.setLayoutParams(new ViewGroup.LayoutParams(buttonSize, buttonSize));

        b2_green.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                if(CubeRenderer.rotating == false){
                    CubeRenderer.rotating = true;
                    CubeRenderer.rot = 2;
                    CubeRenderer.sense = 1;
                }
            }
        });

        ll.addView(b2_green);

        Button b2_orange = new Button(this);
        //b2_orange.setText("orange");
        b2_orange.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_orange3));
        b2_orange.setLayoutParams(new ViewGroup.LayoutParams(buttonSize, buttonSize));

        b2_orange.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                if(CubeRenderer.rotating == false){
                    CubeRenderer.rotating = true;
                    CubeRenderer.rot = 4;
                    CubeRenderer.sense = 1;
                }
            }
        });

        ll.addView(b2_orange);

        Button b2_white = new Button(this);
        //b2_white.setText("white");
        b2_white.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_white3));
        b2_white.setLayoutParams(new ViewGroup.LayoutParams(buttonSize, buttonSize));

        b2_white.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                if(CubeRenderer.rotating == false){
                    CubeRenderer.rotating = true;
                    CubeRenderer.rot = 1;
                    CubeRenderer.sense = 1;
                }
            }
        });

        ll.addView(b2_white);


        this.addContentView(ll, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        if(e.getAction() == MotionEvent.ACTION_DOWN){
            //Log.d("DOWN","TESTE");

        }
        if(e.getAction() == MotionEvent.ACTION_UP){
            //Log.d("UP","TESTE");
            ativar = true;
        }
        if(e.getAction()==MotionEvent.ACTION_POINTER_UP){
            zoom = false;
        }
        if(e.getAction() == MotionEvent.ACTION_POINTER_DOWN){
            zoom = true;
        }
        if(e.getAction() == MotionEvent.ACTION_MOVE){
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                mRenderer.angleTestAux = mRenderer.angleTest;
                mRenderer.angleTest2Aux = mRenderer.angleTest2;
                mRenderer.angleTest += dx * TOUCH_SCALE_FACTOR;
                mRenderer.angleTest2 += dy * TOUCH_SCALE_FACTOR;

        }

        mPreviousX = x;
        mPreviousY = y;

        return true;

    }

}
