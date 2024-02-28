package com.example.chatapplictionlikewhastapp.featureSignIn.repository

import android.app.Activity
import com.example.chatapplictionlikewhastapp.utils.HelperFunctions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class AuthRepository(private val activity: Activity) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore = Firebase.firestore

    interface AuthCallback {
        fun onVerificationCompleted(credential: PhoneAuthCredential)
        fun onVerificationFailed(exception: FirebaseException)
        fun onCodeSent(verificationId: String)
    }

    interface SignInCallBack {
        fun onSuccessListener(authResult: AuthResult)
        fun onFailureListener(exception: Exception)
    }

    fun initiatePhoneAuthentication(
        phoneNumber: String,
        callback: AuthCallback
    ) {

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                callback.onVerificationCompleted(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                callback.onVerificationFailed(p0)
            }

            override fun onCodeSent(
                verificationId: String,
                p1: PhoneAuthProvider.ForceResendingToken
            ) {
                callback.onCodeSent(verificationId)
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(20L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInAfterVerifyingCode(
        phoneNumber: String,
        credential: PhoneAuthCredential,
        callBack: SignInCallBack
    ) {
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                val docRef = firebaseFirestore.collection("users").document(firebaseAuth.currentUser!!.uid)
                val normalizedNumber = HelperFunctions.normalisePhoneNumber(phoneNumber)

                val countryCode = phoneNumber.substring(0, 3)
                val userInfo = mapOf(
                    "countryCode" to countryCode,
                    "phoneNumber" to normalizedNumber,
                    "profileImage" to "",
                    "userName" to "New User"
                )

                docRef.set(userInfo)
                callBack.onSuccessListener(it)
            }
            .addOnFailureListener {
                callBack.onFailureListener(it)
            }
    }

    fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

}