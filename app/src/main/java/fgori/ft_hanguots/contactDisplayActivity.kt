package fgori.ft_hanguots

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.ImageButton
import android.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager


class contactDisplayActivity : BaseActivity() {
    private var contactList: MutableList<Contact> = mutableListOf()
    private lateinit var contactAdapter: ContactDisplayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contact_display)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        contactList = dbHelper.getContacts().toMutableList()
        contactList.sortedBy { it.getValue("name") }

        contactAdapter = ContactDisplayAdapter(contactList.toMutableList()) { contact ->
            val intent = Intent(this, chatActivity::class.java)
            intent.putExtra("contactId", contact.id)
            isInChild = true
            startActivity(intent)
        }


        val serchBar = findViewById<SearchView>(R.id.searchView)
        val returnBtm = findViewById<ImageButton>(R.id.returnBtm)
        returnBtm.setOnClickListener {
            finish()
        }
        val reciclerView = findViewById<RecyclerView>(R.id.contactList)
        reciclerView.layoutManager = GridLayoutManager(this,2)
        reciclerView.adapter = contactAdapter
        serchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(text: String?): Boolean {
                filterList(text)
                return true
            }

            override fun onQueryTextSubmit(text: String?): Boolean {
                return false
            }
        })
    }
    private fun filterList(text: String?) {
        val filtredList = mutableListOf<Contact>()
        if (text.isNullOrEmpty())
            filtredList.addAll(contactList)
        else {
            for (contact in contactList) {
                if (contact.getValue("name").lowercase().contains(text.lowercase()))
                    filtredList.add(contact)
            }
        }
        contactAdapter.updateData(filtredList)
    }

    override fun onResume() {
        super.onResume()
        if (isInChild) {
            isInChild = false
        }
    }


}