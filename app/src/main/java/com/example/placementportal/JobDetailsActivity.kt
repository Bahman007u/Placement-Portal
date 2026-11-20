package com.example.placementportal

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.placementportal.applicationModel.ApplicationModel
import com.example.placementportal.databinding.ActivityJobDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class JobDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobDetailsBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Get Data from Intent
        val jobId = intent.getStringExtra("JOB_ID") ?: ""
        val jobTitle = intent.getStringExtra("JOB_TITLE") ?: ""
        val companyName = intent.getStringExtra("COMPANY_NAME") ?: ""
        val companyLogo = intent.getStringExtra("COMPANY_LOGO") ?: ""
        val description = intent.getStringExtra("DESCRIPTION") ?: ""

        // 2. Bind to UI
        binding.tvDetailJobTitle.text = jobTitle
        binding.tvDetailCompanyName.text = companyName
        binding.tvDetailDesc.text = description

        Glide.with(this)
            .load(companyLogo)
            .placeholder(R.drawable.uni_logo)
            .circleCrop()
            .into(binding.ivDetailCompanyLogo)

        // 3. CHECK APPLICATION STATUS IMMEDIATELY
        checkIfAlreadyApplied(jobId)

        // 4. Click Listeners
        binding.btnApplyNow.setOnClickListener {
            showApplyConfirmation(jobId)
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    /**
     * Checks if a document with ID "studentId_jobId" exists.
     * If yes, it disables and blurs the button.
     */
    private fun checkIfAlreadyApplied(jobId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        val uniqueAppId = "${currentUserId}_$jobId"

        db.collection("Applications").document(uniqueAppId).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    disableAndBlurButton()
                }
            }
    }

    private fun disableAndBlurButton() {
        binding.btnApplyNow.apply {
            isEnabled = false
            text = "Already Applied"
            alpha = 0.5f // Creates the "blurred/faded" look
            setBackgroundColor(Color.GRAY) // Visual cue for disabled state
        }
    }

    private fun showApplyConfirmation(jobId: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Application")
            .setMessage("Are you sure you want to apply for this job?")
            .setPositiveButton("Apply") { _, _ ->
                processApplication(jobId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun processApplication(jobId: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        // Unique ID to prevent double entry: studentId_jobId
        val uniqueAppId = "${currentUserId}_$jobId"

        db.collection("Students").document(currentUserId).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.getString("fullName") ?: "Unknown"
                    val gpa = snapshot.getString("cgpa") ?: "0.0"
                    val skills = snapshot.getString("skills") ?: ""
                    val pic = snapshot.getString("profilePicUrl") ?: ""

                    val application = ApplicationModel(
                        applicationId = uniqueAppId,
                        jobId = jobId,
                        studentId = currentUserId,
                        studentName = name,
                        studentGpa = gpa,
                        studentSkills = skills,
                        studentProfilePic = pic,
                        timestamp = System.currentTimeMillis()
                    )

                    val jobRef = db.collection("Jobs").document(jobId)
                    val appRef = db.collection("Applications").document(uniqueAppId)

                    db.runBatch { batch ->
                        // Using uniqueAppId here as the document ID
                        batch.set(appRef, application)
                        batch.update(jobRef, "appliedCount", FieldValue.increment(1))
                    }.addOnSuccessListener {
                        Toast.makeText(this, "Applied Successfully!", Toast.LENGTH_SHORT).show()
                        disableAndBlurButton() // Update UI immediately
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}