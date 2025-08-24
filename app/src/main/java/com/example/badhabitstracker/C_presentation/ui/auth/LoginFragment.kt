package com.example.badhabitstracker.C_presentation.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.badhabitstracker.C_presentation.viewmodel.LoginViewModel
import com.example.badhabitstracker.R
import com.example.badhabitstracker.D_data_injection.appContainer
import com.google.android.material.textfield.TextInputEditText

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels {
        requireContext().appContainer.viewModelFactory
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextFields(view)
        setupButtons(view)
        setupObservers(view)
    }

    private fun setupTextFields(view: View) {
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)

        // Connect text fields to ViewModel
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.emailText.value = s?.toString() ?: ""
                viewModel.clearError() // Clear error when user types
            }
        })

        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.passwordText.value = s?.toString() ?: ""
                viewModel.clearError() // Clear error when user types
            }
        })
    }

    private fun setupButtons(view: View) {
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val tvRegister = view.findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            viewModel.onLoginClicked()
        }

        tvRegister.setOnClickListener {
            viewModel.onRegisterClicked()
        }
    }

    private fun setupObservers(view: View) {
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvError = view.findViewById<TextView>(R.id.tvError)

        // ✨ THE MAGIC IN ACTION ✨
        // Button automatically enables/disables based on text fields!
        viewModel.isLoginButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            btnLogin.isEnabled = isEnabled
        }

        // Loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnLogin.text = if (isLoading) "" else "Log In"
            btnLogin.isEnabled = !isLoading && (viewModel.isLoginButtonEnabled.value == true)
        }

        // Error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error.isNullOrBlank()) {
                tvError.visibility = View.GONE
            } else {
                tvError.text = error
                tvError.visibility = View.VISIBLE
            }
        }

        // Navigation
        viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                LoginViewModel.NavigationEvent.GO_TO_DASHBOARD -> {
                    findNavController().navigate(R.id.action_login_to_dashboard)
                }
                LoginViewModel.NavigationEvent.GO_TO_REGISTER -> {
                    findNavController().navigate(R.id.action_login_to_register)
                }
            }
        }
    }
}