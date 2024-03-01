package com.example.chatapplictionlikewhastapp.featureHome.pojo

data class RecentChatUserDataClass(
    val uid: String,
    val profileImage : Int? = null,
    val userName: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val messagesCount: Int
)
