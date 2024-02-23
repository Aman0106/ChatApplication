package com.example.chatapplictionlikewhastapp.featureHome.pojo

data class MessageDataClass(
    val message:String,
    val senderUid:String,
    val timeStamp:String? = null
)