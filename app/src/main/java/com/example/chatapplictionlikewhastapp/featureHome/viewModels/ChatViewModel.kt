package com.example.chatapplictionlikewhastapp.featureHome.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapplictionlikewhastapp.featureHome.pojo.MessageDataClass
import com.example.chatapplictionlikewhastapp.featureHome.repository.ChatRepository
import com.example.chatapplictionlikewhastapp.featureHome.repository.FirebaseClientRepository

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val firebaseClientRepository: FirebaseClientRepository
) : ViewModel() {

    private val _messageList = MutableLiveData<List<MessageDataClass>>()
    val messageList: LiveData<List<MessageDataClass>> get() = _messageList


    fun getAllMessages(roomId: String) {
        _messageList.postValue(chatRepository.fetchDummyMessagesList())
    }

    fun sendMessageToUser(uid: String, message: String) {
        firebaseClientRepository.sendMessageToUser(uid, message)
    }
}