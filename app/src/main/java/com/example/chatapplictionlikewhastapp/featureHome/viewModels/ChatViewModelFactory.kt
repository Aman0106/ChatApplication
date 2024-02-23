package com.example.chatapplictionlikewhastapp.featureHome.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chatapplictionlikewhastapp.featureHome.repository.ChatRepository

class ChatViewModelFactory(private val chatsRepository: ChatRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel(chatsRepository) as T
    }
}