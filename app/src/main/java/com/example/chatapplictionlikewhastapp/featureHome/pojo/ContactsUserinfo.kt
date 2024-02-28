package com.example.chatapplictionlikewhastapp.featureHome.pojo

data class ContactsUserinfo(
    var uid: String? = null,
    val name: String,
    val phoneNumber: String? = null,
    val profileImage:Int? = null,
    var isAppUser:Boolean = false
)