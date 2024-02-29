package com.example.chatapplictionlikewhastapp.featureHome.repository

import android.util.Log
import com.example.chatapplictionlikewhastapp.featureHome.pojo.ContactsUserinfo
import com.example.chatapplictionlikewhastapp.utils.HelperFunctions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebaseClientRepository {

    private val firestoreDb = Firebase.firestore
    private val firebaseAuth = FirebaseAuth.getInstance()

    interface UserFetchCallBack {
        fun onSuccess(uid: String)
        fun onFailure()

    }

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
                            continuation.resume("")
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

    fun sendMessageToUser(receiverUid: String, message: String) {
        val senderUid = firebaseAuth.currentUser!!.uid
        val currentUserDoc = firestoreDb.collection("users").document(senderUid).get()
            .addOnSuccessListener {
                if (it.contains("chats")) {
                    val chatMap = it.get("chats") as HashMap<String, String>
                    if (chatMap.containsKey(receiverUid)) {
                        sendMessage(senderUid, message, chatMap[receiverUid] as String)
                        return@addOnSuccessListener
                    }
                }
                startANewChat(receiverUid, message)
            }


    }

    private fun sendMessage(senderUid: String, message: String, roomId: String) {
        Log.d("Inside Firebase Repository", "Send Message")
        firestoreDb.collection("chats").document(roomId).collection("messages").document().set(
            mapOf(
                "content" to message,
                "sender" to senderUid,
                "time_stamp" to FieldValue.serverTimestamp()
            )
        )
            .addOnSuccessListener {

            }
    }

    private fun startANewChat(receiverUid: String, message: String) {
        val docRef = firestoreDb.collection("chats").document()
        val senderUid = firebaseAuth.currentUser?.uid.toString()
        Log.d("Inside Firebase Repository", "To new user")

        val messageId = docRef.collection("messages").document()
        messageId.set(
            mapOf(
                "content" to message,
                "sender" to senderUid,
                "time_stamp" to FieldValue.serverTimestamp()
            )
        )

        // Store the chat room_id to the sender's chats map
        firestoreDb.collection("users").document(senderUid).update(
            mapOf(
                "chats" to mapOf(receiverUid to docRef.id)
            )
        )

        // Store the chat room_id to the receiver's chats map
        firestoreDb.collection("users").document(receiverUid).update(
            mapOf(
                "chats" to mapOf(senderUid to docRef.id)
            )
        )
    }

    private fun checkIfAppUser(phoneNumber: String, callBack: UserFetchCallBack) {
        firestoreDb.collection("users").whereEqualTo("phoneNumber", phoneNumber).get()
            .addOnSuccessListener {
                if (it.isEmpty) callBack.onFailure()
                else callBack.onSuccess(it.documents[0].id)
            }

    }
}
