package fgori.ft_hanguots

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class MessageAdapter(private val messageList: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val holderHolder: ConstraintLayout = itemView.findViewById(R.id.messageItem)
        val messageText: TextView = itemView.findViewById(R.id.messageTextView)
        val timeText: TextView = itemView.findViewById(R.id.timeTextView)
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

        val timeParams = holder.timeText.layoutParams as ConstraintLayout.LayoutParams
        timeParams.startToStart = ConstraintLayout.LayoutParams.UNSET
        timeParams.endToEnd = ConstraintLayout.LayoutParams.UNSET

        if (getItemViewType(position) == 0) {
            holder.messageText.setBackgroundResource(R.drawable.bg_chat_bubble_received)
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            timeParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        } else {
            holder.messageText.setBackgroundResource(R.drawable.bg_chat_bubble_sent)
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            timeParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }
        holder.messageText.layoutParams = params
        holder.timeText.layoutParams = timeParams

        val hasNextMessage = position < messageList.size - 1
        var showTime = true

        if (hasNextMessage) {
            val nextMessage = messageList[position + 1]
            val timeDifference = nextMessage.timeStamp - message.timeStamp
            if (nextMessage.direction == message.direction && timeDifference < 30000) {
                showTime = false
            }
        }
        if (showTime){
            holder.timeText.visibility = View.VISIBLE
            val formatToUse = SimpleDateFormat("HH:mm", Locale.getDefault())
            holder.timeText.text = formatToUse.format(Date(message.timeStamp))
        }else{
            holder.timeText.visibility = View.GONE
        }


    }
    override fun getItemCount(): Int {
        return messageList.size
    }


}
