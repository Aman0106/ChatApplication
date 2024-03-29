package com.example.chatapplictionlikewhastapp.featureHome.adapters

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.chatapplictionlikewhastapp.databinding.RecentChatCardLayoutBinding
import com.example.chatapplictionlikewhastapp.featureHome.pojo.RecentChatUserDataClass
import java.text.SimpleDateFormat

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
        val sdf = SimpleDateFormat("hh:mm aa")

        if(currentChat.profileImage != null)
            holder.binding.imgProfile.load(currentChat.profileImage)
        holder.binding.tvContactName.text = currentChat.userName
        holder.binding.tvLastMessage.text = currentChat.lastMessage
        holder.binding.tvMessageCount.text = if(currentChat.messagesCount > 99) "99+" else currentChat.messagesCount.toString()
        holder.binding.tvMessageTime.text = sdf.format(currentChat.lastMessageTime!!.seconds * 1000)



        if (currentChat.messagesCount == 0)
            holder.binding.tvMessageCount.visibility = View.INVISIBLE

        holder.binding.recentChatCard.setOnClickListener {
            onItemClicked.invoke(recentChatsList[position])
        }
    }

    override fun getItemCount() = recentChatsList.size

    inner class RecentChatsViewHolder(val binding:RecentChatCardLayoutBinding): RecyclerView.ViewHolder(binding.root)
}