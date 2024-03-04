package com.example.chatapplictionlikewhastapp.featureHome.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.example.chatapplictionlikewhastapp.R
import com.example.chatapplictionlikewhastapp.featureHome.pojo.ContactsUserinfo
import com.example.chatapplictionlikewhastapp.featureHome.pojo.RecentChatUserDataClass
import com.example.chatapplictionlikewhastapp.utils.HelperFunctions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class UsersRepository(private val context: Context) {

    companion object {
        private const val TAG = "INSIDE_USER_REPOSITORY"
    }

    private val dummyRecentChat1 = RecentChatUserDataClass(
        uid = "ASD",
        profileImage = R.drawable.hagrid_profile_pic1,
        userName = "Rubeus Hagrid",
        lastMessage = "Yer a wizard Harry!!",
        lastMessageTime = "9:43 pm",
        messagesCount = 3
    )
    private val dummyRecentChat2 = RecentChatUserDataClass(
        uid = "ASD",
        profileImage = R.drawable.dumble_profile_pic1,
        userName = "Albus Dumbledore",
        lastMessage = "20 points to Gryffindor",
        lastMessageTime = "7:38 pm",
        messagesCount = 1
    )
    private val dummyRecentChat3 = RecentChatUserDataClass(
        uid = "ASD",
        profileImage = R.drawable.harmoine_profile_pic,
        userName = "Harmoine Granger",
        lastMessage = "It's leviOsa not leviosAR",
        lastMessageTime = "2:46 pm",
        messagesCount = 6
    )

    private val firestoreDb = Firebase.firestore
    private val firebaseAuth = FirebaseAuth.getInstance()

    interface UserFetchCallBack {
        fun onSuccess(uid: String)
        fun onFailure()

    }

    interface UnreadMessagesCallback {
        fun onSuccess(lastMessage: Pair<String, Int>)
        fun onFailure(error: Exception)
    }

    fun getCurrentUserUid() = firebaseAuth.currentUser?.uid


    suspend fun checkForAppUsers(contactsList: ArrayList<ContactsUserinfo>): ArrayList<ContactsUserinfo> {
        var index = 0;
        while (index < contactsList.size - 1) {
            val contact = contactsList[index]
            try {
                val uid = suspendCancellableCoroutine { continuation ->

                    var normalizedPhoneNumber =
                        HelperFunctions.normalisePhoneNumber(contact.phoneNumber!!)
                    if (normalizedPhoneNumber.length > 10) normalizedPhoneNumber.drop(3)
                    checkIfAppUser(normalizedPhoneNumber, object : UserFetchCallBack {
                        override fun onSuccess(uid: String) {
                            Log.d("Checking app user", "User Id for ${contact.name} = $uid")
                            continuation.resume(uid)
                            contact.isAppUser = true
                        }

                        override fun onFailure() {
                            Log.d("No contact found", "${contact.name} count = $index")
                            continuation.resume(null)
                        }
                    })
                }
                index++
                contact.uid = uid
            } catch (e: Exception) {
                // Handle exception, e.g., log or rethrow if necessary
            }
        }

        return contactsList
    }


    private fun checkIfAppUser(phoneNumber: String, callBack: UserFetchCallBack) {
        firestoreDb.collection("users").whereEqualTo("phoneNumber", phoneNumber).get()
            .addOnSuccessListener {
                if (it.isEmpty) callBack.onFailure()
                else callBack.onSuccess(it.documents[0].id)
            }

    }

    private val dummyContact1 = ContactsUserinfo(
        name = "Dummy 22",
        profileImage = R.drawable.hagrid_profile_pic1,
        uid = "JqN0AGrEPTSKmRkGCpe4",
        isAppUser = true
    )
    private val dummyContact2 = ContactsUserinfo(
        name = "dummy",
        profileImage = R.drawable.hagrid_profile_pic1,
        uid = "NNaMmhjWSBgFnysR4V895aNyxJF2",
        isAppUser = true
    )
    private val dummyContact3 = ContactsUserinfo(
        name = "dummy",
        profileImage = R.drawable.hagrid_profile_pic1,
        uid = "NNaMmhjWSBgFnysR4V895aNyxJF2",
        isAppUser = true
    )

    fun provideDummyData() = dummyRecentChat1
    fun provideDummyRecentChatsList(): List<RecentChatUserDataClass> {
        return listOf(
            dummyRecentChat1,
            dummyRecentChat2,
            dummyRecentChat3,
        )
    }

    suspend fun getAllContactsFromDevice(): ArrayList<ContactsUserinfo> {

        val contactsList = fetchContactsFromUserDevice()


//        firebaseClientRepository.checkForAppUsers(contactsList)

        return contactsList
    }

    private fun fetchContactsFromUserDevice(): ArrayList<ContactsUserinfo> {
        val contactsList = ArrayList<ContactsUserinfo>()
        val conResolver: ContentResolver = context.contentResolver
        val cursor = conResolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null, "display_name ASC"
        )

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                try {
                    val uid =
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    val name =
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
                        while (cursorInfo != null && cursorInfo.moveToNext()) phoneNumber =
                            cursorInfo.getString(
                                cursorInfo.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )

                        cursorInfo?.close()
                    }

                    val contactInfo = ContactsUserinfo(
                        uid = uid, name = name, phoneNumber = phoneNumber
                    )

                    if (phoneNumber != "") contactsList.add(contactInfo)

                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                }
            }
            cursor.close()
        }

        return contactsList
    }

    suspend fun getAllChatsFromFirestore(): List<RecentChatUserDataClass> {
        val usersUid = suspendCancellableCoroutine { continuation ->
            firestoreDb.collection("users").document(getCurrentUserUid()!!).get()
                .addOnSuccessListener { doc ->
                    val uidMapList = HashMap<String, String>()
                    if (doc.contains("chats")) {
                        val chatsMap = doc.get("chats") as Map<String, String>
                        for (chat in chatsMap) {
                            uidMapList[chat.key] = chat.value
                        }

                        continuation.resume(uidMapList)
                    }
                }.addOnFailureListener {
                    Log.d(TAG, "No previous Chats")
                    continuation.resume(null)
                }
        }

        val recentChats = ArrayList<RecentChatUserDataClass>()
        if (usersUid == null)
            return recentChats

        for (user in usersUid) {
            val lastMessage = suspendCancellableCoroutine { continuation ->
                firestoreDb.collection("chats").document(user.value).collection("messages")
                    .whereEqualTo("read", false)
                    .orderBy("time_stamp", Query.Direction.DESCENDING).get()
                    .addOnSuccessListener {
                        if (it.documents.size == 0) {
                            firestoreDb.collection("chats").document(user.value)
                                .collection("messages")
                                .orderBy("time_stamp", Query.Direction.DESCENDING).limit(1).get()
                                .addOnSuccessListener { querySnapshot ->
                                    continuation.resume(
                                        Pair(
                                            querySnapshot.documents[0].get("content") as String,
                                            0
                                        )
                                    )
                                }
                            Log.d(TAG, "No Chats Available")
                            return@addOnSuccessListener
                        }
                        continuation.resume(
                            Pair(
                                it.documents[0].get("content") as String,
                                if (getCurrentUserUid() == it.documents[0].get("sender") as String) 0 else it.documents.size
                            )
                        )
                    }
                    .addOnFailureListener {
                        Log.e(TAG, it.message.toString())
                        continuation.resume(null)
                    }
            }
            val recentChatUser =
                suspendCancellableCoroutine<RecentChatUserDataClass> { continuation ->
                    firestoreDb.collection("users").document(user.key).get()
                        .addOnSuccessListener { doc ->
                            if (lastMessage == null) {
                                continuation.resume(
                                    RecentChatUserDataClass(
                                        userName = doc.get("userName") as String,
                                        uid = user.key,
                                        lastMessageTime = "",
                                        messagesCount = 0,
                                        lastMessage = "",
                                    )
                                )
                                return@addOnSuccessListener
                            }
                            continuation.resume(
                                RecentChatUserDataClass(
                                    userName = doc.get("userName") as String,
                                    uid = user.key,
                                    lastMessageTime = "",
                                    messagesCount = lastMessage.second,
                                    lastMessage = lastMessage.first,
                                )
                            )
                        }

                }

            recentChats.add(recentChatUser)

        }


        return recentChats

    }


    private fun getUnreadMessages(roomId: String, callback: UnreadMessagesCallback) {
        firestoreDb.collection("chats").document(roomId).collection("messages")
            .whereEqualTo("read", false)
            .orderBy("time_stamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e(TAG, error.message.toString())
                    callback.onFailure(error)
                } else {
                    callback.onSuccess(
                        Pair(
                            value!!.documents[0].get("content") as String,
                            value.documents.size
                        )
                    )
                }
            }
    }

    private suspend fun getUnreadMessages(roomId: String): Pair<String, Int> {
        val lastMessage = suspendCancellableCoroutine { continuation ->
            firestoreDb.collection("chats").document(roomId).collection("messages")
                .whereEqualTo("read", false)
                .orderBy("time_stamp").get()
                .addOnSuccessListener {
                    continuation.resume(
                        Pair(
                            it.documents[0].get("content") as String,
                            it.documents.size
                        )
                    )
                }
        }

        return lastMessage
    }

    fun provideDummyContactsList(): List<ContactsUserinfo> {
        return listOf(
            dummyContact1, dummyContact2, dummyContact3
        )
    }
}


