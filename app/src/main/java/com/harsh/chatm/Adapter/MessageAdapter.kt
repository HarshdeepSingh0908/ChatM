package com.harsh.chatm.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.harsh.chatm.DataClasses.Message
import com.harsh.chatm.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ITEM_RECIEVE = 1
    val ITEM_SENT = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.receive_message, parent, false)
            return RecieveViewHoler(view)
        } else {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.sent_message, parent, false)
            return SentViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return messageList.size

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.javaClass == SentViewHolder::class.java) {

            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
            holder.sentMessageTime.text = formatTimestamp(currentMessage.timeStamp ?: 0)
        } else {
            val viewHolder = holder as RecieveViewHoler
            holder.recieveMessage.text = currentMessage.message
            holder.recieveMessageTime.text = formatTimestamp(currentMessage.timeStamp ?: 0)
        }

    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            return ITEM_SENT
        } else {
            return ITEM_RECIEVE
        }
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.tvSentMessage)
        val sentMessageTime = itemView.findViewById<TextView>(R.id.tvSentMessageTime)
    }

    class RecieveViewHoler(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recieveMessage = itemView.findViewById<TextView>(R.id.tvRecievedMessage)
        val recieveMessageTime = itemView.findViewById<TextView>(R.id.tvRecievedMessageTime)
    }
    fun formatTimestamp(timestamp: Long): String {
        val seconds = timestamp / 1000
        val date = Date(seconds * 1000)
        val dateFormatter = SimpleDateFormat("HH:mm a", Locale.getDefault())
        return dateFormatter.format(date)
    }

}