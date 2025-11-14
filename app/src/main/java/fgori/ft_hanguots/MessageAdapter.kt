package fgori.ft_hanguots

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(private val messageList: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
{
    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val messageText: TextView = itemView.findViewById(R.id.messageTextView)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder{}
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {}
    override fun getItemCount(): Int {}

}
