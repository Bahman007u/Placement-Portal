package com.example.placementportal.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.placementportal.R
import com.example.placementportal.databinding.ItemAppliedJobBinding
import com.example.placementportal.jobmodel.JobModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppliedJobAdapter(private var list: List<JobModel>) :
    RecyclerView.Adapter<AppliedJobAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAppliedJobBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppliedJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val job = list[position]

        holder.binding.apply {
            tvJobTitle.text = job.jobTitle
            tvCompanyName.text = job.companyName

            // Format timestamp to readable date
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = sdf.format(Date(job.timestamp))
            tvAppliedDate.text = "Applied on: $date"

            // Load Company Logo
            Glide.with(ivCompanyLogo.context)
                .load(job.companyLogoUrl)
                .placeholder(R.drawable.uni_logo)
                .circleCrop()
                .into(ivCompanyLogo)

            // Static status for now (or get from Application model if you have it)
            tvStatus.text = "Status: Pending"
        }
    }

    fun updateList(newList: List<JobModel>) {
        list = newList
        notifyDataSetChanged()
    }
}