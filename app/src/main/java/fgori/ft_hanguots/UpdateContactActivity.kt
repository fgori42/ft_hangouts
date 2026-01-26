package fgori.ft_hanguots

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import android.widget.ImageView
import androidx.core.net.toUri
import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import android.text.TextWatcher
import android.text.Editable
import android.widget.Button
import android.widget.Toast
import android.content.Intent
import android.content.Context

class UpdateContactActivity : BaseActivity() {

    private lateinit var image: ImageView
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            image.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contactId = intent.getLongExtra("contactId", -1L)
        var contact : Contact? = null
        var isChange: Boolean = false
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_contact)
        header.notifyActivityChanged("UpdateContactActivity")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (contactId != -1L) {
            contact = dbHelper.getIdContact(contactId)
                if(contact == null){
                    Toast.makeText(this, "Contatto non trovato", Toast.LENGTH_SHORT).show()
                    finish()
                    return
                }
            }
        else{
            finish()
        }

        val nameText = findViewById<EditText>(R.id.nameText)
        val surnameText = findViewById<EditText>(R.id.surnameText)
        val phoneText = findViewById<EditText>(R.id.phoneText)
        val emailText = findViewById<EditText>(R.id.emailText)
        val addressText = findViewById<EditText>(R.id.addressText)
        val imageViewLog = findViewById<ImageView>(R.id.imageViewLog)

        nameText.setText(contact?.getValue("name"), null)
        surnameText.setText(contact?.getValue("surname"), null)
        phoneText.setText(contact?.getValue("phone"), null)
        emailText.setText(contact?.getValue("email"), null)
        addressText.setText(contact?.getValue("address"), null)
        val imageUriString = contact?.getValue("img")
        if (imageUriString != null && imageUriString.isNotEmpty()) {

            imageViewLog.setImageURI(imageUriString.toUri())
        } else {
            imageViewLog.setImageResource(R.drawable.ic_launcher_foreground)
        }
        imageViewLog.setOnClickListener {
            isChange = true
            pickImageLauncher.launch("image/*")
        }
        fun createTextChangeWatcher(updateAction: (String) -> Unit) = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {            }
            override fun afterTextChanged(s: Editable?) {
                isChange = true
                updateAction(s.toString())
            }
        }
        nameText.addTextChangedListener(createTextChangeWatcher{newText ->
            contact?.setValue("name", newText)
        })
        surnameText.addTextChangedListener(createTextChangeWatcher{newText ->
            contact?.setValue("surname", newText)
        })
        phoneText.addTextChangedListener(createTextChangeWatcher{newText ->
            contact?.setValue("phone", newText)
        })
        emailText.addTextChangedListener(createTextChangeWatcher{newText ->
            contact?.setValue("email", newText)
        })
        addressText.addTextChangedListener(createTextChangeWatcher{newText ->
            contact?.setValue("address", newText)
        })

        val returnBtn = findViewById<Button>(R.id.returnBtm)
        returnBtn.setOnClickListener {
            finish()
        }
        val saveBtn = findViewById<Button>(R.id.saveBtm)
        saveBtn.setOnClickListener {
            if (isChange) {
                if (contact != null) {
                    if (contactId > 0) {
                        dbHelper.upDateContact(contact, contactId)
                    }
                }
            }else{
                Toast.makeText(this, "@string/nothingChange", Toast.LENGTH_SHORT).show()
            }
        }
    }


}