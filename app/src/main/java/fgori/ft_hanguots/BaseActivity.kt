package fgori.ft_hanguots

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.content.res.Configuration
import java.util.Locale

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var header: Header
    protected lateinit var dbHelper: DatabaseHelper
    protected lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        sharedPrefs = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        applyLanguage()
    }
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        header = findViewById(R.id.header)
    }
    override fun onPause() {
        super.onPause()
        header.onPause()
    }

    override fun onResume() {
        super.onResume()
        header.onResume()
    }
    protected fun applyLanguage()
    {
        val language = sharedPrefs.getString("USER_LANGUAGE", "default")
        val locale = Locale(language!!)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)

        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

    }
}
