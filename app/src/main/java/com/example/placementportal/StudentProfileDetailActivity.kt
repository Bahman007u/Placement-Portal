package com.example.placementportal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.placementportal.databinding.ActivityStudentProfileDetailBinding
import com.example.placementportal.model.Student

class StudentProfileDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentProfileDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentProfileDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Get the Parcelable Student object
        val student = intent.getParcelableExtra<Student>("STUDENT_DATA") ?: return

        // 2. Map fields to UI
        binding.apply {
            tvDetailFullName.text = student.fullName
            tvDetailEmail.text = student.email
            tvDetailPhone.text = student.phone
            tvDetailBranch.text = "Branch: ${student.branch}"
            tvDetailCgpa.text = "CGPA: ${student.cgpa}"
            tvDetailBacklogs.text = "Backlogs: ${student.backlogs}"
            tvDetailDuration.text = "${student.startYear} - ${student.endYear}"
            tvDetailSkills.text = student.skills
            tvDetailLinkedin.text = student.linkedin

            Glide.with(this@StudentProfileDetailActivity)
                .load(student.profilePicUrl)
                .placeholder(R.drawable.profile)
                .circleCrop()
                .into(ivDetailProfilePic)

            // 3. Updated Resume Logic: Open in Browser
            btnViewResume.setOnClickListener {
                val url = student.resumeUrl
                if (!url.isNullOrEmpty()) {
                    try {
                        // This forces the phone to open the link in a browser
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(this@StudentProfileDetailActivity,
                            "No app found to open PDF", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@StudentProfileDetailActivity,
                        "Resume not uploaded", Toast.LENGTH_SHORT).show()
                }
            }

            btnBack.setOnClickListener { finish() }
        }
    }
}