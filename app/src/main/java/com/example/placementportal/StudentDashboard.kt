package com.example.placementportal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.placementportal.activities.StudentProfile
import com.example.placementportal.adapters.StudentJobAdapter
import com.example.placementportal.databinding.ActivityStudentDashboardBinding
import com.example.placementportal.studentViewModel.StudentViewModel
import com.google.firebase.auth.FirebaseAuth

class StudentDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDashboardBinding
    private val viewModel: StudentViewModel by viewModels()
    private lateinit var adapter: StudentJobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDrawer()
        setupRecyclerView()
        setupObservers()
        setupBottomNavigation()
        setupBackNavigation()

        // Fetch initial data
        viewModel.fetchStudentProfile()
        viewModel.fetchAllJobs()
    }

    private fun setupObservers() {
        // Observe student profile to get eligibility criteria (Branch, CGPA, Backlogs)
        viewModel.studentData.observe(this) { student ->
            val sBranch = student?.branch ?: ""
            val sCgpa = student?.cgpa?.toDoubleOrNull() ?: 0.0
            val sBacklogs = student?.backlogs?.toIntOrNull() ?: 0

            // Nested observation: Filter all available jobs based on student eligibility
            viewModel.allJobs.observe(this) { jobs ->
                if (jobs != null) {
                    val filteredJobs = jobs.filter { job ->
                        // 1. Branch/Course Match
                        val branchMatch = job.targetCourse.equals(sBranch, ignoreCase = true) ||
                                job.targetCourse.equals("All Courses", ignoreCase = true)

                        // 2. CGPA Match (Student CGPA >= Job Minimum Requirement)
                        val cgpaMatch = sCgpa >= job.minCgpa

                        // 3. Backlog Match (Student Backlogs <= Job Maximum Allowance)
                        val backlogMatch = sBacklogs <= job.maxBacklogs

                        branchMatch && cgpaMatch && backlogMatch
                    }
                    adapter.updateList(filteredJobs)

                    // Toggle visibility of empty state message
                    binding.tvNoJobs.visibility = if (filteredJobs.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { msg ->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    private fun setupRecyclerView() {
        // Initialize adapter with an empty list; updated via LiveData
        adapter = StudentJobAdapter(emptyList())
        binding.rvStudentJobs.layoutManager = LinearLayoutManager(this)
        binding.rvStudentJobs.adapter = adapter
    }

    private fun setupDrawer() {
        binding.ivMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawer(GravityCompat.START)

            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    startActivity(Intent(this, StudentProfile::class.java))
                }
                R.id.nav_developer -> {
                    startActivity(Intent(this, DeveloperContactActivity::class.java))
                }
                R.id.nav_company_login -> {
                    showLogoutDialog(isSwitchingToCompany = true)
                }
                R.id.nav_logout -> {
                    showLogoutDialog(isSwitchingToCompany = false)
                }
            }
            true
        }
    }

    private fun showLogoutDialog(isSwitchingToCompany: Boolean) {
        val title = if (isSwitchingToCompany) "Switch to Company" else "Logout"
        val message = if (isSwitchingToCompany)
            "Logout to switch to Company Login?"
        else "Are you sure you want to sign out?"

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                val target = if (isSwitchingToCompany) CompanyLoginActivity::class.java else Login_Activity::class.java
                startActivity(Intent(this, target).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupBottomNavigation() {
        // HOME: Stay on current screen and update UI
        binding.llHome.setOnClickListener {
            updateNavUI(it.id)
        }

        // APPLIED: Navigate to the list of jobs the student has already applied for
        binding.llApplied.setOnClickListener {
            updateNavUI(it.id)
            startActivity(Intent(this, AppliedJobsActivity::class.java))
        }

        // PROFILE: Navigate to the student's profile details
        binding.llProfile.setOnClickListener {
            updateNavUI(it.id)
            startActivity(Intent(this, StudentProfile::class.java))
        }
    }

    private fun updateNavUI(selectedId: Int) {
        val activeColor = Color.parseColor("#8A0000") // Deep Red
        val inactiveColor = Color.parseColor("#757575") // Grey

        binding.ivHome.setColorFilter(if (selectedId == R.id.llHome) activeColor else inactiveColor)
        binding.ivApplied.setColorFilter(if (selectedId == R.id.llApplied) activeColor else inactiveColor)
        binding.ivProfile.setColorFilter(if (selectedId == R.id.llProfile) activeColor else inactiveColor)
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // If the sidebar (drawer) is open, back button closes it first
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }
}