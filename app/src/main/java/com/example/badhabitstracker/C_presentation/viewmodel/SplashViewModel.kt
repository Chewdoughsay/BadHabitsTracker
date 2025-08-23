package com.example.badhabitstracker.C_presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badhabitstracker.A_domain.usecase.authentification.IsUserLoggedInUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase
) : ViewModel() {

    // Navigation destination options
    enum class NavigationDestination {
        LOGIN,
        DASHBOARD
    }

    // Private mutable LiveData - only ViewModel can change this
    private val _navigationEvent = MutableLiveData<NavigationDestination>()
    // Public read-only LiveData - Fragment can observe this
    val navigationEvent: LiveData<NavigationDestination> = _navigationEvent

    // Loading state for showing/hiding loading spinner
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Starts the splash screen logic
     * Called by SplashFragment when it's ready
     */
    fun startSplashSequence() {
        viewModelScope.launch {
            // Show loading spinner
            _isLoading.value = true

            // Add your "cool" delay (3 seconds)
            delay(3000)

            // Check if user is logged in using our Use Case
            val loginCheckResult = isUserLoggedInUseCase()

            // Determine where to navigate based on result
            val destination = if (loginCheckResult.isSuccess && loginCheckResult.getOrDefault(false)) {
                NavigationDestination.DASHBOARD
            } else {
                NavigationDestination.LOGIN
            }

            // Hide loading spinner
            _isLoading.value = false

            // Tell Fragment where to navigate
            _navigationEvent.value = destination
        }
    }

    /**
     * Handles errors if login check fails
     * In production, you might want more sophisticated error handling
     */
    private fun handleLoginCheckError() {
        // If we can't determine login status, assume user needs to log in
        _isLoading.value = false
        _navigationEvent.value = NavigationDestination.LOGIN
    }
}