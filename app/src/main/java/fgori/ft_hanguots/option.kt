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
import android.widget.RadioGroup
import android.widget.RadioButton


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

        header.setButton(buttonHome)

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

        val languageRadioGroup = findViewById<RadioGroup>(R.id.languageRadioGroup)
        val radioItalian = findViewById<RadioButton>(R.id.radioItalian)
        val radioEnglish = findViewById<RadioButton>(R.id.radioEnglish)
        val radioSystem = findViewById<RadioButton>(R.id.radioSystem)
        header.setRadioButtonStyle(radioItalian)
        header.setRadioButtonStyle(radioEnglish)
        header.setRadioButtonStyle(radioSystem)
        val editor = sharedPrefs.edit()

        val currentLanguage = sharedPrefs.getString("app_language", "auto") ?: "auto"
        when (currentLanguage) {
            "it" -> languageRadioGroup.check(R.id.radioItalian)
            "en" -> languageRadioGroup.check(R.id.radioEnglish)
            "auto" -> languageRadioGroup.check(R.id.radioSystem)
        }



        languageRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val languageToSave = when (checkedId) {
                R.id.radioItalian -> "it"
                R.id.radioEnglish -> "en"
                R.id.radioSystem -> "auto"
                else -> "auto"
            }

            if (languageToSave != sharedPrefs.getString("app_language", "auto")) {
                editor.putString("app_language", languageToSave).apply()
                recreate()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        isInChild = false
    }
}