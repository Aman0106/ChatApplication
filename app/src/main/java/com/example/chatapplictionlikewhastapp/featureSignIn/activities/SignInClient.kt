package com.example.chatapplictionlikewhastapp.featureSignIn.activities

import android.app.Activity
import com.google.firebase.Firebase
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit

class SignInClient( private val activity: Activity, private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
    private val firebaseAuth = Firebase.auth
    fun signUserWithNumber(number: String) {

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(number)
            .setTimeout(20L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}