package com.example.chatapplictionlikewhastapp.featureHome.pojo

data class RecentChatUserDataClass(
    val senderId: String,
    val senderProfilePic: Int,
    val senderName: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val messagesCount: Int
)
