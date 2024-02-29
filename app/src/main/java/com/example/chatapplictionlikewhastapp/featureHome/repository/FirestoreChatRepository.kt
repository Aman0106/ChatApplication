package com.example.chatapplictionlikewhastapp.featureHome.repository

import android.util.Log
import com.example.chatapplictionlikewhastapp.featureHome.pojo.MessageDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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

//    fun sendMessageToUser(
//        receiverUid: String,
//        message: String,
//        roomId: String,
//        callback: MessageSentCallback
//    ) {
//        val senderUid = firebaseAuth.currentUser!!.uid
//
//        if (roomId == "") {
//            startANewChat(receiverUid, message, callback)
//        } else
//            sendMessageToRoom(senderUid, message, roomId)
//    }

    fun setRoomId(receiverUid: String, callBack: RoomFetchCallBack) {
        firestoreDb.collection("users").document(firebaseAuth.currentUser?.uid.toString())
            .get().addOnSuccessListener {
                if (it.contains("chats")) {
                    val chatMap = it.get("chats") as HashMap<String, String>
                    if (chatMap.containsKey(receiverUid)) {
                        callBack.onSuccess(chatMap[receiverUid]!!)
                    } else
                        callBack.onFailure()
                }
            }
    }

    fun listenToMessageUpdates(roomId: String, callBack: SnapshotCallBack) {
        firestoreDb.collection("chats").document(roomId).collection("messages")
            .orderBy("time_stamp")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    callBack.onFailure(error)
                    return@addSnapshotListener
                }
                val messages = ArrayList<MessageDataClass>()
                for (doc in value!!) {
                    messages.add(
                        MessageDataClass(
                            message = doc.get("content") as String,
                            senderUid = doc.get("sender") as String,
                            timeStamp = doc.get("time_stamp").toString()
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
                "time_stamp" to FieldValue.serverTimestamp()
            )
        ).addOnSuccessListener {
            callback.onSuccess(docRef.id)
        }.addOnFailureListener {
            callback.onFailure(it)
        }
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

    fun sendMessageToRoom(
        message: String,
        roomId: String,
    ) {
        Log.d("Inside Firebase Repository", "Send Message")
        firestoreDb.collection("chats").document(roomId).collection("messages").document().set(
            mapOf(
                "content" to message,
                "sender" to firebaseAuth.currentUser?.uid,
                "time_stamp" to FieldValue.serverTimestamp()
            )
        )

    }

}