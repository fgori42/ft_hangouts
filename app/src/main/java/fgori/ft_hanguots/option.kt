package fgori.ft_hanguots

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.SeekBar
import android.widget.Button
import fgori.ft_hanguots.Header
import fgori.ft_hanguots.R
import android.content.Intent




class option : BaseActivity() {
    private fun retSeeker(redBar: SeekBar, greenBar: SeekBar, blueBar: SeekBar) {
        var color = header.getHeaderColor()
        color = color.removePrefix("#")
        val red = color.substring(0, 2).toInt(16)
        val blue = color.substring(5, 6).toInt(16)
        val green = color.substring(2, 4).toInt(16)
        redBar.progress = red
        blueBar.progress = blue
        greenBar.progress = green
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_option)
        header.notifyActivityChanged("OptionActivity")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val redBar = findViewById<SeekBar>(R.id.seekBarRed)
        val greenBar = findViewById<SeekBar>(R.id.seekBarGreen)
        val blueBar = findViewById<SeekBar>(R.id.seekBarBlu)
        val buttonHome = findViewById<Button>(R.id.buttonHome)

        buttonHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        retSeeker(redBar, greenBar, blueBar)

        val colorChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val red = redBar.progress
                val green = greenBar.progress
                val blue = blueBar.progress
                header.changeColor(String.format("#%02X%02X%02X", red, green, blue))
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
        redBar.setOnSeekBarChangeListener(colorChangeListener)
        greenBar.setOnSeekBarChangeListener(colorChangeListener)
        blueBar.setOnSeekBarChangeListener(colorChangeListener)

        val switchButton = findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.switch2)
        val editor = sharedPrefs.edit()
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editor.putString("USER_LANGUAGE", "en").apply()
            } else {
                editor.putString("USER_LANGUAGE", "default").apply()
            }

        }
    }

    override fun onPause() {
        super.onPause()
        header.onPause()
    }

    override fun onResume() {
        super.onResume()
        header.onResume()
    }
}