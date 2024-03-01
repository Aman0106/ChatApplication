package com.example.chatapplictionlikewhastapp.featureHome.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapplictionlikewhastapp.featureHome.pojo.ContactsUserinfo
import com.example.chatapplictionlikewhastapp.featureHome.pojo.RecentChatUserDataClass
import com.example.chatapplictionlikewhastapp.featureHome.repository.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val usersRepository: UsersRepository,
) : ViewModel() {

    private val _recentChatsList = MutableLiveData<List<RecentChatUserDataClass>>()
    val recentChatsList: LiveData<List<RecentChatUserDataClass>> = _recentChatsList

    private var _contactsListCache: List<ContactsUserinfo>? = null
    private val _contactsList = MutableLiveData<List<ContactsUserinfo>>()
    val contactsList: LiveData<List<ContactsUserinfo>> = _contactsList

    lateinit var currentUser: String

    var selectedChat: ContactsUserinfo? = null

    fun getRecentChats() {
        currentUser = usersRepository.getCurrentUserUid()!!
        viewModelScope.launch(Dispatchers.IO) {
            val chats = usersRepository.getAllChatsFromFirestore()
            _recentChatsList.postValue(chats)
        }
    }

    fun getContactsList() {
//        getFromFirebase()
        getDummyContacts()

    }

    private fun getDummyContacts() {
        _contactsListCache = usersRepository.provideDummyContactsList()
        _contactsList.postValue(_contactsListCache!!)
    }

    private fun getFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            val rawContacts = usersRepository.getAllContactsFromDevice()
            if (_contactsListCache == null)
                _contactsListCache = usersRepository.checkForAppUsers(rawContacts)
            _contactsList.postValue(_contactsListCache!!)
        }
    }


    fun searchContactsList(text: String) {
        val filteredList = ArrayList<ContactsUserinfo>()

        //TODO add number based search
        for (contact in _contactsListCache!!) {

            if (contact.name.lowercase().contains(text.lowercase()))
                filteredList.add(contact)
        }

        _contactsList.postValue(filteredList)
    }

    fun listenForIncomingUnreadMessages() {
//        usersRepository.
    }

}