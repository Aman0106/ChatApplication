package com.example.chatapplictionlikewhastapp.featureHome.repository

import android.util.Log
import com.example.chatapplictionlikewhastapp.featureHome.pojo.ContactsUserinfo
import com.example.chatapplictionlikewhastapp.featureHome.pojo.MessageDataClass
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





    private fun checkIfAppUser(phoneNumber: String, callBack: UserFetchCallBack) {
        firestoreDb.collection("users").whereEqualTo("phoneNumber", phoneNumber).get()
            .addOnSuccessListener {
                if (it.isEmpty) callBack.onFailure()
                else callBack.onSuccess(it.documents[0].id)
            }

    }
}
