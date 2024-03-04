package com.example.chatapplictionlikewhastapp.featureHome.pojo

import com.google.firebase.Timestamp

data class RecentChatUserDataClass(
    val uid: String,
    val profileImage : Int? = null,
    val userName: String,
    val lastMessage: String,
    val lastMessageTime: Timestamp? = null,
    val messagesCount: Int
)
