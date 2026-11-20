package com.example.placementportal


import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.placementportal.adapters.JobAdapter
import com.example.placementportal.companyViewModel.CompanyViewModel
import com.example.placementportal.databinding.ActivityJobsBinding

class JobsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobsBinding
    private val viewModel: CompanyViewModel by viewModels()
    private lateinit var adapter: JobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener { finish() }

        // Setup RecyclerView
        adapter = JobAdapter(emptyList())
        binding.rvJobs.layoutManager = LinearLayoutManager(this)
        binding.rvJobs.adapter = adapter

        // Fetch and Observe
        viewModel.fetchMyJobs()
        viewModel.myJobs.observe(this) { jobs ->
            if (jobs.isNullOrEmpty()) {
                binding.rvJobs.visibility = View.VISIBLE
                binding.rvJobs.visibility = View.GONE
            } else {
                binding.rvJobs.visibility = View.GONE
                binding.rvJobs.visibility = View.VISIBLE
                adapter.updateList(jobs)
            }
        }
    }
}