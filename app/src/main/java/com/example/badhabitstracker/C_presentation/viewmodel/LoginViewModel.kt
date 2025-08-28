package com.example.badhabitstracker.C_presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badhabitstracker.A_domain.usecase.authentification.LoginUserUseCase
import com.example.badhabitstracker.A_domain.usecase.authentification.LoginParams
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    // Input fields that Fragment will update
    val emailText = MutableLiveData<String>()
    val passwordText = MutableLiveData<String>()

    // Loading state for showing progress
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Navigation events
    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    // ✨ THE MAGIC: Reactive button state! ✨
    // This automatically recalculates whenever email OR password changes
    val isLoginButtonEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        // Add both sources - when either changes, this recalculates
        addSource(emailText) { value = calculateButtonEnabled() }
        addSource(passwordText) { value = calculateButtonEnabled() }
        // Set initial value
        value = false
    }

    /**
     * Called when user clicks login button
     */
    fun onLoginClicked() {
        val email = emailText.value?.trim() ?: return
        val password = passwordText.value ?: return

        if (email.isBlank() || password.isBlank()) return

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            delay(3000)

            val result = loginUserUseCase(LoginParams(email, password))
            _isLoading.value = false

            if (result.isSuccess) {
                // Login successful - navigate to dashboard
                _navigationEvent.value = NavigationEvent.GO_TO_DASHBOARD
            } else {
                // Login failed - show error
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Login failed"
            }
        }
    }

    /**
     * Called when user clicks "Register" button
     */
    fun onRegisterClicked() {
        _navigationEvent.value = NavigationEvent.GO_TO_REGISTER
    }

    /**
     * Clear error message when user starts typing
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * The magic function - determines if login button should be enabled
     */
    private fun calculateButtonEnabled(): Boolean {
        val email = emailText.value?.trim()
        val password = passwordText.value
        return !email.isNullOrBlank() && !password.isNullOrBlank()
    }

    enum class NavigationEvent {
        GO_TO_DASHBOARD,
        GO_TO_REGISTER
    }
}