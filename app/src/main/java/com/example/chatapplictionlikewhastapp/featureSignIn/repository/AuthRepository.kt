package com.example.chatapplictionlikewhastapp.featureSignIn.repository

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class AuthRepository(private val activity: Activity) {
    private val firebaseAuth = FirebaseAuth.getInstance()

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
        credential: PhoneAuthCredential,
        callBack: SignInCallBack
    ) {
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
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