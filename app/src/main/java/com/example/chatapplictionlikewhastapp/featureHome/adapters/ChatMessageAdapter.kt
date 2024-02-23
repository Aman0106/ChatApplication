package com.example.chatapplictionlikewhastapp.featureHome.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplictionlikewhastapp.R
import com.example.chatapplictionlikewhastapp.databinding.ReceivedMessageBoxBinding
import com.example.chatapplictionlikewhastapp.databinding.SentMessageBoxBinding
import com.example.chatapplictionlikewhastapp.featureHome.pojo.MessageDataClass

class ChatMessageAdapter(private val context: Context, private val currentUser: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SENT_MESSAGE = 0
    private val RECEIVED_MESSAGE = 1

    private lateinit var messageList: ArrayList<MessageDataClass>

    fun setMessages(messages: ArrayList<MessageDataClass>) {
        messageList = messages
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == SENT_MESSAGE) {
            val view = LayoutInflater.from(context).inflate(R.layout.sent_message_box, parent, false)
            return SentViewHolder(view)
        }

        val view = LayoutInflater.from(context).inflate(R.layout.received_message_box, parent, false)
        return ReceivedViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curMessage = messageList[position]
        if(holder.javaClass == SentViewHolder::class.java){
            val viewHolder = holder as SentViewHolder
            viewHolder.msg.text = curMessage.message
        }
        else{
            val viewHolder = holder as ReceivedViewHolder
            viewHolder.msg.text = curMessage.message
        }
    }

    override fun getItemCount() = messageList.size

    override fun getItemViewType(position: Int): Int {
        val curMessage = messageList[position]
        if(curMessage.equals(curMessage.senderUid))
            return SENT_MESSAGE
        return RECEIVED_MESSAGE
    }

    inner class SentViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val msg: TextView = view.findViewById(R.id.msg_sent)
    }
    inner class ReceivedViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val msg:TextView = view.findViewById(R.id.msg_received)
    }
}