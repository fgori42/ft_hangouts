package fgori.ft_hanguots

import android.os.Bundle
import android.Manifest
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
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.core.content.ContextCompat


class MainActivity : BaseActivity() {

    private lateinit var contactRecyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private var contactList: MutableList<SmartContact> = mutableListOf()
    private val SMS_PERMISSIONS_REQUEST_CODE = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkAndRequestSmsPermission()
        setContentView(R.layout.activity_main)
        header.notifyActivityChanged("MainActivity")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        contactRecyclerView = findViewById(R.id.contactList)
        contactRecyclerView.layoutManager = LinearLayoutManager(this)
        contactList = dbHelper.getListLastChat().toMutableList()
        updateContactList(contactList.toMutableList())

        val searchBar = findViewById<SearchView>(R.id.search_view)
        val searchLayout = findViewById<View>(R.id.searchbarContainer)
        val opBtn = findViewById<ImageButton>(R.id.option_button)
        opBtn.colorFilter = android.graphics.PorterDuffColorFilter(header.textColor, android.graphics.PorterDuff.Mode.SRC_IN)
        searchLayout.setBackgroundColor( Color.parseColor(header.getHeaderColor()))
        val searchBarMargin = searchBar.background.mutate()
        if (searchBarMargin is GradientDrawable) {
            val borderWidth = (2 * resources.displayMetrics.density).toInt()
            searchBarMargin.setStroke(borderWidth,header.textColor)
        }

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(text: String?): Boolean {
                filterContact(text)
                return true
            }

            override fun onQueryTextSubmit(text: String?): Boolean {
                return false
            }
        })

        val filter = IntentFilter("com.fgori.ft_hanguots.UPDATE_CHAT")

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            registerReceiver(refreshReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(refreshReceiver, filter)
        }


        val button = findViewById<Button>(R.id.button2)
        val button4 = findViewById<ImageButton>(R.id.option_button)



        button4.setOnClickListener {
            activityLauncher.launch(Intent(this, option::class.java))
            isInChild = true
        }


        button.setOnClickListener {
            activityLauncher.launch(Intent(this, CreateContactActivity::class.java))
            isInChild = true
        }

        loadContactsFromDatabase()
    }

    private fun updateContactList(ListToshow : MutableList<SmartContact>){
        contactAdapter = ContactAdapter(ListToshow, header.textColor){contact ->
            val intent = Intent(this, chatActivity::class.java)
            intent.putExtra("contactId", contact.id)
            isInChild = true
            activityLauncher.launch(intent)
        }
        contactRecyclerView.adapter = contactAdapter

    }
    private fun filterContact(text: String?) {
        val filtedList = mutableListOf<SmartContact>()
        if (text.isNullOrEmpty())
            filtedList.addAll(contactList)
        else {
            for (contact in contactList) {
                if (contact.name.lowercase().contains(text.lowercase())) {
                    filtedList.add(contact)
                }
            }
        }
        contactAdapter.updateData(filtedList)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(refreshReceiver)
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

    private val refreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.fgori.ft_hanguots.UPDATE_CHAT")
                loadContactsFromDatabase()
        }
    }

    private fun loadContactsFromDatabase() {
        val newHelper = DatabaseHelper(this)
        dbHelper.close()
        dbHelper = newHelper
        newHelper.close()
        val updatedContactList = dbHelper.getListLastChat()
        contactList.clear()
        contactList.addAll(updatedContactList)
        contactAdapter.notifyDataSetChanged()
        updateContactList(contactList.toMutableList())
    }

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        recreate()
    }

    private fun checkAndRequestSmsPermission() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.SEND_SMS)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECEIVE_SMS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                SMS_PERMISSIONS_REQUEST_CODE
            )
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permessi SMS concessi!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Attenzione: alcune funzioni SMS potrebbero non essere disponibili.", Toast.LENGTH_LONG).show()
            }
        }
    }


}
