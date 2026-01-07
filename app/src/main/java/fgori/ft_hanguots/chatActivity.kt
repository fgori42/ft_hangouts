package fgori.ft_hanguots

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager


class chatActivity() : AppCompatActivity() {
    private val dbhelper = DatabaseHelper(this)
    private lateinit var messageRecyclerView: RecyclerView // Correct
    private lateinit var messageList : MutableList<Message>
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val contactId = intent.getLongExtra("contactId", -1L)
        setContentView(R.layout.activity_chat)
        val header = findViewById<Header>(R.id.header)
        header.notifyActivityChanged("ChatActivity")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

        messageRecyclerView = findViewById(R.id.messageList)
        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        if (contactId != -1L) {
            messageAdapter = MessageAdapter(messageList)
            messageRecyclerView.adapter = messageAdapter
        }else{
            messageList = mutableListOf()
        }
    }


}