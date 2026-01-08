package fgori.ft_hanguots

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView



class MessageAdapter(private val messageList: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val messageText: TextView = itemView.findViewById(R.id.messageTextView)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        if (message.direction == MsgDir.IN) {
            return 0
        }
        return 1

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder{
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return MessageViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.messageText.text = message.content
        val params = holder.messageText.layoutParams as ConstraintLayout.LayoutParams
        params.startToStart = ConstraintLayout.LayoutParams.UNSET
        params.endToEnd = ConstraintLayout.LayoutParams.UNSET
        if (getItemViewType(position) == 0) { // MsgDir.IN (Ricevuto -> Sinistra)
            holder.messageText.setBackgroundResource(R.drawable.bg_chat_bubble_received)
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        } else { // MsgDir.OUT (Inviato -> Destra)
            holder.messageText.setBackgroundResource(R.drawable.bg_chat_bubble_sent)
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }
        holder.messageText.layoutParams = params
    }
    override fun getItemCount(): Int {
        return messageList.size
    }


}
