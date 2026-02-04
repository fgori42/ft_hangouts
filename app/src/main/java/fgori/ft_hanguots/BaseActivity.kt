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
    protected var isInChild: Boolean = false

    companion object {
        private fun getUpdatedContext(context: Context): Context {
            val sharedPrefs = context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
            val languageCode = sharedPrefs.getString("app_language", "auto") ?: "auto"

            val localeToSet: Locale = if (languageCode.equals("auto", ignoreCase = true)) {
                context.resources.configuration.locales.get(0)
            } else {
                Locale(languageCode)
            }
            Locale.setDefault(localeToSet)

            val configuration = Configuration(context.resources.configuration)
            configuration.setLocale(localeToSet)
            return context.createConfigurationContext(configuration)
        }
    }
        override fun attachBaseContext(newBase: Context) {
            val context = getUpdatedContext(newBase)
            super.attachBaseContext(context)
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        sharedPrefs = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
    }
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        header = findViewById(R.id.header)
    }
    override fun onPause() {
        super.onPause()
        if (!isInChild)
            header.onPause()
    }

    override fun onResume() {
        super.onResume()
        header.onResume()
    }
}
