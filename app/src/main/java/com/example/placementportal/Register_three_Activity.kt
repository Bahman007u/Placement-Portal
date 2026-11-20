package com.example.placementportal

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.placementportal.authViewModel.AuthViewModelOnly
import com.example.placementportal.databinding.ActivityRegisterThreeBinding
import com.example.placementportal.model.Student

class Register_three_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterThreeBinding
    private val viewModel: AuthViewModelOnly by viewModels()
    private var pdfUri: Uri? = null

    // Launcher defined at class level to avoid lifecycle crashes
    private val pickFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            pdfUri = uri
            binding.etUploadResume.setText("Resume Attached ✅")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterThreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fixed: VERSION_33 replaced with integer 33 for compatibility
        val student = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("STUDENT_DATA", Student::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("STUDENT_DATA")
        }

        binding.etUploadResume.setOnClickListener {
            pickFile.launch("application/pdf")
        }

        setupObservers()

        binding.btnCompleteRegistration.setOnClickListener {
            val skillsText = binding.etSkills.text.toString().trim()
            val linkedinText = binding.etLinkedin.text.toString().trim()

            if (skillsText.isEmpty()) {
                binding.etSkills.error = "Skills are required"
                return@setOnClickListener
            }

            student?.apply {
                skills = skillsText
                linkedin = linkedinText
            }

            if (student != null) {
                // Temporary password - replace with actual pass from Step 1 if needed
                viewModel.register(student, pdfUri)
            } else {
                Toast.makeText(this, "Data Error: Restart Registration", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { loading ->
            binding.btnCompleteRegistration.isEnabled = !loading
            binding.btnCompleteRegistration.text = if (loading) "Please wait..." else "Complete Registration"
        }

        viewModel.regStatus.observe(this) { (success, error) ->
            if (success) {
                Toast.makeText(this, "Welcome aboard!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Login_Activity::class.java))
                finishAffinity()
            } else {
                Toast.makeText(this, "Registration Failed: $error", Toast.LENGTH_LONG).show()
            }
        }
    }
}