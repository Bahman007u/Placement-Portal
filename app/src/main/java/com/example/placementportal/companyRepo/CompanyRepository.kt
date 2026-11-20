package com.example.placementportal.companyRepo

import com.example.placementportal.companyModel.CompanyModel
import android.net.Uri
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.MediaManager
import com.example.placementportal.jobmodel.JobModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CompanyRepo {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()



    fun registerCompany(company: CompanyModel, pass: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(company.hrEmail, pass)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                // We do NOT save the password to Firestore for security
                val finalCompany = company.copy(companyId = uid, password = "")

                db.collection("Companies").document(uid)
                    .set(finalCompany)
                    .addOnSuccessListener { callback(true, "Registration Successful") }
                    .addOnFailureListener { e -> callback(false, "Database Error: ${e.message}") }
            }
            .addOnFailureListener { e -> callback(false, "Auth Error: ${e.message}") }
    }

    fun loginCompany(email: String, pass: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                db.collection("Companies").document(uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            callback(true, "Login Successful")
                        } else {
                            auth.signOut()
                            callback(false, "This account is not a registered Company.")
                        }
                    }
                    .addOnFailureListener { e -> callback(false, "Lookup Error: ${e.message}") }
            }
            .addOnFailureListener { e -> callback(false, "Invalid Credentials: ${e.message}") }
    }

    fun getCompanyDetails(uid: String, callback: (CompanyModel?) -> Unit) {
        db.collection("Companies").document(uid).get()
            .addOnSuccessListener { document ->
                callback(document.toObject(CompanyModel::class.java))
            }
            .addOnFailureListener { callback(null) }
    }

    fun uploadCompanyProfilePic(imageUri: Uri, onResult: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false, "User not logged in")

        MediaManager.get().upload(imageUri)
            .unsigned("profile_pics")
            .option("resource_type", "image")
            .callback(object : UploadCallback {
                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val imageUrl = resultData?.get("secure_url")?.toString()
                    if (imageUrl != null) {
                        db.collection("Companies").document(uid)
                            .update("profilePicUrl", imageUrl)
                            .addOnSuccessListener { onResult(true, imageUrl) }
                            .addOnFailureListener { e -> onResult(false, e.message) }
                    }
                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onResult(false, error?.description ?: "Upload failed")
                }
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, b: Long, t: Long) {}
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            }).dispatch()
    }

    // Inside CompanyRepo class
    fun postJob(job: JobModel, callback: (Boolean, String?) -> Unit) {
        val jobRef = db.collection("Jobs").document() // Generate a random ID
        val finalJob = job.copy(jobId = jobRef.id)

        jobRef.set(finalJob)
            .addOnSuccessListener {
                callback(true, "Job Posted Successfully")
            }
            .addOnFailureListener { e ->
                callback(false, "Error: ${e.message}")
            }
    }

    // Inside CompanyRepo.kt

    fun getJobsByCompany(companyId: String, callback: (List<JobModel>) -> Unit) {
        db.collection("Jobs")
            .whereEqualTo("companyId", companyId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Converts the Firestore documents directly into a List of JobModels
                val jobs = querySnapshot.toObjects(JobModel::class.java)
                callback(jobs)
            }
            .addOnFailureListener { e ->
                android.util.Log.e("REPO_ERROR", "Error fetching jobs: ${e.message}")
                callback(emptyList())
            }
    }
    // Inside CompanyRepo class
    fun signOut() {
        auth.signOut()
    }
}