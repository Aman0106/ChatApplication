package com.example.chatapplictionlikewhastapp.featureHome.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapplictionlikewhastapp.featureHome.pojo.MessageDataClass
import com.example.chatapplictionlikewhastapp.featureHome.repository.ChatRepository

class ChatViewModel(private val chatRepository: ChatRepository): ViewModel() {
    private val _messageList = MutableLiveData<List<MessageDataClass>>()
    val messageList: LiveData<List<MessageDataClass>> get() = _messageList



    fun getAllMessages(roomId: String) {
        _messageList.postValue(chatRepository.fetchDummyMessagesList())
    }
}