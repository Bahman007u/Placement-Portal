package com.example.placementportal

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.placementportal.databinding.ActivityRegisterOneBinding
import com.example.placementportal.model.Student

class Register_one_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterOneBinding

    // Branch Options
    private val branches = arrayOf("MCA", "M.Tech", "BCA", "CSE", "BBA", "MBA")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBranchDropdown()

        binding.alreadyRegistertxt.setOnClickListener {
            val intent = Intent(this, Login_Activity::class.java)
            startActivity(intent)
        }

        binding.btnNext.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val phone = binding.etPhone.text.toString().trim()
            val branch = binding.autoBranch.text.toString()

            // 1. Empty Check
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || branch.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Full Name Validation (Fixed Logic: if < 3 OR > 15)
            if (fullName.length < 3 || fullName.length > 15) {
                Toast.makeText(this, "Full name must be between 3 and 15 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Phone Validation (10 digits only)
            if (phone.length != 10 || !phone.all { it.isDigit() }) {
                Toast.makeText(this, "Phone number must be exactly 10 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 4. Strong Password Validation
            val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=]).{8,}$")
            if (!passwordPattern.matches(password)) {
                Toast.makeText(this, "Password too weak. Use uppercase, lowercase, numbers & symbols.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Create Student object and pass to next activity
            val student = Student(
                fullName = fullName,
                email = email,
                password = password,
                phone = phone,
                branch = branch
            )

            val intent = Intent(this, Register_two_Activity::class.java)
            intent.putExtra("STUDENT_DATA", student)
            startActivity(intent)
        }
    }

    private fun setupBranchDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, branches)
        binding.autoBranch.setAdapter(adapter)
    }
}