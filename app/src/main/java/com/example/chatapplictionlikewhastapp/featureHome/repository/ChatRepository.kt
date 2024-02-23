package com.example.chatapplictionlikewhastapp.featureHome.repository

import com.example.chatapplictionlikewhastapp.featureHome.pojo.MessageDataClass

class ChatRepository {

    fun fetchDummyMessagesList() = dummyMessagesList
}

private val dummyMessagesList = listOf(
    MessageDataClass(message = "Hi", senderUid = "1"),
    MessageDataClass(message = "Hi", senderUid = "1"),
    MessageDataClass(message = "Hi", senderUid = "0"),
    MessageDataClass(message = "Hi", senderUid = "0"),
    MessageDataClass(message = "Hi", senderUid = "0"),
    MessageDataClass(message = "Hi", senderUid = "0"),
    MessageDataClass(message = "Hi", senderUid = "0"),
    MessageDataClass(message = "lkdfivervoitutoberiututbnenuuieyrvyvuyvbrtuykvdkjvblivygiidvdmyjdb\nHi", senderUid = "0"),
    MessageDataClass(message = "Hi", senderUid = "0"),
    MessageDataClass(message = "Hi", senderUid = "0"),
    MessageDataClass(message = "Hi", senderUid = "0"),
    MessageDataClass(message = "Hi", senderUid = "0"),
    MessageDataClass(message = "Hi", senderUid = "0"),
    MessageDataClass(message = "Hi", senderUid = "0"),
    MessageDataClass(message = "Hi", senderUid = "0"),
    MessageDataClass(message = "Hi", senderUid = "0"),
    MessageDataClass(message = "Hdsfkahfhiabciuwueu;cwldlhiksjruiaweujebdliuewkusebliuei", senderUid = "0"),
    MessageDataClass(message = "Hi", senderUid = "0"),
    MessageDataClass(message = "Hi", senderUid = "1")
)