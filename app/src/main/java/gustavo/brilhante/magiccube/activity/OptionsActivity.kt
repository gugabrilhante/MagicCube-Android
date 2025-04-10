package gustavo.brilhante.magiccube.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import gustavo.brilhante.magiccube.R

class OptionsActivity : AppCompatActivity() {
    var shuffleTextView: TextView? = null
    var speedTextView: TextView? = null
    var sizeTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opcoes)
        shuffleTextView = findViewById<View>(R.id.embaralhar_tv) as TextView
        speedTextView = findViewById<View>(R.id.velocidade_tv) as TextView
        sizeTextView = findViewById<View>(R.id.tamanho_tv) as TextView
    }

    fun increaseShuffle(v: View?) {
        if (shuffle < 10) {
            shuffle++
        }
        shuffleTextView!!.text = (10 * shuffle).toString()
    }

    fun decreaseShuffle(v: View?) {
        if (shuffle > 1) {
            shuffle--
        }
        shuffleTextView!!.text = (10 * shuffle).toString()
    }

    fun increaseSpeed(v: View?) {
        if (speed < 10) {
            speed++
        }
        speedTextView!!.text = speed.toString()
    }

    fun decreaseSpeed(v: View?) {
        if (speed > 1) {
            speed--
        }
        speedTextView!!.text = speed.toString()
    }

    fun increaseSize(v: View?) {
        if (size < 10) {
            size++
        }
        sizeTextView!!.text = size.toString()
    }

    fun decreaseSize(v: View?) {
        if (size > 1) {
            size--
        }
        sizeTextView!!.text = size.toString()
    }

    companion object {
        @JvmField
        var shuffle: Int = 5
        var speed: Int = 5
        var size: Int = 9
    }
}

