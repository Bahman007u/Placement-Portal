package com.example.placementportal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.placementportal.databinding.ActivityDeveloperContactBinding

class DeveloperContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeveloperContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeveloperContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button functionality
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Click to Email functionality
        binding.tvDevEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:rgctech@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Inquiry from Placement Portal")
            }
            startActivity(intent)
        }
    }
}