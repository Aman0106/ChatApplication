package com.example.chatapplictionlikewhastapp.featureHome.pojo

import com.google.firebase.Timestamp

data class MessageDataClass(
    val message: String,
    val senderUid: String,
    var timeStamp: Timestamp? = null,
    val read: Boolean = false
)