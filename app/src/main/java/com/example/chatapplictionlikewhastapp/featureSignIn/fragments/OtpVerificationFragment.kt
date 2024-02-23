package com.example.chatapplictionlikewhastapp.featureSignIn.fragments

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chatapplictionlikewhastapp.MainActivity
import com.example.chatapplictionlikewhastapp.R
import com.example.chatapplictionlikewhastapp.databinding.FragmentOtpVerificationBinding
import com.example.chatapplictionlikewhastapp.databinding.OtpFieldLayoutBinding
import com.example.chatapplictionlikewhastapp.featureSignIn.pojo.SignInState
import com.example.chatapplictionlikewhastapp.featureSignIn.repository.AuthRepository
import com.example.chatapplictionlikewhastapp.featureSignIn.viewModels.AuthViewModel
import com.example.chatapplictionlikewhastapp.featureSignIn.viewModels.AuthViewModelFactory

class OtpVerificationFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels {
        AuthViewModelFactory(AuthRepository(requireActivity()))
    }

    private lateinit var binding: FragmentOtpVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHeadingText()

        handleOtpInput()
        popToPreviousScreen()

        observerSignInState()
    }

    private fun observerSignInState() {
        authViewModel.signInState.observe(viewLifecycleOwner) { state ->
            hideProgressBar()
            when (state) {
                is SignInState.Success -> {
                    Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }

                is SignInState.Failure -> {
                    Toast.makeText(activity, state.errorMsg, Toast.LENGTH_SHORT).show()
                }

                else -> Toast.makeText(activity, "Unknown Error", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun getInput(): String {
        var code: String = binding.edtOtp1.edtOtp.text.toString()
        code += binding.edtOtp2.edtOtp.text.toString()
        code += binding.edtOtp3.edtOtp.text.toString()
        code += binding.edtOtp4.edtOtp.text.toString()
        code += binding.edtOtp5.edtOtp.text.toString()
        code += binding.edtOtp6.edtOtp.text.toString()

        return code
    }


    private fun setHeadingText() {
        binding.tvUsernumberLabel.text =
            "Enter the 6 digit number sent to " + authViewModel.getPhoneNumber()
    }

    private fun handleOtpInput() {

        binding.edtOtp1.edtOtp.requestFocus()
        val methodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        methodManager.showSoftInput(binding.edtOtp1.edtOtp, InputMethodManager.SHOW_IMPLICIT)
        highlightEditingText(binding.edtOtp1.viewUnderLine, 5, R.color.dark_primary_variant)

        moveEditTextFromTo(null, binding.edtOtp1, binding.edtOtp2)
        moveEditTextFromTo(binding.edtOtp1, binding.edtOtp2, binding.edtOtp3)
        moveEditTextFromTo(binding.edtOtp2, binding.edtOtp3, binding.edtOtp4)
        moveEditTextFromTo(binding.edtOtp3, binding.edtOtp4, binding.edtOtp5)
        moveEditTextFromTo(binding.edtOtp4, binding.edtOtp5, binding.edtOtp6)
        moveEditTextFromTo(binding.edtOtp5, binding.edtOtp6, null)

        binding.btnNext.setOnClickListener {
            if (getInput().length < 6)
                return@setOnClickListener
            authViewModel.signInAfterVerifyingOtp(getInput())
            showProgressBar()
        }
    }

    private fun highlightEditingText(view: View, newHeight: Int, color: Int) {

        val pixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newHeight.toFloat(),
            resources.displayMetrics
        ).toInt()

        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        layoutParams.height = pixels
        view.layoutParams = layoutParams

        view.background = ColorDrawable(
            ContextCompat.getColor(
                requireActivity(),
                color
            )
        )
    }

    private fun popToPreviousScreen() {
        val backPressCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressCallBack)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun moveEditTextFromTo(
        prev: OtpFieldLayoutBinding?,
        curr: OtpFieldLayoutBinding,
        next: OtpFieldLayoutBinding?
    ) {

        curr.edtOtp.doOnTextChanged { text, start, before, count ->
            when (text.toString()) {
                "" -> {
                    if (prev != null) {
                        prev.edtOtp.requestFocus()
                        prev.edtOtp.setSelection(prev.edtOtp.text.length)
                        highlightEditingText(curr.viewUnderLine, 3, R.color.dark_text_secondary)
                        highlightEditingText(prev.viewUnderLine, 5, R.color.dark_primary_variant)

                    }
                }

                else -> {

                    if (next != null) {
                        next.edtOtp.requestFocus()
                        next.edtOtp.setSelection(next.edtOtp.text.length)
                        highlightEditingText(curr.viewUnderLine, 3, R.color.dark_primary)
                        highlightEditingText(next.viewUnderLine, 5, R.color.dark_primary_variant)
                    }
                }
            }
        }
    }

    private fun showProgressBar() {
        binding.btnNext.text = ""
        binding.btnNext.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun hideProgressBar() {
        binding.btnNext.text = "Next"
        binding.btnNext.isEnabled = true
        binding.progressBar.visibility = View.INVISIBLE
    }

}