package com.example.placementportal.adapters


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.placementportal.R
import com.example.placementportal.ViewApplicantsActivity
import com.example.placementportal.jobmodel.JobModel

class JobAdapter(private var jobs: List<JobModel>) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    class JobViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvJobTitle)
        val desc: TextView = view.findViewById(R.id.tvJobDescription)
        val applicants: TextView = view.findViewById(R.id.tvApplicantCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_jobs, parent, false)
        return JobViewHolder(view)

    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobs[position]

        holder.title.text = job.jobTitle
        holder.desc.text = job.description

        // FIX 1: Wrap the Int in a string template to avoid ResourceNotFoundException
        holder.applicants.text = "${job.appliedCount} Applied"

        // FIX 2: Set the click listener on the TextView or a Button
        // When HR clicks this updated button:
        holder.applicants.setOnClickListener {
            val intent = Intent(holder.itemView.context, ViewApplicantsActivity::class.java)
            // Make sure 'job.jobId' matches the "7O9RbQ..." value in your screenshot
            intent.putExtra("JOB_ID", job.jobId)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = jobs.size

    fun updateList(newList: List<JobModel>) {
        this.jobs = newList
        notifyDataSetChanged()
    }
}