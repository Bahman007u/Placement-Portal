package com.example.placementportal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.placementportal.CompanyProfileActivity
import com.example.placementportal.companyViewModel.CompanyViewModel
import com.example.placementportal.databinding.ActivityCompanyRegisterBinding // Adjust package name

class CompanyRegister: AppCompatActivity() {

    private lateinit var binding: ActivityCompanyRegisterBinding
    private val viewModel: CompanyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.alreadyRegistertxt.setOnClickListener {
            val intent = Intent(this, CompanyLoginActivity::class.java)
            startActivity(intent)
        }

        // 1. Set up the Register Button
        binding.btnCompleteRegistration.setOnClickListener {
            val name = binding.etCompanyName.text.toString().trim()
            val email = binding.hrEmail.text.toString().trim()
            val pass = binding.password.text.toString().trim()

            if (pass.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.signUp(name, email, pass)
            }
        }

        viewModel.isSuccess.observe(this) { success ->
            if (success) {
                // Save role to SharedPreferences here if not already done in SelectionActivity
                val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                sharedPref.edit().putString("userRole", "Company").apply()

                startActivity(Intent(this, CompanyProfileActivity::class.java))
                finish()
            }
        }
    }
}