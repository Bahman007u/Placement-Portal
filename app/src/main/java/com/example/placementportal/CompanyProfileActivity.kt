package com.example.placementportal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.placementportal.databinding.ActivityCompanyProfileBinding
import com.example.placementportal.companyViewModel.CompanyViewModel
// Ensure these paths match your actual project structure

class CompanyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompanyProfileBinding
    private val viewModel: CompanyViewModel by viewModels()

    // Image Picker for Company Logo updates
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.updateProfilePic(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDrawer()
        setupObservers()
        setupClickListeners()

        // Initial Data Load
        viewModel.fetchProfile()
        viewModel.fetchMyJobs()
    }

    private fun setupDrawer() {
        // Handle Menu Icon Click to open sidebar
        binding.ivMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // Handle Side Navigation Menu Clicks
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            // Close drawer immediately on click
            binding.drawerLayout.closeDrawer(GravityCompat.START)

            when (menuItem.itemId) {
                R.id.nav_switch_student -> {
                    // Uses Login_Activity (Student Entry)
                    confirmAndLogout(Login_Activity::class.java, "Logout and switch to Student Login?")
                    true
                }
                R.id.nav_logout -> {
                    // Uses CompanyLoginActivity
                    confirmAndLogout(CompanyLoginActivity::class.java, "Are you sure you want to sign out?")
                    true
                }
                else -> false
            }
        }

        // Handle Back Press: Close drawer if open, otherwise go back
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    /**
     * Reusable Logout & Role-Switch Logic
     */
    private fun confirmAndLogout(targetActivity: Class<*>, message: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Logout")
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                // Sign out via ViewModel (which handles FirebaseAuth.signOut())
                viewModel.logout()

                // Clear activity stack and navigate to the selected Login screen
                val intent = Intent(this, targetActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupObservers() {
        // Observe Profile Changes
        viewModel.companyData.observe(this) { company ->
            company?.let {
                binding.tvShowCompanyName.text = it.companyName
                binding.tvShowIndustry.text = it.industry
                binding.tvShowWebsite.text = it.website.ifBlank { "Website Not Provided" }
                binding.tvShowDescription.text = it.description.ifBlank { "No company description available." }

                // Load Logo with Glide
                Glide.with(this)
                    .load(it.profilePicUrl)
                    .placeholder(R.drawable.uni_logo) // Default placeholder
                    .circleCrop()
                    .into(binding.ivCompanyLogo)
            }
        }

        // Observe Job Counts
        viewModel.myJobs.observe(this) { jobs ->
            binding.tvJobCount.text = jobs.size.toString()
        }

        // Observe Status/Error Messages
        viewModel.statusMessage.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                viewModel.resetStatus()
            }
        }
    }

    private fun setupClickListeners() {
        // Change Profile Picture
        binding.ivCompanyLogo.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Navigate to PostJobActivity
        binding.btnPostJob.setOnClickListener {
            startActivity(Intent(this, PostJobActivity::class.java))
        }

        // Navigate to JobsActivity (List of jobs posted by company)
        binding.layoutActiveJobs.setOnClickListener {
            val count = viewModel.myJobs.value?.size ?: 0
            if (count > 0) {
                startActivity(Intent(this, JobsActivity::class.java))
            } else {
                Toast.makeText(this, "You haven't posted any jobs yet.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh job count when returning from PostJobActivity
        viewModel.fetchMyJobs()
    }
}