package com.example.placementportal.authRepository

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.placementportal.model.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepo {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val COLLECTION_NAME = "Students"

    fun completeRegistration(student: Student, pdfUri: Uri?, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(student.email, student.password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                if (pdfUri != null) {
                    uploadToCloudinary(pdfUri, "student_resumes") { cloudinaryUrl ->
                        if (cloudinaryUrl != null) {
                            student.resumeUrl = cloudinaryUrl
                            saveToFirestore(uid, student, onResult)
                        } else {
                            onResult(false, "Resume upload failed.")
                        }
                    }
                } else {
                    saveToFirestore(uid, student, onResult)
                }
            }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun logoutUser() {
        auth.signOut()
    }

    fun uploadProfilePic(imageUri: Uri, onResult: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false, "User not logged in")

        uploadToCloudinary(imageUri, "profile_pics") { imageUrl ->
            if (imageUrl != null) {
                firestore.collection(COLLECTION_NAME).document(uid)
                    .update("profilePicUrl", imageUrl)
                    .addOnSuccessListener { onResult(true, imageUrl) }
                    .addOnFailureListener { e -> onResult(false, e.message) }
            } else {
                onResult(false, "Image upload failed")
            }
        }
    }

    private fun uploadToCloudinary(uri: Uri, preset: String, callback: (String?) -> Unit) {
        MediaManager.get().upload(uri)
            .unsigned(preset)
            .option("resource_type", "auto")
            .callback(object : UploadCallback {
                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    callback(resultData?.get("secure_url")?.toString())
                }
                override fun onError(requestId: String?, error: ErrorInfo?) { callback(null) }
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            }).dispatch()
    }

    // FIXED: Added ': Student' type here
    private fun saveToFirestore(uid: String, student: Student, onResult: (Boolean, String?) -> Unit) {
        firestore.collection(COLLECTION_NAME).document(uid)
            .set(student)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun loginUser(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getStudentProfile(onResult: (Student?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(null)
        firestore.collection(COLLECTION_NAME).document(userId)
            .get()
            .addOnSuccessListener { document ->
                onResult(document.toObject(Student::class.java))
            }
            .addOnFailureListener { onResult(null) }
    }
}