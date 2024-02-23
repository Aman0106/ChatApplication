package com.example.chatapplictionlikewhastapp.featureSignIn.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.chatapplictionlikewhastapp.MainActivity
import com.example.chatapplictionlikewhastapp.R
import com.example.chatapplictionlikewhastapp.databinding.FragmentSignInBinding
import com.example.chatapplictionlikewhastapp.featureSignIn.pojo.SignInState
import com.example.chatapplictionlikewhastapp.featureSignIn.repository.AuthRepository
import com.example.chatapplictionlikewhastapp.featureSignIn.viewModels.AuthViewModel
import com.example.chatapplictionlikewhastapp.featureSignIn.viewModels.AuthViewModelFactory
import com.example.chatapplictionlikewhastapp.featureSignIn.viewModels.observeOnce


class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    private val authViewModel: AuthViewModel by activityViewModels {
        AuthViewModelFactory(AuthRepository(requireActivity()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkIfUserAlreadySignedIn()
        verifyMobileNumber()
    }

    private fun checkIfUserAlreadySignedIn() {
        if (!authViewModel.checkIfUserAlreadySignedIn())
            return
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun verifyMobileNumber() {
        binding.btnNext.setOnClickListener {
            val countryCode = binding.countrycodePicker.selectedCountryCode
            val phoneNumber = "+$countryCode ${binding.edtPhoneNumber.text}"
            Log.d("num", phoneNumber)
            authViewModel.signInUserWithNumber(phoneNumber)

            val signInStateObserver = Observer<SignInState?> { state ->
                when (state) {
                    is SignInState.Failure -> {
                        Toast.makeText(activity, state.errorMsg, Toast.LENGTH_SHORT).show()
                    }

                    is SignInState.CodeSent -> {
                        Toast.makeText(activity, "Code Sent", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_signInFragment_to_otpVerificationFragment)
                    }

                    else -> {
                        Toast.makeText(activity, "Network error", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            authViewModel.signInState.observeOnce(viewLifecycleOwner, signInStateObserver)


        }
    }

}