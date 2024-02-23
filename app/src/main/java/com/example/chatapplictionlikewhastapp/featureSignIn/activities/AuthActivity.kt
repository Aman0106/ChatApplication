package com.example.chatapplictionlikewhastapp.featureSignIn.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.lifecycle.ViewModelProvider
import com.example.chatapplictionlikewhastapp.R
import com.example.chatapplictionlikewhastapp.databinding.ActivityAuthBinding
import com.example.chatapplictionlikewhastapp.featureSignIn.viewModels.AuthViewModel

class AuthActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}