package com.example.chatapplictionlikewhastapp.featureHome.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapplictionlikewhastapp.featureHome.pojo.MessageDataClass
import com.example.chatapplictionlikewhastapp.featureHome.repository.ChatRepository
import com.example.chatapplictionlikewhastapp.featureHome.repository.FirestoreChatRepository
import java.lang.Exception

private const val TAG  = "INSIDE_CHAT_VIEWMODEL"

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val firestoreChatRepository: FirestoreChatRepository
) : ViewModel() {

    private val _messageList = MutableLiveData<List<MessageDataClass>>()
    val messageList: LiveData<List<MessageDataClass>> get() = _messageList

    private val _roomId = MutableLiveData<String>("")
    val roomId: LiveData<String> get() = _roomId

    fun getAllMessages(room: String) {
//        _messageList.postValue(chatRepository.fetchDummyMessagesList())
        firestoreChatRepository.listenToMessageUpdates(room, object : FirestoreChatRepository.SnapshotCallBack {
            override fun onSuccess(messages: List<MessageDataClass>) {
                _messageList.postValue(messages)
            }

            override fun onFailure(exception: Exception) {
                Log.d(TAG, exception.message.toString())
            }
        })
    }

    fun setRoomId(receiverUid: String) {
        firestoreChatRepository.setRoomId(receiverUid, object : FirestoreChatRepository.RoomFetchCallBack {
            override fun onSuccess(roomId: String) {
                _roomId.postValue(roomId)
                getAllMessages(roomId)
            }

            override fun onFailure() {

            }
        })
    }

    fun sendMessageToUser(uid: String, message: String) {
        if (roomId.value == "") {
            firestoreChatRepository.startANewChat(uid, message, object : FirestoreChatRepository.MessageSentCallback {
                override fun onSuccess(roomId: String) {
                    getAllMessages(roomId)
                }

                override fun onFailure(exception: Exception) {
                    Log.d(TAG, exception.message.toString())
                }
            })
        } else {
            firestoreChatRepository.sendMessageToRoom(message, roomId.value!!)
        }
    }
}