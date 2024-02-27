package com.example.chatapplictionlikewhastapp.featureHome.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.example.chatapplictionlikewhastapp.R
import com.example.chatapplictionlikewhastapp.featureHome.pojo.ContactsUserinfo
import com.example.chatapplictionlikewhastapp.featureHome.pojo.RecentChatUserDataClass

class UsersRepository(private val context: Context) {

    companion object {
        private const val TAG = "INSIDE_USER_REPOSITORY"
    }

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

    private val dummyContact1 = ContactsUserinfo(
        name = "Rubeus Hagrid", profileImage = R.drawable.hagrid_profile_pic1
    )

    fun provideDummyData() = dummyRecentChat1
    fun provideDummyRecentChatsList(): List<RecentChatUserDataClass> {
        return listOf(
            dummyRecentChat1,
            dummyRecentChat2,
            dummyRecentChat3,

            )
    }


    fun getAllContactsFromDevice(): List<ContactsUserinfo> {
        val fromColumns = arrayOf(
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )

        val contactsList = ArrayList<ContactsUserinfo>()
        val conResolver: ContentResolver = context.contentResolver
        val cursor = conResolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null, "display_name ASC"
        )

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                try {
                    var uid =
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    var name =
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
                    var phoneNumber = ""
                    if (cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        val cursorInfo = conResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(uid),
                            null
                        )
                        while (cursorInfo != null && cursorInfo.moveToNext())
                            phoneNumber = cursorInfo.getString(
                                cursorInfo.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )

                        cursorInfo?.close()
                    }

                    val contactInfo = ContactsUserinfo(
                        uid = uid, name = name, phoneNumber = phoneNumber
                    )

                    if (phoneNumber != "")
                        contactsList.add(contactInfo)

                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                }
            }
            cursor.close()
        }
//        Log.d(TAG, " Total Contacts(${cursor?.count})")

        return contactsList
    }

    fun provideDummyContactsList(): List<ContactsUserinfo> {
        return listOf(
            dummyContact1, dummyContact1, dummyContact1
        )
    }
}

