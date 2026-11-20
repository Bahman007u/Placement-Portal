package com.example.placementportal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.placementportal.activities.StudentProfile
import com.example.placementportal.authViewModel.AuthViewModelOnly
import com.example.placementportal.databinding.ActivityLoginBinding

class Login_Activity : AppCompatActivity() {

    // Using ViewBinding for better performance and safety
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModelOnly by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. NAVIGATE TO REGISTER
        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(this, Register_one_Activity::class.java)
            startActivity(intent)
        }

        // 2. HANDLE LOGIN BUTTON CLICK
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmai.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. OBSERVE VIEWMODEL FOR RESULTS
        observeViewModel()
    }

    private fun observeViewModel() {
        // Handle Loading State (e.g., disable button while logging in)
        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnLogin.isEnabled = !isLoading
            binding.btnLogin.text = if (isLoading) "Verifying..." else "LOGIN"
        }

        // Handle Login Success or Failure
        viewModel.loginStatus.observe(this) { (success, message) ->
            if (success) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                // Navigate to SelectionActivity or Dashboard
                startActivity(Intent(this, StudentDashboard::class.java))
                finish()
            } else {
                Toast.makeText(this, "Login Failed: $message", Toast.LENGTH_LONG).show()
            }
        }
    }
}