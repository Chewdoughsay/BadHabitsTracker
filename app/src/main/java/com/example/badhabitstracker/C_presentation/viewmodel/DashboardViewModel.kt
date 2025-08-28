package com.example.badhabitstracker.C_presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badhabitstracker.A_domain.model.User
import com.example.badhabitstracker.A_domain.usecase.authentification.GetCurrentUserUseCase
import com.example.badhabitstracker.A_domain.usecase.authentification.LogoutUserUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUserUseCase: LogoutUserUseCase
) : ViewModel() {

    // Current user information for welcome message
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    // Loading state for logout operation
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Navigation event when logout is complete
    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    // Error messages (if logout fails)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        // Load current user when ViewModel is created
        loadCurrentUser()
    }

    /**
     * Load current user information for welcome message
     * Shows user's name or email in dashboard header
     */
    private fun loadCurrentUser() {
        viewModelScope.launch {
            val result = getCurrentUserUseCase()
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
            } else {
                // If we can't get current user, something is wrong - logout
                _navigationEvent.value = NavigationEvent.GO_TO_LOGIN
            }
        }
    }

    /**
     * Called when user taps logout button
     * Clears session and navigates back to login
     */
    fun onLogoutClicked() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            delay(3000)
            // LogoutUserUseCase handles clearing session, cache, etc.
            val result = logoutUserUseCase()
            _isLoading.value = false

            if (result.isSuccess) {
                // Logout successful - navigate to login screen
                _navigationEvent.value = NavigationEvent.GO_TO_LOGIN
            } else {
                // Logout failed (rare) - show error but still navigate
                _errorMessage.value = "Logout failed"
                // Navigate anyway since session is probably corrupted
                _navigationEvent.value = NavigationEvent.GO_TO_LOGIN
            }
        }
    }

    /**
     * Get welcome message based on current user
     * Falls back to generic message if user data unavailable
     */
    fun getWelcomeMessage(): String {
        val user = _currentUser.value
        return if (user != null) {
            "Welcome back, ${user.getDisplayName()}! 👋"
        } else {
            "Welcome to BadHabits Tracker! 👋"
        }
    }

    /**
     * Navigation events that Fragment listens to
     */
    enum class NavigationEvent {
        GO_TO_LOGIN  // After successful logout
    }
}