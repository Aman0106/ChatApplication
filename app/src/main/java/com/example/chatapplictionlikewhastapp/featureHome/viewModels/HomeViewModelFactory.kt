package com.example.chatapplictionlikewhastapp.featureHome.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chatapplictionlikewhastapp.featureHome.repository.UsersRepository

class HomeViewModelFactory(private val usersRepository: UsersRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(usersRepository) as T
    }
}