package com.example.badhabitstracker.C_presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.badhabitstracker.A_domain.model.Habit
import com.example.badhabitstracker.R

class HabitsAdapter(
    private val onHabitClick: (Habit) -> Unit,
    private val onCheckInClick: (Long, Boolean) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {

    private var habits = listOf<Habit>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit_dashboard, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount(): Int = habits.size

    fun updateHabits(newHabits: List<Habit>) {
        habits = newHabits
        notifyDataSetChanged()
    }

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvHabitIcon: TextView = itemView.findViewById(R.id.tvHabitIcon)
        private val tvHabitName: TextView = itemView.findViewById(R.id.tvHabitName)
        private val tvCurrentStreak: TextView = itemView.findViewById(R.id.tvCurrentStreak)
        private val tvProgressInfo: TextView = itemView.findViewById(R.id.tvProgressInfo)
        private val tvMoneySaved: TextView = itemView.findViewById(R.id.tvMoneySaved)
        private val btnCheckIn: Button = itemView.findViewById(R.id.btnCheckIn)

        fun bind(habit: Habit) {
            // Habit category icon
            tvHabitIcon.text = habit.category.icon

            // Habit name
            tvHabitName.text = habit.name

            // Current streak
            tvCurrentStreak.text = when {
                habit.currentStreak == 0 -> "Start today"
                habit.currentStreak == 1 -> "Day 1"
                else -> "Day ${habit.currentStreak}"
            }

            // Progress information
            tvProgressInfo.text = when {
                habit.targetDays != null -> {
                    val percentage = habit.getProgressPercentage() ?: 0
                    "${percentage}% to goal"
                }
                habit.currentStreak > 0 -> "${habit.currentStreak} days clean"
                else -> "Ready to start"
            }

            // Money saved
            val moneySaved = habit.getMoneySaved()
            if (moneySaved > 0) {
                tvMoneySaved.text = "$${String.format("%.0f", moneySaved)} saved"
                tvMoneySaved.visibility = View.VISIBLE
            } else {
                tvMoneySaved.visibility = View.GONE
            }

            // Check-in button setup
            setupCheckInButton(habit)

            // Item click listener
            itemView.setOnClickListener {
                onHabitClick(habit)
            }
        }

        private fun setupCheckInButton(habit: Habit) {
            // TODO: Check if user already logged today's progress
            // For now, assume they haven't checked in today
            val hasCheckedInToday = false

            if (hasCheckedInToday) {
                btnCheckIn.text = "✓"
                btnCheckIn.isEnabled = false
                btnCheckIn.setBackgroundColor(
                    itemView.context.getColor(R.color.success_green)
                )
            } else {
                btnCheckIn.text = "?"
                btnCheckIn.isEnabled = true
                btnCheckIn.setBackgroundColor(
                    itemView.context.getColor(R.color.orange_light)
                )

                // Set up check-in click listener with success/failure options
                btnCheckIn.setOnClickListener {
                    showCheckInOptions(habit)
                }
            }
        }

        private fun showCheckInOptions(habit: Habit) {
            // Create simple dialog for success/failure
            val context = itemView.context
            val options = arrayOf("Success ✅", "Failed ❌")

            val builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("How did you do with ${habit.name} today?")
            builder.setItems(options) { _, which ->
                val wasSuccessful = which == 0
                onCheckInClick(habit.id, wasSuccessful)

                // Update button immediately for user feedback
                if (wasSuccessful) {
                    btnCheckIn.text = "✅"
                    btnCheckIn.setBackgroundColor(context.getColor(R.color.success_green))
                } else {
                    btnCheckIn.text = "❌"
                    btnCheckIn.setBackgroundColor(context.getColor(R.color.error_red))
                }
                btnCheckIn.isEnabled = false
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }
    }
}