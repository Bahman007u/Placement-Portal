package com.example.placementportal.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.placementportal.JobDetailsActivity
import com.example.placementportal.R
import com.example.placementportal.jobmodel.JobModel

class StudentJobAdapter(private var jobs: List<JobModel>) :
    RecyclerView.Adapter<StudentJobAdapter.StudentViewHolder>() {

    class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logo: ImageView = view.findViewById(R.id.ivStudentCompanyLogo)
        val title: TextView = view.findViewById(R.id.tvStudentJobTitle)
        val company: TextView = view.findViewById(R.id.tvStudentCompanyName)
        val desc: TextView = view.findViewById(R.id.tvStudentJobDesc)
        val btnApply: Button = view.findViewById(R.id.btnApplyJob)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_job, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val job = jobs[position]

        holder.title.text = job.jobTitle
        holder.company.text = job.companyName
        holder.desc.text = job.description

        Glide.with(holder.itemView.context)
            .load(job.companyLogoUrl)
            .placeholder(R.drawable.uni_logo)
            .circleCrop()
            .into(holder.logo)

        // NEW LOGIC: Just open the Details Activity
        holder.btnApply.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, JobDetailsActivity::class.java).apply {
                putExtra("JOB_ID", job.jobId)
                putExtra("JOB_TITLE", job.jobTitle)
                putExtra("COMPANY_NAME", job.companyName)
                putExtra("COMPANY_LOGO", job.companyLogoUrl)
                putExtra("DESCRIPTION", job.description)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = jobs.size

    fun updateList(newList: List<JobModel>) {
        jobs = newList
        notifyDataSetChanged()
    }
}