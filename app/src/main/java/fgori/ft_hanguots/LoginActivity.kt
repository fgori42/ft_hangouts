package fgori.ft_hanguots

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import java.util.concurrent.TimeUnit


class LoginActivity : BaseActivity() {

    private lateinit var image: ImageView
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            val contentResolver = applicationContext.contentResolver
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                contentResolver.takePersistableUriPermission(it, takeFlags)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            image.setImageURI(it)
        }
    }

    fun singInListInit(): MutableList<EditText>
    {
        val sigIntexts = mutableListOf<EditText>()
        sigIntexts.add(findViewById<EditText>(R.id.sigInPass))
        sigIntexts.add(findViewById<EditText>(R.id.sigInPhone))
        sigIntexts.add(findViewById<EditText>(R.id.sigInEmail))
        sigIntexts.add(findViewById<EditText>(R.id.SigInAddress))
        return sigIntexts
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        header = findViewById<Header>(R.id.header)


        val  isIn  = sharedPrefs.getBoolean("IS_LOGGED_IN", false)
        if (isIn){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        val loginButton = findViewById<Button>(R.id.logIn_btm)
        val registerButton = findViewById<Button>(R.id.button_sigIn)
        val nameText = findViewById<EditText>(R.id.logInName)
        val passwordText = findViewById<EditText>(R.id.logInPass)
        image = findViewById<ImageView>(R.id.SigInImg)
        val sigIntexts = singInListInit()
        val returnButton = findViewById<Button>(R.id.returnBtm)

        image.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        registerButton.setOnClickListener {
            if(passwordText.isVisible)
            {

                passwordText.visibility = View.INVISIBLE
                loginButton.visibility = View.INVISIBLE
                for(text in sigIntexts)
                {
                    text.visibility = View.VISIBLE
                }
                image.visibility = View.VISIBLE
                returnButton.visibility = View.VISIBLE
            }
            else
            {
                val name = nameText.text.toString()
                val password = sigIntexts[0].text.toString()


                if (name.isBlank() || password.isBlank()) {
                    val missingField = if (name.isBlank()) "Name" else "Password"
                    Toast.makeText(this, "$missingField is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val editor = sharedPrefs.edit()

                editor.putString("USER_NAME", name)
                editor.putString("USER_IMAGE_URI", selectedImageUri?.toString() ?: "")
                editor.putString("USER_PASSWORD", password)
                editor.putBoolean("IS_LOGGED_IN", true)

                editor.apply()

                Toast.makeText(this, "Welcome, $name!", Toast.LENGTH_SHORT).show()


                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

        returnButton.setOnClickListener {
            if (!passwordText.isVisible)
            {
                passwordText.visibility = View.VISIBLE
                loginButton.visibility = View.VISIBLE
                for(text in sigIntexts)
                {
                    text.visibility = View.INVISIBLE
                }
                image.visibility = View.INVISIBLE
                returnButton.visibility = View.INVISIBLE
            }
        }

        loginButton.setOnClickListener {
            val name = nameText.text.toString()
            val password = passwordText.text.toString()

            if (name.isBlank() || password.isBlank()) {
                val missingField = if (name.isBlank()) "Name" else "Password"
                Toast.makeText(this, "$missingField is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val savedName = sharedPrefs.getString("USER_NAME", "")
            val savedPassword = sharedPrefs.getString("USER_PASSWORD", "")

            if (name == savedName && password == savedPassword) {
                val editor = sharedPrefs.edit()
                editor.putBoolean("IS_LOGGED_IN", true)
                editor.apply()

                Toast.makeText(this, "Welcome back, $name!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
        val switchButton = findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.switch2)
        if (sharedPrefs.getString("USER_LANGUAGE", "default") == "en")
            switchButton.isChecked = true
        val editor = sharedPrefs.edit()
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editor.putString("USER_LANGUAGE", "en").apply()
            } else {
                editor.putString("USER_LANGUAGE", "default").apply()
            }

        }


    }
}