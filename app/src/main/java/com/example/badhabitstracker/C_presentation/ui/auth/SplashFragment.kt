package com.example.badhabitstracker.C_presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.badhabitstracker.C_presentation.viewmodel.SplashViewModel
import com.example.badhabitstracker.R
import com.example.badhabitstracker.D_data_injection.appContainer

class SplashFragment : Fragment() {

    // ✨ THE MAGIC LINE ✨
    // This automatically creates SplashViewModel with all dependencies!
    private val viewModel: SplashViewModel by viewModels {
        requireContext().appContainer.viewModelFactory
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        // Start the splash sequence
        viewModel.startSplashSequence()
    }

    private fun setupObservers() {
        // Listen for navigation decisions
        viewModel.navigationEvent.observe(viewLifecycleOwner) { destination ->
            when (destination) {
                SplashViewModel.NavigationDestination.LOGIN -> {
                    findNavController().navigate(R.id.action_splash_to_login)
                }
                SplashViewModel.NavigationDestination.DASHBOARD -> {
                    findNavController().navigate(R.id.action_splash_to_dashboard)
                }
            }
        }

        // Listen for loading state changes
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide loading spinner based on state
            // We'll implement the UI next!
            view?.findViewById<ProgressBar>(R.id.progressBar)?.visibility =
                if (isLoading) View.VISIBLE else View.GONE
            view?.findViewById<TextView>(R.id.tvLoading)?.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }
    }
}

// COMPARISON:
// Simple approach: 8 lines of dependency creation in EVERY fragment
// Fancy approach: 1 line total: `by viewModels { requireContext().appContainer.viewModelFactory }`