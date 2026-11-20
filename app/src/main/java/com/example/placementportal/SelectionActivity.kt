package com.example.placementportal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.placementportal.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Initialize SharedPreferences
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

        binding.btnStudent.setOnClickListener {
            // 1. Save Role and FirstTime flag
            sharedPref.edit().apply {
                putString("userRole", "Student")
                putBoolean("isFirstTime", false)
                apply()
            }

            // 2. Direct to Student Registration (or Login as per your flow)
            val intent = Intent(this, Register_one_Activity::class.java)
            startActivity(intent)
            finish() // Optional: Finish this activity so they can't go back
        }

        binding.btnCompany.setOnClickListener {
            // 1. Save Role and FirstTime flag
            sharedPref.edit().apply {
                putString("userRole", "Company")
                putBoolean("isFirstTime", false)
                apply()
            }

            // 2. Direct to Company Registration
            val intent = Intent(this, CompanyRegister::class.java)
            startActivity(intent)
            finish()
        }
    }
}