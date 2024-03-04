package com.example.chatapplictionlikewhastapp.featureHome.repository

import android.util.Log
import com.example.chatapplictionlikewhastapp.featureHome.pojo.MessageDataClass
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val TAG = "INSIDE_FIRESTORE_CHAT_REPOSITORY"

class FirestoreChatRepository {
    private val firestoreDb = Firebase.firestore
    private val firebaseAuth = FirebaseAuth.getInstance()

    interface RoomFetchCallBack {
        fun onSuccess(roomId: String)
        fun onFailure()
    }

    interface SnapshotCallBack {
        fun onSuccess(messages: List<MessageDataClass>)
        fun onFailure(exception: java.lang.Exception)
    }

    interface MessageSentCallback {
        fun onSuccess(roomId: String)
        fun onFailure(exception: java.lang.Exception)
    }

    fun setRoomId(receiverUid: String, callBack: RoomFetchCallBack) {
        firestoreDb.collection("users").document(firebaseAuth.currentUser?.uid.toString()).get()
            .addOnSuccessListener {
                if (it.contains("chats")) {
                    val chatMap = it.get("chats") as HashMap<String, String>
                    if (chatMap.containsKey(receiverUid)) {
                        callBack.onSuccess(chatMap[receiverUid]!!)
                    } else callBack.onFailure()
                }
            }
    }


    fun listenToMessageUpdates(roomId: String, callBack: SnapshotCallBack) {

        //TODO cache all the fields to prevent unnecessary network calls
        firestoreDb.collection("chats").document(roomId).collection("messages")
            .orderBy("time_stamp").addSnapshotListener { value, error ->
                if (error != null) {
                    callBack.onFailure(error)
                    return@addSnapshotListener
                }
                val messages = ArrayList<MessageDataClass>()
                for (doc in value!!) {
                    val read = doc.get("read") as Boolean
                    if (!read && firebaseAuth.currentUser!!.uid != doc.get("sender") as String) {
                        val messageDocRef =
                            firestoreDb.collection("chats").document(roomId).collection("messages")
                                .document(doc.id)

                        //There is major bug that does write operations again and again if there are multiple unread messages
                        //so 2 unread messages resulted in 22 writes and 48 unread messages resulted in 1.2k writes
                        val updateData = mapOf("read" to true)
                        messageDocRef.update(updateData)
                    }
                    messages.add(
                        MessageDataClass(
                            message = doc.get("content") as String,
                            senderUid = doc.get("sender") as String,
                            timeStamp = doc.get("time_stamp") as Timestamp,
                            read = if (firebaseAuth.currentUser!!.uid == doc.get("sender") as String) read else true
                        )
                    )
                }

                callBack.onSuccess(messages)
            }
    }

    fun startANewChat(receiverUid: String, message: String, callback: MessageSentCallback) {
        val docRef = firestoreDb.collection("chats").document()
        val senderUid = firebaseAuth.currentUser?.uid.toString()
        Log.d("Inside Firebase Repository", "To new user")

        val messageId = docRef.collection("messages").document()
        messageId.set(
            mapOf(
                "content" to message,
                "sender" to senderUid,
                "time_stamp" to Timestamp.now(),
                "read" to false
            )
        ).addOnSuccessListener {
            callback.onSuccess(docRef.id)
        }.addOnFailureListener {
            callback.onFailure(it)
        }
        // Store the chat room_id to the sender's chats map
        firestoreDb.collection("users").document(senderUid).update(
            "chats.$receiverUid", docRef.id
        )

        // Store the chat room_id to the receiver's chats map
        firestoreDb.collection("users").document(receiverUid).update(
            "chats.$senderUid", docRef.id

        )

        Log.d(TAG, "New Chat")
    }

    fun sendMessageToRoom(
        message: String,
        roomId: String,
    ) {
        Log.d("Inside Firebase Repository", "Send Message")
        firestoreDb.collection("chats").document(roomId).collection("messages").document().set(
            mapOf(
                "content" to message,
                "sender" to firebaseAuth.currentUser?.uid,
                "time_stamp" to Timestamp.now(),
                "read" to false
            )
        )

    }

}