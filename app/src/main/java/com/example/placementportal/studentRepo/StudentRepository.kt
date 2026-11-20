package com.example.placementportal.studentRepo

import com.example.placementportal.applicationModel.ApplicationModel
import com.example.placementportal.jobmodel.JobModel
import com.example.placementportal.model.Student
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class StudentRepository {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Fetches all jobs available in the system, ordered by newest first.
     */
    fun getAllJobs(callback: (List<JobModel>) -> Unit) {
        db.collection("Jobs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    callback(emptyList())
                    return@addSnapshotListener
                }
                val jobs = value?.toObjects(JobModel::class.java) ?: emptyList()
                callback(jobs)
            }
    }

    /**
     * Fetches details for a specific student profile.
     */
    fun getStudentDetails(uid: String, callback: (Student?) -> Unit) {
        db.collection("Students").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val student = document.toObject(Student::class.java)
                    callback(student)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { callback(null) }
    }

    /**
     * Handles the Job Application process.
     * Uses a custom Document ID (studentId_jobId) to prevent duplicate applications.
     */
    fun applyToJob(application: ApplicationModel, callback: (Boolean, String?) -> Unit) {
        // Create a unique ID to prevent a student from applying to the same job twice
        val uniqueAppId = "${application.studentId}_${application.jobId}"
        val appRef = db.collection("Applications").document(uniqueAppId)
        val jobRef = db.collection("Jobs").document(application.jobId)

        db.runTransaction { transaction ->
            // Check if application already exists in this transaction
            val snapshot = transaction.get(appRef)
            if (snapshot.exists()) {
                throw Exception("Already Applied")
            }

            // Save application and increment job count
            transaction.set(appRef, application.copy(applicationId = uniqueAppId))
            transaction.update(jobRef, "appliedCount", FieldValue.increment(1))

            null
        }.addOnSuccessListener {
            callback(true, "Applied Successfully")
        }.addOnFailureListener { e ->
            callback(false, e.message ?: "Application failed")
        }
    }

    /**
     * NEW: Fetches only the Job IDs that a specific student has applied for.
     */
    fun getAppliedJobIds(studentId: String, callback: (List<String>) -> Unit) {
        db.collection("Applications")
            .whereEqualTo("studentId", studentId)
            .get()
            .addOnSuccessListener { snapshot ->
                val ids = snapshot.documents.map { it.getString("jobId") ?: "" }
                callback(ids)
            }
            .addOnFailureListener { callback(emptyList()) }
    }

    /**
     * NEW: Checks if a student has already applied to a specific job.
     */
    fun checkIfAlreadyApplied(studentId: String, jobId: String, callback: (Boolean) -> Unit) {
        val uniqueId = "${studentId}_${jobId}"
        db.collection("Applications").document(uniqueId).get()
            .addOnSuccessListener { document ->
                callback(document.exists())
            }
            .addOnFailureListener { callback(false) }
    }
}