package com.example.chatapplictionlikewhastapp.featureSignIn.viewModels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.chatapplictionlikewhastapp.featureSignIn.pojo.SignInState
import com.example.chatapplictionlikewhastapp.featureSignIn.repository.AuthRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class AuthViewModel(private val authRepository: AuthRepository): ViewModel() {


    private val _signInState = MutableLiveData<SignInState?>()
    val signInState: LiveData<SignInState?> get() = _signInState

    private lateinit var phoneNumber: String

    private lateinit var  verificationId: String

    fun checkIfUserAlreadySignedIn() = authRepository.isUserSignedIn()

    fun signInUserWithNumber(phoneNumber: String) {
        this.phoneNumber = phoneNumber

        authRepository.initiatePhoneAuthentication(phoneNumber, object : AuthRepository.AuthCallback {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                _signInState.postValue(SignInState.Success)
            }

            override fun onVerificationFailed(exception: FirebaseException) {
                _signInState.postValue(SignInState.Failure(exception.message.toString()))
                resetSignInState()
            }

            override fun onCodeSent(verificationId: String) {

                this@AuthViewModel.verificationId = verificationId
                _signInState.postValue(SignInState.CodeSent)
                resetSignInState()
            }
        })

    }

    fun signInAfterVerifyingOtp(code: String) {
        if(code.length < 6)
            return
        val credential = PhoneAuthProvider.getCredential(verificationId, code)

        authRepository.signInAfterVerifyingCode(phoneNumber,credential, object : AuthRepository.SignInCallBack {
            override fun onSuccessListener(authResult: AuthResult) {
                _signInState.postValue(SignInState.Success)
            }

            override fun onFailureListener(exception: Exception) {
                _signInState.postValue(SignInState.Failure(exception.message.toString()))
            }
        })
    }

    fun resetSignInState() {
//        _signInState.value   = null
    }

    fun getPhoneNumber():String = phoneNumber
}


fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
    observe(owner, object : Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}
