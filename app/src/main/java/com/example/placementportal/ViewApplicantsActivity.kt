package com.example.placementportal

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.placementportal.adapters.ApplicantsAdapter
import com.example.placementportal.databinding.ActivityViewApplicantsBinding
import com.example.placementportal.applicationModel.ApplicationModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewApplicantsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewApplicantsBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewApplicantsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Get the Job ID passed from the Company Job Adapter
        val jobId = intent.getStringExtra("JOB_ID") ?: return

        setupRecyclerView()
        fetchAppliedStudents(jobId)

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        binding.rvApplicants.layoutManager = LinearLayoutManager(this)
    }

    // Inside ViewApplicantsActivity.kt
    private fun fetchAppliedStudents(jobId: String) {
        Log.d("APP_DEBUG", "Starting query for jobId: $jobId") // Check what ID we are searching for

        db.collection("Applications")
            .whereEqualTo("jobId", jobId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("FIRESTORE_ERROR", "Error: ${error.message}")
                    return@addSnapshotListener
                }

                if (value != null) {
                    Log.d("APP_DEBUG", "Documents found in Firestore: ${value.size()}")

                    // If value.size() > 0 but the list is still empty,
                    // it means the Model mapping failed.
                    val list = value.toObjects(ApplicationModel::class.java)
                    Log.d("APP_DEBUG", "Mapped to Model list size: ${list.size}")

                    binding.rvApplicants.adapter = ApplicantsAdapter(list)
                    binding.tvApplicantCount.text = "${list.size} Students Applied"
                    binding.llEmptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    binding.pbApplicants.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
    }
}