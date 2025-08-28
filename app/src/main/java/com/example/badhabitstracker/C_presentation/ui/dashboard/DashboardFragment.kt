package com.example.badhabitstracker.C_presentation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.badhabitstracker.C_presentation.viewmodel.DashboardViewModel
import com.example.badhabitstracker.R
import com.example.badhabitstracker.D_data_injection.appContainer

/**
 * Dashboard Fragment - Main screen after login/registration
 * Currently minimal implementation with logout functionality for testing
 * TODO: Add habit tracking, statistics, achievements, etc.
 */
class DashboardFragment : Fragment() {

    // ✨ DEPENDENCY INJECTION MAGIC ✨
    // Automatically creates DashboardViewModel with all dependencies
    private val viewModel: DashboardViewModel by viewModels {
        requireContext().appContainer.viewModelFactory
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButtons(view)
        setupObservers(view)
    }

    /**
     * Set up button click listeners
     */
    private fun setupButtons(view: View) {
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Logout button - triggers session cleanup and navigation
        btnLogout.setOnClickListener {
            viewModel.onLogoutClicked()
        }
    }

    /**
     * Set up observers for ViewModel state changes
     * This is where the UI reacts to logout progress
     */
    private fun setupObservers(view: View) {
        val tvWelcome = view.findViewById<TextView>(R.id.tvDashboardWelcome)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarLogout)
        val tvError = view.findViewById<TextView>(R.id.tvError)

        // ✨ REACTIVE WELCOME MESSAGE ✨
        // Updates automatically when user data loads
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            tvWelcome.text = viewModel.getWelcomeMessage()
        }

        // Loading state during logout - disable button, show spinner
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                btnLogout.text = "" // Hide text, show spinner
                btnLogout.isEnabled = false
            } else {
                progressBar.visibility = View.GONE
                btnLogout.text = getString(R.string.logout)
                btnLogout.isEnabled = true
            }
        }

        // Error messages (rare, but good to handle)
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error.isNullOrBlank()) {
                tvError.visibility = View.GONE
            } else {
                tvError.text = error
                tvError.visibility = View.VISIBLE
            }
        }

        // Navigation events - respond to logout completion
        viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                DashboardViewModel.NavigationEvent.GO_TO_LOGIN -> {
                    // Logout complete - clear back stack and go to login
                    findNavController().navigate(
                        R.id.action_dashboard_to_login,
                        null,
                        androidx.navigation.navOptions {
                            // Clear entire back stack so user can't press back to dashboard
                            popUpTo(R.id.nav_graph) { inclusive = true }
                        }
                    )
                }
            }
        }
    }
}