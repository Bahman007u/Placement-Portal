package com.example.placementportal

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.placementportal.companyViewModel.CompanyViewModel
import com.example.placementportal.databinding.ActivityPostJobBinding

class PostJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostJobBinding
    private val viewModel: CompanyViewModel by viewModels()

    // 1. Selection Options
    private val courseList = arrayOf("MCA", "M.Tech", "BCA", "CSE", "BBA", "MBA", "All Courses")

    // CGPA starts from 4.0 as requested
    private val cgpaOptions = arrayOf("4.0", "5.0", "6.0", "7.0", "8.0", "9.0")

    // Backlogs up to 10 as requested
    private val backlogOptions = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

    private var selectedCourse = "All Courses"
    private var selectedCgpa = 4.0
    private var selectedBacklogs = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch company profile to ensure we have company name/logo for the job post
        viewModel.fetchProfile()

        setupDropdowns()
        setupObservers()

        binding.btnSubmitJob.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun setupDropdowns() {
        // Course Dropdown
        val courseAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, courseList)
        binding.autoCompleteCourses.setAdapter(courseAdapter)
        binding.autoCompleteCourses.setOnItemClickListener { p, _, pos, _ ->
            selectedCourse = p.getItemAtPosition(pos).toString()
        }

        // CGPA Dropdown
        val cgpaAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cgpaOptions)
        binding.autoCompleteCgpa.setAdapter(cgpaAdapter)
        binding.autoCompleteCgpa.setOnItemClickListener { p, _, pos, _ ->
            selectedCgpa = p.getItemAtPosition(pos).toString().toDouble()
        }

        // Backlog Dropdown
        val backlogAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, backlogOptions)
        binding.autoCompleteBacklogs.setAdapter(backlogAdapter)
        binding.autoCompleteBacklogs.setOnItemClickListener { p, _, pos, _ ->
            selectedBacklogs = p.getItemAtPosition(pos).toString().toInt()
        }
    }

    private fun validateAndSubmit() {
        val title = binding.etJobTitle.text.toString().trim()
        val desc = binding.etJobDescription.text.toString().trim()
        val benefits = binding.etBenefits.text.toString().trim()
        val eligibility = binding.etEligibility.text.toString().trim()

        // Validation Checks
        if (title.isEmpty()) {
            binding.etJobTitle.error = "Title is required"
            return
        }

        if (title.length < 3 || title.length > 30) {
            binding.etJobTitle.error = "Title must be between 3 and 30 characters"
            return
        }

        if (desc.isEmpty()) {
            binding.etJobDescription.error = "Description is required"
            return
        }

        if (desc.length < 50) {
            binding.etJobDescription.error = "Description should be at least 50 characters"
            return
        }

        // Optional: Check if a course was actually clicked/selected
        if (binding.autoCompleteCourses.text.isEmpty()) {
            Toast.makeText(this, "Please select a target course", Toast.LENGTH_SHORT).show()
            return
        }

        // All good? Post the job.
        viewModel.postNewJob(
            title, desc, benefits, eligibility,
            selectedCourse, selectedBacklogs, selectedCgpa
        )
    }

    private fun setupObservers() {
        viewModel.statusMessage.observe(this) { msg ->
            if (msg == "Job Posted Successfully") {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                finish() // Close activity and return to Profile
            } else if (!msg.isNullOrEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                viewModel.resetStatus()
            }
        }

        viewModel.isLoading.observe(this) { loading ->
            // You can toggle a ProgressBar visibility here if you have one in XML
            binding.btnSubmitJob.isEnabled = !loading
            binding.btnSubmitJob.text = if (loading) "Posting..." else "Post Job"
        }
    }
}