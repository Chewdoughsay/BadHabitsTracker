package com.example.badhabitstracker.C_presentation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.badhabitstracker.C_presentation.adapter.HabitsAdapter
import com.example.badhabitstracker.C_presentation.viewmodel.DashboardViewModel
import com.example.badhabitstracker.R
import com.example.badhabitstracker.D_data_injection.appContainer
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels {
        requireContext().appContainer.viewModelFactory
    }

    // UI Components
    private lateinit var tvWelcomeMessage: TextView
    private lateinit var tvOverallStreak: TextView
    private lateinit var ivSettings: ImageView

    // Daily Inspiration Card
    private lateinit var tvDailyQuote: TextView
    private lateinit var tvQuoteAuthor: TextView
    private lateinit var tvDailyHealthTip: TextView
    private lateinit var ivRefreshContent: ImageView
    private lateinit var tvOfflineIndicator: TextView
    private lateinit var progressBarContent: ProgressBar

    // Statistics Cards
    private lateinit var tvActiveHabitsCount: TextView
    private lateinit var tvTotalDaysCount: TextView
    private lateinit var tvMoneySaved: TextView

    // Habits Section
    private lateinit var rvActiveHabits: RecyclerView
    private lateinit var layoutEmptyHabits: LinearLayout
    private lateinit var tvSeeAllHabits: TextView

    // Achievements Section
    private lateinit var layoutAchievementsList: LinearLayout
    private lateinit var layoutEmptyAchievements: LinearLayout
    private lateinit var tvAchievementCount: TextView

    // Actions
    private lateinit var fabAddHabit: FloatingActionButton
    private lateinit var btnLogout: Button
    private lateinit var progressBarLogout: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
    }

    private fun initializeViews(view: View) {
        // Header
        tvWelcomeMessage = view.findViewById(R.id.tvWelcomeMessage)
        tvOverallStreak = view.findViewById(R.id.tvOverallStreak)
        ivSettings = view.findViewById(R.id.ivSettings)

        // Daily Inspiration Card
        tvDailyQuote = view.findViewById(R.id.tvDailyQuote)
        tvQuoteAuthor = view.findViewById(R.id.tvQuoteAuthor)
        tvDailyHealthTip = view.findViewById(R.id.tvDailyHealthTip)
        ivRefreshContent = view.findViewById(R.id.ivRefreshContent)
        tvOfflineIndicator = view.findViewById(R.id.tvOfflineIndicator)
        progressBarContent = view.findViewById(R.id.progressBarContent)

        // Statistics Cards
        tvActiveHabitsCount = view.findViewById(R.id.tvActiveHabitsCount)
        tvTotalDaysCount = view.findViewById(R.id.tvTotalDaysCount)
        tvMoneySaved = view.findViewById(R.id.tvMoneySaved)

        // Habits Section
        rvActiveHabits = view.findViewById(R.id.rvActiveHabits)
        layoutEmptyHabits = view.findViewById(R.id.layoutEmptyHabits)
        tvSeeAllHabits = view.findViewById(R.id.tvSeeAllHabits)

        // Achievements Section
        layoutAchievementsList = view.findViewById(R.id.layoutAchievementsList)
        layoutEmptyAchievements = view.findViewById(R.id.layoutEmptyAchievements)
        tvAchievementCount = view.findViewById(R.id.tvAchievementCount)

        // Actions
        fabAddHabit = view.findViewById(R.id.fabAddHabit)
        btnLogout = view.findViewById(R.id.btnLogout)
        progressBarLogout = view.findViewById(R.id.progressBarLogout)
    }

    private fun setupRecyclerView() {
        val habitsAdapter = HabitsAdapter(
            onHabitClick = { habit ->
                // TODO: Navigate to habit detail
                Toast.makeText(requireContext(), "Clicked: ${habit.name}", Toast.LENGTH_SHORT)
                    .show()
            },
            onCheckInClick = { habitId, wasSuccessful ->
                onHabitCheckIn(habitId, wasSuccessful)
            }
        )

        rvActiveHabits.layoutManager = LinearLayoutManager(requireContext())
        rvActiveHabits.adapter = habitsAdapter
    }

    private fun setupClickListeners() {
        // Refresh daily content (HTTP requests)
        ivRefreshContent.setOnClickListener {
            viewModel.refreshDailyContent()
        }

        // Settings icon (SharedPreferences demo)
        ivSettings.setOnClickListener {
            // TODO: Navigate to settings or show settings dialog
            Toast.makeText(requireContext(), "Settings clicked - SharedPreferences demo", Toast.LENGTH_SHORT).show()
        }

        // See all habits
        tvSeeAllHabits.setOnClickListener {
            // TODO: Navigate to all habits screen
            Toast.makeText(requireContext(), "See all habits clicked", Toast.LENGTH_SHORT).show()
        }

        // Add new habit
        fabAddHabit.setOnClickListener {
            // TODO: Navigate to add habit screen
            Toast.makeText(requireContext(), "Add habit clicked", Toast.LENGTH_SHORT).show()
        }

        // Logout
        btnLogout.setOnClickListener {
            viewModel.onLogoutClicked()
        }
    }

    private fun setupObservers() {
        // ============ USER & WELCOME MESSAGE ============

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            tvWelcomeMessage.text = viewModel.getWelcomeMessage()
        }

        // ============ DASHBOARD STATISTICS ============

        viewModel.dashboardStatistics.observe(viewLifecycleOwner) { stats ->
            if (stats != null) {
                val (activeCount, totalDays, moneySaved) = viewModel.getFormattedStats()
                tvActiveHabitsCount.text = activeCount
                tvTotalDaysCount.text = totalDays
                tvMoneySaved.text = moneySaved

                // Update overall streak info
                tvOverallStreak.text = if (stats.longestStreakEver > 0) {
                    "Best streak: ${stats.longestStreakEver} days"
                } else {
                    "Ready to start your journey"
                }
            }
        }

        // ============ ACTIVE HABITS (RECYCLERVIEW) ============

        viewModel.activeHabits.observe(viewLifecycleOwner) { habits ->
            (rvActiveHabits.adapter as? HabitsAdapter)?.updateHabits(habits)
        }

        viewModel.showEmptyHabitsState.observe(viewLifecycleOwner) { showEmpty ->
            if (showEmpty) {
                rvActiveHabits.visibility = View.GONE
                layoutEmptyHabits.visibility = View.VISIBLE
            } else {
                rvActiveHabits.visibility = View.VISIBLE
                layoutEmptyHabits.visibility = View.GONE
            }
        }

        // ============ HTTP REQUESTS (REQUIREMENTS) ============

        viewModel.dailyQuote.observe(viewLifecycleOwner) { quote ->
            if (quote != null) {
                tvDailyQuote.text = "\"${quote.content}\""
                tvQuoteAuthor.text = "— ${quote.author}"
            }
        }

        viewModel.dailyHealthTip.observe(viewLifecycleOwner) { healthTip ->
            if (healthTip != null) {
                tvDailyHealthTip.text = healthTip.fact
            }
        }

        viewModel.isContentLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressBarContent.visibility = View.VISIBLE
                tvDailyQuote.visibility = View.GONE
                tvQuoteAuthor.visibility = View.GONE
                tvDailyHealthTip.visibility = View.GONE
            } else {
                progressBarContent.visibility = View.GONE
                tvDailyQuote.visibility = View.VISIBLE
                tvQuoteAuthor.visibility = View.VISIBLE
                tvDailyHealthTip.visibility = View.VISIBLE
            }
        }

        viewModel.isOfflineMode.observe(viewLifecycleOwner) { isOffline ->
            tvOfflineIndicator.visibility = if (isOffline) View.VISIBLE else View.GONE
        }

        // ============ ACHIEVEMENTS ============

        viewModel.recentAchievements.observe(viewLifecycleOwner) { achievements ->
            // TODO: Populate achievements list dynamically
            tvAchievementCount.text = achievements.size.toString()
            tvAchievementCount.visibility = if (achievements.isNotEmpty()) View.VISIBLE else View.GONE

            if (achievements.isNotEmpty()) {
                Toast.makeText(requireContext(), "Loaded ${achievements.size} recent achievements", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.showEmptyAchievementsState.observe(viewLifecycleOwner) { showEmpty ->
            if (showEmpty) {
                layoutAchievementsList.visibility = View.GONE
                layoutEmptyAchievements.visibility = View.VISIBLE
            } else {
                layoutAchievementsList.visibility = View.VISIBLE
                layoutEmptyAchievements.visibility = View.GONE
            }
        }

        // ============ UI STATE & LOADING ============

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressBarLogout.visibility = View.VISIBLE
                btnLogout.text = ""
                btnLogout.isEnabled = false
            } else {
                progressBarLogout.visibility = View.GONE
                btnLogout.text = getString(R.string.logout)
                btnLogout.isEnabled = true
            }
        }

        // ============ ERROR HANDLING ============

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrBlank()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        // ============ NAVIGATION EVENTS ============

        viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                DashboardViewModel.NavigationEvent.GO_TO_LOGIN -> {
                    findNavController().navigate(
                        R.id.action_dashboard_to_login,
                        null,
                        androidx.navigation.navOptions {
                            popUpTo(R.id.nav_graph) { inclusive = true }
                        }
                    )
                }
                DashboardViewModel.NavigationEvent.GO_TO_ADD_HABIT -> {
                    // TODO: Navigate to add habit screen
                    Toast.makeText(requireContext(), "Navigation to Add Habit", Toast.LENGTH_SHORT).show()
                }
                DashboardViewModel.NavigationEvent.GO_TO_SETTINGS -> {
                    // TODO: Navigate to settings screen
                    Toast.makeText(requireContext(), "Navigation to Settings", Toast.LENGTH_SHORT).show()
                }
                DashboardViewModel.NavigationEvent.GO_TO_HABIT_DETAIL -> {
                    // TODO: Navigate to habit detail screen
                    Toast.makeText(requireContext(), "Navigation to Habit Detail", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Handle habit check-in from RecyclerView adapter
     * This will be called by the adapter when user taps check-in button
     */
    fun onHabitCheckIn(habitId: Long, wasSuccessful: Boolean) {
        viewModel.onHabitCheckIn(habitId, wasSuccessful)
    }
}