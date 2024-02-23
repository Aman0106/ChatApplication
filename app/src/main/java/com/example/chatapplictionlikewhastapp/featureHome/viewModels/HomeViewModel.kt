package com.example.chatapplictionlikewhastapp.featureHome.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapplictionlikewhastapp.featureHome.pojo.ContactsUserinfo
import com.example.chatapplictionlikewhastapp.featureHome.pojo.RecentChatUserDataClass
import com.example.chatapplictionlikewhastapp.featureHome.repository.UsersRepository

class HomeViewModel(private val usersRepository: UsersRepository) : ViewModel() {
    private val _recentChatsList = MutableLiveData<List<RecentChatUserDataClass>>()
    val recentChatsList: LiveData<List<RecentChatUserDataClass>> = _recentChatsList

    private val _contactsList = MutableLiveData<List<ContactsUserinfo>>()
    val contactsList: LiveData<List<ContactsUserinfo>> = _contactsList

    var selectedChat: RecentChatUserDataClass? = null

    fun getRecentChats() {
        val chats = listOf(usersRepository.provideDummyData())
        _recentChatsList.postValue(usersRepository.provideDummyRecentChatsList())
    }

    fun getContactsList() {
        _contactsList.postValue(usersRepository.provideDummyContactsList())
    }

}