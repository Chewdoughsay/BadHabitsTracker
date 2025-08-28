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
import com.example.badhabitstracker.C_presentation.viewmodel.RegisterViewModel
import com.example.badhabitstracker.R
import com.example.badhabitstracker.D_data_injection.appContainer
import com.google.android.material.textfield.TextInputEditText

class RegisterFragment : Fragment() {

    // ✨ DEPENDENCY INJECTION MAGIC ✨
    // Automatically creates RegisterViewModel with all its dependencies
    private val viewModel: RegisterViewModel by viewModels {
        requireContext().appContainer.viewModelFactory
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up all the interactive components
        setupTextFields(view)
        setupButtons(view)
        setupObservers(view)
    }

    /**
     * Connect all text input fields to ViewModel using TextWatchers
     * Real-time updates enable reactive button states and validation
     */
    private fun setupTextFields(view: View) {
        val etName = view.findViewById<TextInputEditText>(R.id.etName)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
        val etConfirmPassword = view.findViewById<TextInputEditText>(R.id.etConfirmPassword)

        // Name field - updates ViewModel and clears errors on text change
        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.nameText.value = s?.toString() ?: ""
                viewModel.clearError() // Clear validation errors when user types
            }
        })

        // Email field - updates ViewModel and clears errors on text change
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.emailText.value = s?.toString() ?: ""
                viewModel.clearError()
            }
        })

        // Password field - updates ViewModel and clears errors on text change
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.passwordText.value = s?.toString() ?: ""
                viewModel.clearError()
            }
        })

        // Confirm Password field - updates ViewModel and clears errors on text change
        etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.confirmPasswordText.value = s?.toString() ?: ""
                viewModel.clearError()
            }
        })
    }

    /**
     * Set up button click listeners
     */
    private fun setupButtons(view: View) {
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val tvLogin = view.findViewById<TextView>(R.id.tvLogin)

        // Register button - triggers registration process
        btnRegister.setOnClickListener {
            viewModel.onRegisterClicked()
        }

        // "Already have an account" link - navigates back to login
        tvLogin.setOnClickListener {
            viewModel.onLoginClicked()
        }
    }

    /**
     * Set up observers for ViewModel state changes
     * This is where the UI reacts to ViewModel updates
     */
    private fun setupObservers(view: View) {
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvError = view.findViewById<TextView>(R.id.tvError)

        // ✨ REACTIVE BUTTON STATE ✨
        // Button automatically enables/disables based on form validation
        viewModel.isRegisterButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            btnRegister.isEnabled = isEnabled
            // Visual feedback: button color changes based on enabled state
        }

        // Loading state - show/hide spinner, disable button during registration
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                btnRegister.text = "" // Hide text, show only spinner
                btnRegister.isEnabled = false
            } else {
                progressBar.visibility = View.GONE
                btnRegister.text = getString(R.string.create_account)
                // Re-enable button based on form validation
                btnRegister.isEnabled = viewModel.isRegisterButtonEnabled.value == true
            }
        }

        // Error message display - show validation or registration errors
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error.isNullOrBlank()) {
                tvError.visibility = View.GONE
            } else {
                tvError.text = error
                tvError.visibility = View.VISIBLE
            }
        }

        // Navigation events - respond to ViewModel navigation requests
        viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                RegisterViewModel.NavigationEvent.GO_TO_DASHBOARD -> {
                    // Registration successful - navigate to main app
                    findNavController().navigate(R.id.action_register_to_dashboard)
                }
                RegisterViewModel.NavigationEvent.GO_TO_LOGIN -> {
                    // User wants to go back to login - pop back stack
                    findNavController().navigate(R.id.action_register_to_login)
                }
            }
        }
    }
}