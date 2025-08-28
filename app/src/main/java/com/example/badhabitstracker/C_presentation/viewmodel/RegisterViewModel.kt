package com.example.badhabitstracker.C_presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badhabitstracker.A_domain.usecase.authentification.RegisterUserUseCase
import com.example.badhabitstracker.A_domain.usecase.authentification.RegisterParams
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    // Input fields that Fragment will update via TextWatchers
    val nameText = MutableLiveData<String>()
    val emailText = MutableLiveData<String>()
    val passwordText = MutableLiveData<String>()
    val confirmPasswordText = MutableLiveData<String>()

    // Loading state for showing progress spinner
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error messages for validation and registration failures
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Navigation events to tell Fragment where to navigate
    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    // ✨ REACTIVE MAGIC: Button automatically enables/disables based on ALL input fields ✨
    // This recalculates whenever ANY of the 4 fields change
    val isRegisterButtonEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        // Monitor all 4 input fields - when any changes, button state recalculates
        addSource(nameText) { value = calculateButtonEnabled() }
        addSource(emailText) { value = calculateButtonEnabled() }
        addSource(passwordText) { value = calculateButtonEnabled() }
        addSource(confirmPasswordText) { value = calculateButtonEnabled() }
        // Initial state is disabled
        value = false
    }

    /**
     * Called when user taps the "Create Account" button
     * Validates input then calls RegisterUserUseCase
     */
    fun onRegisterClicked() {
        val name = nameText.value?.trim() ?: return
        val email = emailText.value?.trim() ?: return
        val password = passwordText.value ?: return
        val confirmPassword = confirmPasswordText.value ?: return

        // Client-side validation before sending to Use Case
        if (!isInputValid(name, email, password, confirmPassword)) return

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            delay(3000)

            val result = registerUserUseCase(RegisterParams(
                name = name,
                email = email,
                password = password,
                confirmPassword = confirmPassword
            ))

            _isLoading.value = false

            if (result.isSuccess) {
                // Registration successful - navigate to dashboard
                // User is automatically logged in by RegisterUserUseCase
                _navigationEvent.value = NavigationEvent.GO_TO_DASHBOARD
            } else {
                // Registration failed - show error message
                val exception = result.exceptionOrNull()
                _errorMessage.value = exception?.message ?: "Registration failed"
            }
        }
    }

    /**
     * Called when user taps "Already have an account? Log In"
     */
    fun onLoginClicked() {
        // Navigate back to login screen
        _navigationEvent.value = NavigationEvent.GO_TO_LOGIN
    }

    /**
     * Clear error message when user starts typing in any field
     * Called by Fragment's TextWatchers
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Determines if register button should be enabled
     * All fields must be non-empty and passwords must match
     */
    private fun calculateButtonEnabled(): Boolean {
        val name = nameText.value?.trim()
        val email = emailText.value?.trim()
        val password = passwordText.value
        val confirmPassword = confirmPasswordText.value

        // All fields must be filled
        if (name.isNullOrBlank() || email.isNullOrBlank() ||
            password.isNullOrBlank() || confirmPassword.isNullOrBlank()) {
            return false
        }

        // Passwords must match for button to be enabled
        return password == confirmPassword
    }

    /**
     * Validates input before sending to Use Case
     * Shows specific error messages for validation failures
     */
    private fun isInputValid(name: String, email: String, password: String, confirmPassword: String): Boolean {
        // Name validation
        if (name.length < 2) {
            _errorMessage.value = "Name must be at least 2 characters"
            return false
        }

        // Email validation (basic)
        if (!email.contains("@") || !email.contains(".")) {
            _errorMessage.value = "Please enter a valid email address"
            return false
        }

        // Password validation
        if (password.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters"
            return false
        }

        // Confirm password validation
        if (password != confirmPassword) {
            _errorMessage.value = "Passwords do not match"
            return false
        }

        return true
    }

    /**
     * Navigation events that Fragment listens to
     */
    enum class NavigationEvent {
        GO_TO_DASHBOARD,  // After successful registration
        GO_TO_LOGIN       // When user wants to go back to login
    }
}