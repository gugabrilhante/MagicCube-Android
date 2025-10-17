package gustavo.brilhante.magiccube2.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import gustavo.brilhante.magiccube2.R

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun start(v: View?) {
        val i = Intent(this, MagicCubeActivity::class.java)
        startActivity(i)
    }

    fun options(v: View?) {
        val i = Intent(this, OptionsActivity::class.java)
        startActivity(i)
    }

    fun exit(v: View?) {
        finish()
    }
}
