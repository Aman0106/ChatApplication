package com.example.chatapplictionlikewhastapp.featureHome.repository

import com.example.chatapplictionlikewhastapp.R
import com.example.chatapplictionlikewhastapp.featureHome.pojo.ContactsUserinfo
import com.example.chatapplictionlikewhastapp.featureHome.pojo.RecentChatUserDataClass

class UsersRepository {
    private val dummyRecentChat1 = RecentChatUserDataClass(
        senderUid = "ASD",
        senderProfileImage = R.drawable.hagrid_profile_pic1,
        senderName = "Rubeus Hagrid",
        lastMessage = "Yer a wizard Harry!!",
        lastMessageTime = "9:43 pm",
        messagesCount = 3
    )
    private val dummyRecentChat2 = RecentChatUserDataClass(
        senderUid = "ASD",
        senderProfileImage = R.drawable.dumble_profile_pic1,
        senderName = "Albus Dumbledore",
        lastMessage = "20 points to Gryffindor",
        lastMessageTime = "7:38 pm",
        messagesCount = 1
    )
    private val dummyRecentChat3 = RecentChatUserDataClass(
        senderUid = "ASD",
        senderProfileImage = R.drawable.harmoine_profile_pic,
        senderName = "Harmoine Granger",
        lastMessage = "It's leviOsa not leviosAR",
        lastMessageTime = "2:46 pm",
        messagesCount = 6
    )

    private val dummyContact1 = ContactsUserinfo (
        name = "Rubeus Hagrid",
        profileImage = R.drawable.hagrid_profile_pic1
    )

    fun provideDummyData() = dummyRecentChat1
    fun provideDummyRecentChatsList(): List<RecentChatUserDataClass> {
        return listOf(
            dummyRecentChat1,
            dummyRecentChat2,
            dummyRecentChat3,

            )
    }

    fun provideDummyContactsList(): List<ContactsUserinfo> {
        return listOf(
            dummyContact1,
            dummyContact1,
            dummyContact1
        )
    }
}

