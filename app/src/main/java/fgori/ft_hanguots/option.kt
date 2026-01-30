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
import android.graphics.Color
import android.widget.ImageButton


class option : BaseActivity() {
    private fun retSeeker(redBar: SeekBar, greenBar: SeekBar, blueBar: SeekBar) {
        val colorString = header.getHeaderColor()
        val colorInt = try {
            Color.parseColor(colorString)
        } catch (e: IllegalArgumentException) {
            Color.parseColor("#2196F3")
        }
        redBar.progress = Color.red(colorInt)
        greenBar.progress = Color.green(colorInt)
        blueBar.progress = Color.blue(colorInt)
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
        val buttonHome = findViewById<ImageButton>(R.id.buttonHome)
        val button5 = findViewById<Button>(R.id.systemLanguageButton)


        header.setButton(buttonHome)
        header.setButton(button5)


        buttonHome.setOnClickListener {
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
        val currentLang = sharedPrefs.getString("USER_LANGUAGE", "default") ?: "default"
        val editor = sharedPrefs.edit()
        if (sharedPrefs.getString("USER_LANGUAGE", "default") == "en")
            switchButton.isChecked = true
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editor.putString("USER_LANGUAGE", "en").apply()
            } else {
                editor.putString("USER_LANGUAGE", "default").apply()
            }
            recreate()
        }
        button5.setOnClickListener {
            editor.putString("USER_LANGUAGE", "auto").apply()
            recreate()
        }
    }


    override fun onResume() {
        super.onResume()
        isInChild = false
    }
}