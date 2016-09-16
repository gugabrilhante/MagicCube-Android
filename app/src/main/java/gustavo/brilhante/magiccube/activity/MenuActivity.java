package gustavo.brilhante.magiccube.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import gustavo.brilhante.magiccube.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Iniciar(View v){
        Intent i = new Intent(this, MagicCubeActivity.class);
        startActivity(i);
    }
    public void Opcoes(View v){
        Intent i = new Intent(this, OpcoesActivity.class);
        startActivity(i);
    }

    public void Sair(View v){
        finish();
    }
}
