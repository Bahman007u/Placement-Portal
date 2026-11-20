package com.example.placementportal.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.placementportal.R
import com.example.placementportal.StudentProfileDetailActivity
import com.example.placementportal.applicationModel.ApplicationModel
import com.example.placementportal.model.Student
import com.google.firebase.firestore.FirebaseFirestore

class ApplicantsAdapter(private val list: List<ApplicationModel>) :
    RecyclerView.Adapter<ApplicantsAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.ivStudentProfile)
        val name: TextView = v.findViewById(R.id.tvStudentName)
        val gpa: TextView = v.findViewById(R.id.tvStudentGPA)
        val skills: TextView = v.findViewById(R.id.tvStudentSkills)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_applicant, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = list[position]
        val context = holder.itemView.context

        // Bind basic data from the ApplicationModel
        holder.name.text = app.studentName
        holder.gpa.text = "GPA: ${app.studentGpa}"
        holder.skills.text = "Skills: ${app.studentSkills}"

        Glide.with(context)
            .load(app.studentProfilePic)
            .placeholder(R.drawable.profile)
            .circleCrop()
            .into(holder.img)

        // NEW: Click Listener to navigate to Full Profile
        holder.itemView.setOnClickListener {
            val db = FirebaseFirestore.getInstance()

            // 1. Fetch the full student profile using the ID saved in the application
            db.collection("Students").document(app.studentId).get()
                .addOnSuccessListener { snapshot ->
                    val student = snapshot.toObject(Student::class.java)
                    if (student != null) {
                        // 2. Pass the Parcelable student object to the Detail Activity
                        val intent = Intent(context, StudentProfileDetailActivity::class.java)
                        intent.putExtra("STUDENT_DATA", student)
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Student profile no longer exists", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun getItemCount() = list.size
}