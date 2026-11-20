package com.example.placementportal.activities // Ensure this matches your package

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.placementportal.R
import com.example.placementportal.authViewModel.AuthViewModelOnly
import com.example.placementportal.databinding.ActivityStudentProfile2Binding
import com.example.placementportal.model.Student

class StudentProfile : AppCompatActivity() {

    private lateinit var binding: ActivityStudentProfile2Binding
    private lateinit var viewModel: AuthViewModelOnly

    // To store the URI of the image picked from gallery
    private var selectedImageUri: Uri? = null

    // Launcher to handle Gallery Picking
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            // 1. Show immediate preview in the ImageView
            binding.ivProfilePicture.setImageURI(it)
            // 2. Make the 'Update Picture' button visible
            binding.btnSaveProfile.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentProfile2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthViewModelOnly::class.java]

        setupObservers()
        setupClickListeners()

        // Initial data fetch
        viewModel.fetchProfile()
    }

    private fun setupClickListeners() {
        // Pick image when clicking the profile picture or the camera icon
        binding.ivProfilePicture.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Upload to Cloudinary when clicking 'Update Picture'
        binding.btnSaveProfile.setOnClickListener {
            selectedImageUri?.let { uri ->
                viewModel.uploadProfileImage(uri)
            }
        }

        // View Resume PDF in Browser/Drive
        binding.btnViewResume.setOnClickListener {
            val url = viewModel.studentData.value?.resumeUrl
            if (!url.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } else {
                Toast.makeText(this, "No Resume Uploaded", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        // Observes Student Data changes (from fetch or after upload)
        viewModel.studentData.observe(this) { student ->
            if (student != null) {
                bindStudentData(student)
            }
        }

        // Observes loading state to toggle Progress Bar
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnSaveProfile.isEnabled = !loading
        }

        // FIX: Observes the successful upload status
        viewModel.imageUploadStatus.observe(this) { isSuccess ->
            if (isSuccess) {
                binding.btnSaveProfile.visibility = View.GONE
                Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
            } else if (selectedImageUri != null) {
                // Only show error if a user actually tried to upload
                Toast.makeText(this, "Upload failed. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindStudentData(student: Student) {
        binding.apply {
            tvFullName.text = student.fullName
            tvBranch.text = student.branch
            tvCgpa.text = student.cgpa.ifEmpty { "0.0" }
            tvBacklogs.text = student.backlogs.ifEmpty { "None" }
            tvBatch.text = "Batch: ${student.startYear} - ${student.endYear}"
            tvSkills.text = student.skills.ifEmpty { "No skills listed" }
            tvEmail.text = student.email

            // Load Circular Profile Pic from Cloudinary URL
            if (student.profilePicUrl.isNotEmpty()) {
                Glide.with(this@StudentProfile)
                    .load(student.profilePicUrl)
                    .circleCrop()
                    .placeholder(R.drawable.profileperson)
                    .into(ivProfilePicture)
            }
        }
    }
}