package fgori.ft_hanguots

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.widget.Toast
import android.widget.ImageView
import android.content.Intent
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class CreateContactActivity : AppCompatActivity() {
    private lateinit var image: ImageView
    private var selectedImageUri: Uri? = null

    private fun saveImageToInternalStorage(uri: Uri): Uri? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(filesDir, "IMG_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val savedUri = saveImageToInternalStorage(it)
            savedUri?.let { newUri ->
                selectedImageUri = newUri
                image.setImageURI(newUri)
            }
        }
    }

    fun createContactList() : MutableList<EditText>
    {
        val list = mutableListOf<EditText>()
        list.add(findViewById<EditText>(R.id.nameText))
        list.add(findViewById<EditText>(R.id.surnameText))
        list.add(findViewById<EditText>(R.id.emailText))
        list.add(findViewById<EditText>(R.id.phoneText))
        list.add(findViewById<EditText>(R.id.addressText))
        return list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_contact)
        val header = findViewById<Header>(R.id.header)
        header.notifyActivityChanged("CreateContactActivity")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        image = findViewById(R.id.imageViewLog)

        val saveBtm = findViewById<View>(R.id.saveBtm)
        val returnBtm = findViewById<View>(R.id.returnBtm)
        val dbHelper = DatabaseHelper(this)
        val listOfText = createContactList()
        returnBtm.setOnClickListener {
            finish()
        }

        saveBtm.setOnClickListener {
            if (listOfText[0].text.toString() == "")
            {
                Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (listOfText[2].text.toString() == "" && listOfText[3].text.toString() == "")
            {
                Toast.makeText(this, "Email or phone is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val newContact = Contact(id = System.currentTimeMillis(),
                listOfText[0].text.toString(),
                listOfText[1].text.toString(),
                listOfText[2].text.toString(),
                listOfText[3].text.toString(),
                listOfText[4].text.toString(),
                selectedImageUri?.toString() ?: "")
            dbHelper.addContact(newContact)
            finish()
        }

        image.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }
}