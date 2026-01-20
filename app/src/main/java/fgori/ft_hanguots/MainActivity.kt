package fgori.ft_hanguots

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.activity.result.contract.ActivityResultContracts


class MainActivity : BaseActivity() {

    private lateinit var contactRecyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private var contactList: MutableList<SmartContact> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        header.notifyActivityChanged("MainActivity")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        contactRecyclerView = findViewById(R.id.contactList)
        contactRecyclerView.layoutManager = LinearLayoutManager(this)

        contactAdapter = ContactAdapter(contactList){contact ->
            val intent = Intent(this, chatActivity::class.java)
            intent.putExtra("contactId", contact.id)
            isInChild = true
            activityLauncher.launch(intent)
        }
        contactRecyclerView.adapter = contactAdapter



        val button = findViewById<Button>(R.id.button)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)
        val button4 = findViewById<Button>(R.id.button4)


        button.setOnClickListener {
            if (button2.isVisible) {
                button2.visibility = View.INVISIBLE
                button3.visibility = View.INVISIBLE
                button4.visibility = View.INVISIBLE
            } else {
                button2.visibility = View.VISIBLE
                button3.visibility = View.VISIBLE
                button4.visibility = View.VISIBLE
            }
        }

        button4.setOnClickListener {
            activityLauncher.launch(Intent(this, option::class.java))
            isInChild = true
        }


        button2.setOnClickListener {
            activityLauncher.launch(Intent(this, CreateContactActivity::class.java))
            isInChild = true
        }

        loadContactsFromDatabase()
    }

    override fun onPause() {
        super.onPause()
    }
    override fun onResume() {
        super.onResume()
        if (isInChild) {
            isInChild = false
        }
        loadContactsFromDatabase()
    }

    private fun loadContactsFromDatabase() {
        val dbHelper = DatabaseHelper(this)
        val updatedContactList = dbHelper.getListLastChat()
        contactList.clear()
        contactList.addAll(updatedContactList)
        contactAdapter.notifyDataSetChanged()
    }

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        recreate()
    }

}
