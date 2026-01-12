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


class MainActivity : AppCompatActivity() {

    private lateinit var contactRecyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private var contactList: MutableList<Contact> = mutableListOf()
    private lateinit var header: Header


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        header = findViewById(R.id.header)
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
            startActivity(intent)
        }
        contactRecyclerView.adapter = contactAdapter



        val button = findViewById<Button>(R.id.button)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)
        val button4 = findViewById<Button>(R.id.button4)

        val buttonRed = findViewById<Button>(R.id.red)
        val buttonBlu = findViewById<Button>(R.id.blu)
        val buttonGreen = findViewById<Button>(R.id.green)



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

        button2.setOnClickListener {
            startActivity(Intent(this, CreateContactActivity::class.java))

        }

        buttonGreen.setOnClickListener {
            header.changeColor("#00FF00")

        }
        buttonRed.setOnClickListener {
            header.changeColor("#FF0000")

        }
        buttonBlu.setOnClickListener {
            header.changeColor("#0000FF")

        }

    }

    override fun onPause() {
        super.onPause()
        header.onPause()
    }

    override fun onResume() {
        super.onResume()
        header.onResume()
        loadContactsFromDatabase()
    }

    private fun loadContactsFromDatabase() {
        val dbHelper = DatabaseHelper(this)
        val updatedContactList = dbHelper.getContacts()
        contactList.clear()
        contactList.addAll(updatedContactList)
        contactAdapter.notifyDataSetChanged()
    }

}
