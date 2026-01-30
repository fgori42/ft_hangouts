package fgori.ft_hanguots

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.EditText
import android.content.Intent
import android.content.Context
import android.telephony.SmsManager
import android.Manifest
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.ImageButton
import android.widget.Toast
import android.net.Uri


class chatActivity() : BaseActivity() {

    private lateinit var messageRecyclerView: RecyclerView // Correct
    private lateinit var messageList : MutableList<Message>
    private lateinit var messageAdapter: MessageAdapter
    private val CALL_PHONE_PERMISSION_REQUEST_CODE = 102

    override fun onResume()
    {
        super.onResume()
        if (isInChild)
            recreate()
        loadChat()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(refreshReceiver)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val contactId = intent.getLongExtra("contactId", -1L)
        setContentView(R.layout.activity_chat)
        header = findViewById<Header>(R.id.header)
        header.notifyActivityChanged("ChatActivity")
        val contact = dbHelper.getIdContact(contactId)
        if (contact != null) {
            header.populateChatHeader(contact)
        }else{
            finish()
        }

        val filter = IntentFilter("com.fgori.ft_hanguots.UPDATE_CHAT")

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            registerReceiver(refreshReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(refreshReceiver, filter)
        }


        header.onEditClickListener = {
            val intent = Intent(this, UpdateContactActivity::class.java)
            intent.putExtra("contactId", contactId)
            isInChild = true
            startActivity(intent)

        }

        header.onPhoneClickListener = onPhoneClickListener@{
            val phoneNumber = contact?.getValue("phone")
            if(phoneNumber.isNullOrEmpty()){
                return@onPhoneClickListener
            } else {
                makePhoneCall(phoneNumber)
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

        messageRecyclerView = findViewById(R.id.messageList)
        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        if (contactId != -1L) {
            messageList = dbHelper.getIdList(contactId)
        }else{
            messageList = mutableListOf()
        }
        messageAdapter = MessageAdapter(messageList)
        messageRecyclerView.adapter = messageAdapter

        val sendButton = findViewById<Button>(R.id.sendButton)
        sendButton.setOnClickListener {
            val chatText = findViewById<EditText>(R.id.chatText)
            val message = chatText.text.toString()
            if (message.isNotEmpty()) {
                val newMessage = Message(MsgDir.OUT, message, contactId, System.currentTimeMillis())
                dbHelper.addMessage(newMessage)
                messageList.add(newMessage)

                messageAdapter.notifyItemInserted(messageList.size - 1)
                messageRecyclerView.scrollToPosition(messageList.size - 1)

                chatText.text.clear()
                val phoneNumber = contact?.getValue("phone")
                if (phoneNumber.isNullOrEmpty()) {
                    return@setOnClickListener
                }
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    try{
                        val smsManager = getSystemService(SmsManager::class.java)
                        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                    }catch (e: Exception){
                        return@setOnClickListener
                    }
                }
            }
        }

    }

    private fun makePhoneCall(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phoneNumber")
            try{
                startActivity(callIntent)
            }catch (e: Exception){
                return
            }
        }else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL_PHONE_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALL_PHONE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val contactId = intent.getLongExtra("contactId", -1L)
                val contact = dbHelper.getIdContact(contactId)
                val phoneNumber = contact?.getValue("phone")
                if (!phoneNumber.isNullOrEmpty()) {
                    makePhoneCall(phoneNumber)
                }
            }
        }
    }
    private fun loadChat()
    {
        messageList.clear()
        messageList.addAll(dbHelper.getIdList(
            intent.getLongExtra("contactId", -1L)
        ))
        messageAdapter.notifyDataSetChanged()
        messageRecyclerView.scrollToPosition(messageList.size - 1)
    }

    private val refreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.fgori.ft_hanguots.UPDATE_CHAT")
                loadChat()
        }
    }


}