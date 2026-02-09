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
import android.widget.ImageButton

class UpdateContactActivity : BaseActivity() {

    private lateinit var image: ImageView
    private var selectedImageUri: Uri? = null
    var isChange: Boolean = false
    private val changedValues = mutableMapOf<String, String>()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(it, takeFlags)
            } catch (e: SecurityException) {
                e.printStackTrace()
                val txt = getString(R.string.ImageFail)
                Toast.makeText(this, txt, Toast.LENGTH_SHORT).show()
            }

            selectedImageUri = it
            image.setImageURI(it)
            changedValues["img"] = it.toString()
            isChange = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contactId = intent.getLongExtra("contactId", -1L)
        var contact : Contact? = null

        enableEdgeToEdge()
        setContentView(R.layout.activity_update_contact)
        header.notifyActivityChanged("UpdateContactActivity")

        val nameText = findViewById<EditText>(R.id.nameText)
        val surnameText = findViewById<EditText>(R.id.surnameText)
        val phoneText = findViewById<EditText>(R.id.phoneText)
        val emailText = findViewById<EditText>(R.id.emailText)
        val addressText = findViewById<EditText>(R.id.addressText)
        image = findViewById<ImageView>(R.id.imageViewLog)
        val cancelBtn = findViewById<ImageButton>(R.id.deleteBtm)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (contactId != -1L) {
            contact = dbHelper.getIdContact(contactId)
            if(contact == null){
                finish()
                return
            }
        }
        else{
            finish()
        }


        nameText.setText(contact?.getValue("name"), null)
        surnameText.setText(contact?.getValue("surname"), null)
        phoneText.setText(contact?.getValue("phone"), null)
        emailText.setText(contact?.getValue("email"), null)
        addressText.setText(contact?.getValue("address"), null)
        val imageUriString = contact?.getValue("img")
        if (imageUriString != null && imageUriString.isNotEmpty()) {

            image.setImageURI(imageUriString.toUri())
        } else {
            image.setImageResource(R.drawable.ic_launcher_foreground)
        }
        image.setOnClickListener {
            isInChild = true
            pickImageLauncher.launch("image/*")
        }
        fun createTextChangeWatcher(key: String) = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {            }
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != contact?.getValue(key)) {
                    changedValues[key] = s.toString()
                } else {
                    changedValues.remove(key)
                }
                isChange = changedValues.isNotEmpty()
            }
        }
        nameText.addTextChangedListener(createTextChangeWatcher("name"))
        surnameText.addTextChangedListener(createTextChangeWatcher("surname"))
        phoneText.addTextChangedListener(createTextChangeWatcher("phone"))
        emailText.addTextChangedListener(createTextChangeWatcher("email"))
        addressText.addTextChangedListener(createTextChangeWatcher("address"))

        val returnBtn = findViewById<Button>(R.id.returnBtm)
        returnBtn.setOnClickListener {
            finish()
        }
        val saveBtn = findViewById<Button>(R.id.saveBtm)
        cancelBtn.setOnClickListener {
            dbHelper.deleteContact(contactId)
            finish()
        }
        saveBtn.setOnClickListener {
            if (isChange) {
                if (contact != null) {
                    if (contactId > 0) {
                        for((key, value ) in changedValues) {
                            if (key == "name" && value.isBlank()) {
                                val text = getString(R.string.nameRequest)
                                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                            if (key == "phone" && (value.length != 10 || dbHelper.isNumberInDatabase(value) != 0)){
                                val text = getString(R.string.PhoneRequest)
                                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }

                            contact.setValue(key, value)
                        }
                        dbHelper.upDateContact(contact, contactId)
                        finish()
                    }
                }
            }else{
                val txt = getString(R.string.nothingChange)
                Toast.makeText(this, txt, Toast.LENGTH_SHORT).show()
            }
        }
        header.setButton(saveBtn)
        header.setButton(returnBtn)

    }
    override fun onResume() {
        super.onResume()
        if (isInChild) {
            isInChild = false
        }
    }
}