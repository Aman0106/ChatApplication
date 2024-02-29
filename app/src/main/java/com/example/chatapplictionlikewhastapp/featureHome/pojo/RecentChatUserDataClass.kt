package com.example.chatapplictionlikewhastapp.featureHome.pojo

data class RecentChatUserDataClass(
    val senderUid: String,
    val senderProfileImage : Int,
    val senderName: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val messagesCount: Int
)
