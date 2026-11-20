package com.example.placementportal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.placementportal.CompanyProfileActivity
import com.example.placementportal.companyViewModel.CompanyViewModel
import com.example.placementportal.databinding.ActivityCompanyLoginBinding

class CompanyLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompanyLoginBinding
    private val viewModel: CompanyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(this, CompanyRegister::class.java)
            startActivity(intent)
        }

        // ONLY ONE CLICK LISTENER
        binding.btnLogin.setOnClickListener {
            val email = binding.etHREmail.text.toString().trim()
            val pass = binding.etHRPassword.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                viewModel.login(email, pass)
            } else {
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe Status Messages
        viewModel.statusMessage.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }

        // HANDLE NAVIGATION HERE
        viewModel.loginSuccess.observe(this) { success ->
            if (success) {
                // IMPORTANT: Save role so it persists after reinstall
                val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("userRole", "Company").apply()

                val intent = Intent(this, CompanyProfileActivity::class.java)
                startActivity(intent)
                finish() // Close login screen
            }
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.btnLogin.isEnabled = !loading
            binding.btnLogin.text = if (loading) "Logging in..." else "Login"
        }
    }
}