package com.example.chatapplictionlikewhastapp.featureHome.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.chatapplictionlikewhastapp.databinding.RecentChatCardLayoutBinding
import com.example.chatapplictionlikewhastapp.featureHome.pojo.RecentChatUserDataClass

class RecentChatsAdapter: RecyclerView.Adapter<RecentChatsAdapter.RecentChatsViewHolder>() {

    private var recentChatsList = ArrayList<RecentChatUserDataClass>()

    lateinit var onItemClicked: ((RecentChatUserDataClass) -> Unit)

    fun setRecentChats(chatList: List<RecentChatUserDataClass>) {
        recentChatsList = chatList as ArrayList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatsViewHolder {
        return RecentChatsViewHolder(RecentChatCardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecentChatsViewHolder, position: Int) {
        val currentChat = recentChatsList[position]
        holder.binding.imgProfile.load(currentChat.senderProfileImage)
        holder.binding.tvContactName.text = currentChat.senderName
        holder.binding.tvLastMessage.text = currentChat.lastMessage
        holder.binding.tvMessageCount.text = currentChat.messagesCount.toString()
        holder.binding.tvMessageTime.text = currentChat.lastMessageTime

        holder.binding.recentChatCard.setOnClickListener {
            onItemClicked.invoke(recentChatsList[position])
        }
    }

    override fun getItemCount() = recentChatsList.size

    inner class RecentChatsViewHolder(val binding:RecentChatCardLayoutBinding): RecyclerView.ViewHolder(binding.root)
}