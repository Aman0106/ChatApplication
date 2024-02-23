package com.example.chatapplictionlikewhastapp.featureSignIn.pojo

sealed class SignInState {
    data object Success : SignInState()
    data object CodeSent: SignInState()
    data class Failure(val errorMsg: String): SignInState()
}