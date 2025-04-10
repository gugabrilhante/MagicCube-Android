package gustavo.brilhante.magiccube.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import gustavo.brilhante.magiccube.R;

public class OpcoesActivity extends AppCompatActivity {

    public static int embaralhar=5, velocidade=5, tamanho=9;
    TextView embaralhar_tv;
    TextView velocidade_tv;
    TextView tamanho_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcoes);
        embaralhar_tv = (TextView) findViewById(R.id.embaralhar_tv);
        velocidade_tv = (TextView) findViewById(R.id.velocidade_tv);
        tamanho_tv = (TextView) findViewById(R.id.tamanho_tv);
    }

    public void embaralhar_mais(View v){
        if (OpcoesActivity.embaralhar<10){
            OpcoesActivity.embaralhar++;
        }
        embaralhar_tv.setText(Integer.toString(10*embaralhar));
    }

    public void embaralhar_menos(View v){
        if (OpcoesActivity.embaralhar>1){
            OpcoesActivity.embaralhar--;
        }
        embaralhar_tv.setText(Integer.toString(10*embaralhar));
    }

    public void velocidade_mais(View v){
        if (OpcoesActivity.velocidade<10){
            OpcoesActivity.velocidade++;
        }
        velocidade_tv.setText(Integer.toString(velocidade));
    }

    public void velocidade_menos(View v){
        if (OpcoesActivity.velocidade>1){
            OpcoesActivity.velocidade--;
        }
        velocidade_tv.setText(Integer.toString(velocidade));
    }

    public void tamanho_mais(View v){
        if (OpcoesActivity.tamanho<10){
            OpcoesActivity.tamanho++;
        }
        tamanho_tv.setText(Integer.toString(tamanho));
    }

    public void tamanho_menos(View v){
        if (OpcoesActivity.tamanho>1){
            OpcoesActivity.tamanho--;
        }
        tamanho_tv.setText(Integer.toString(tamanho));
    }

}

