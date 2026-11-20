package com.example.placementportal

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.placementportal.adapters.AppliedJobAdapter
import com.example.placementportal.databinding.ActivityAppliedJobsBinding
import com.example.placementportal.studentViewModel.StudentViewModel
import com.google.firebase.auth.FirebaseAuth

class AppliedJobsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppliedJobsBinding
    private val viewModel: StudentViewModel by viewModels()
    private lateinit var adapter: AppliedJobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAppliedJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle Edge-to-Edge Padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupObservers()

        // Fetch Data
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            viewModel.fetchAllJobs() // Get all jobs first
            viewModel.fetchAppliedJobIds(currentUserId) { /* IDs will be observed */ }
        } else {
            Toast.makeText(this, "User session expired", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = AppliedJobAdapter(emptyList())
        binding.rvAppliedJobs.layoutManager = LinearLayoutManager(this)
        binding.rvAppliedJobs.adapter = adapter
    }

    private fun setupObservers() {
        // Observe Loading State
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe Error Messages
        viewModel.errorMessage.observe(this) { msg ->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        /**
         * Logic: We wait for BOTH the full list of jobs and the list of applied IDs.
         * Then we filter the full list to show only what the student applied for.
         */
        viewModel.allJobs.observe(this) { allJobs ->
            viewModel.appliedJobIds.observe(this) { appliedIds ->
                if (allJobs != null && appliedIds != null) {
                    val filteredList = allJobs.filter { job ->
                        appliedIds.contains(job.jobId)
                    }

                    adapter.updateList(filteredList)

                    // Show empty state if no applications found
                    if (filteredList.isEmpty()) {
                        binding.layoutEmpty.visibility = View.VISIBLE
                        binding.rvAppliedJobs.visibility = View.GONE
                    } else {
                        binding.layoutEmpty.visibility = View.GONE
                        binding.rvAppliedJobs.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}