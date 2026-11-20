package com.example.placementportal.studentViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.placementportal.applicationModel.ApplicationModel
import com.example.placementportal.jobmodel.JobModel
import com.example.placementportal.model.Student
import com.example.placementportal.studentRepo.StudentRepository
import com.google.firebase.auth.FirebaseAuth

class StudentViewModel : ViewModel() {

    private val repo = StudentRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _allJobs = MutableLiveData<List<JobModel>>()
    val allJobs: LiveData<List<JobModel>> get() = _allJobs

    private val _studentData = MutableLiveData<Student?>()
    val studentData: LiveData<Student?> get() = _studentData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // NEW: LiveData to track IDs of jobs the student has already applied to
    private val _appliedJobIds = MutableLiveData<List<String>>()
    val appliedJobIds: LiveData<List<String>> get() = _appliedJobIds

    /**
     * Fetches all jobs from Firestore
     */
    fun fetchAllJobs() {
        _isLoading.value = true
        repo.getAllJobs { jobs ->
            _isLoading.value = false
            _allJobs.postValue(jobs)
        }
    }

    /**
     * Fetches the logged-in student's profile (Branch, CGPA, etc.)
     */
    fun fetchStudentProfile() {
        val uid = auth.currentUser?.uid ?: return
        _isLoading.value = true
        repo.getStudentDetails(uid) { student ->
            _isLoading.value = false
            if (student != null) {
                _studentData.postValue(student)
            } else {
                _errorMessage.postValue("Failed to load profile data.")
            }
        }
    }

    /**
     * NEW: Fetches the list of Job IDs the student has applied for.
     * Useful for filtering the "Applied Jobs" screen and checking button states.
     */
    fun fetchAppliedJobIds(studentId: String, onResult: (List<String>) -> Unit) {
        repo.getAppliedJobIds(studentId) { ids ->
            _appliedJobIds.postValue(ids)
            onResult(ids)
        }
    }

    /**
     * NEW: Check if student has already applied to a specific job (Single check)
     */
    fun checkIfAlreadyApplied(jobId: String, callback: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        repo.checkIfAlreadyApplied(uid, jobId) { exists ->
            callback(exists)
        }
    }

    /**
     * NEW: Orchestrates the application process
     */
    fun applyToJob(application: ApplicationModel, callback: (Boolean, String?) -> Unit) {
        _isLoading.value = true
        repo.applyToJob(application) { success, message ->
            _isLoading.value = false
            if (success) {
                // Refresh applied IDs so UI stays in sync
                fetchAppliedJobIds(application.studentId) {}
            }
            callback(success, message)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}