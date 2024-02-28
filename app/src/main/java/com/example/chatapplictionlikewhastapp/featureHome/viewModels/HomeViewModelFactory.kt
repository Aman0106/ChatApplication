package com.example.chatapplictionlikewhastapp.featureHome.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chatapplictionlikewhastapp.featureHome.repository.FirebaseClientRepository
import com.example.chatapplictionlikewhastapp.featureHome.repository.UsersRepository

class HomeViewModelFactory(private val usersRepository: UsersRepository, private val firebaseClientRepository: FirebaseClientRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(usersRepository, firebaseClientRepository) as T
    }
}