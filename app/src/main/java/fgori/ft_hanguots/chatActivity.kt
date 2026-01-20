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


class chatActivity() : BaseActivity() {

    private lateinit var messageRecyclerView: RecyclerView // Correct
    private lateinit var messageList : MutableList<Message>
    private lateinit var messageAdapter: MessageAdapter


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


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

        messageRecyclerView = findViewById(R.id.messageList)
        messageRecyclerView.layoutManager = LinearLayoutManager(this)
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
                val newMessage = Message(MsgDir.OUT, message, contactId)
                dbHelper.addMessage(newMessage)
                messageList.add(newMessage)

                messageAdapter.notifyItemInserted(messageList.size - 1)
                messageRecyclerView.scrollToPosition(messageList.size - 1)

                chatText.text.clear()
            }
        }

    }


}