package com.example.chatapplictionlikewhastapp.featureHome.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chatapplictionlikewhastapp.featureHome.repository.ChatRepository
import com.example.chatapplictionlikewhastapp.featureHome.repository.FirebaseClientRepository
import com.example.chatapplictionlikewhastapp.featureHome.repository.FirestoreChatRepository

class ChatViewModelFactory(private val chatsRepository: ChatRepository, private val firestoreChatRepository: FirestoreChatRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel(chatsRepository, firestoreChatRepository) as T
    }
}