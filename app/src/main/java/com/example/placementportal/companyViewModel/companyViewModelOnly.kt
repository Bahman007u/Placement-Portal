package com.example.placementportal.companyViewModel

import com.example.placementportal.companyModel.CompanyModel
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.placementportal.companyRepo.CompanyRepo
import com.example.placementportal.jobmodel.JobModel
import com.google.firebase.auth.FirebaseAuth

class CompanyViewModel : ViewModel() {
    private val repo = CompanyRepo()
    private val auth = FirebaseAuth.getInstance()

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> get() = _statusMessage

    private val _isSuccess = MutableLiveData<Boolean>(false)
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    private val _loginSuccess = MutableLiveData<Boolean>(false)
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    private val _companyData = MutableLiveData<CompanyModel?>()
    val companyData: LiveData<CompanyModel?> get() = _companyData

    private val _myJobs = MutableLiveData<List<JobModel>>()
    val myJobs: LiveData<List<JobModel>> get() = _myJobs

    fun signUp(name: String, email: String, pass: String) {
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            _statusMessage.value = "Please fill all fields"
            return
        }
        _isLoading.value = true
        val company = CompanyModel(companyName = name, hrEmail = email)
        repo.registerCompany(company, pass) { success, message ->
            _isLoading.value = false
            _statusMessage.value = message
            _isSuccess.value = success
        }
    }

    fun login(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            _statusMessage.value = "Email and Password are required"
            return
        }
        _isLoading.value = true
        repo.loginCompany(email, pass) { success, message ->
            _isLoading.value = false
            _statusMessage.value = message
            _loginSuccess.value = success
        }
    }

    fun fetchProfile() {
        val user = auth.currentUser
        if (user != null) {
            _isLoading.value = true
            repo.getCompanyDetails(user.uid) { data ->
                _isLoading.value = false
                if (data != null) {
                    _companyData.postValue(data)
                } else {
                    _statusMessage.postValue("Profile not found in database")
                }
            }
        } else {
            _statusMessage.value = "User session expired."
        }
    }

    fun updateProfilePic(uri: Uri) {
        _isLoading.value = true
        repo.uploadCompanyProfilePic(uri) { success, result ->
            _isLoading.value = false
            if (success) {
                _statusMessage.value = "Profile picture updated!"
                fetchProfile()
            } else {
                _statusMessage.value = result ?: "Update failed"
            }
        }
    }

    fun resetStatus() { _statusMessage.value = "" }

    /**
     * UPDATED: Now accepts targetCourse and maps company logo correctly
     */
    fun postNewJob(title: String, desc: String, benefits: String, eligibility: String, course: String, backlogs: Int, cgpa: Double) {
        val user = auth.currentUser ?: return
        val currentCompany = _companyData.value

        val job = JobModel(
            jobId = "", // Handled by Repo
            companyId = user.uid,
            companyName = currentCompany?.companyName ?: "Unknown",
            companyLogoUrl = currentCompany?.profilePicUrl ?: "",
            jobTitle = title,
            description = desc,
            benefits = benefits,
            eligibility = eligibility,
            targetCourse = course,
            minCgpa = cgpa,
            maxBacklogs = backlogs,
            timestamp = System.currentTimeMillis()
        )

        repo.postJob(job) { success, message ->
            _statusMessage.postValue(if (success) "Job Posted Successfully" else message)
        }
    }

    fun fetchMyJobs() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            repo.getJobsByCompany(uid) { jobs ->
                _myJobs.postValue(jobs)
            }
        }
    }

    // Inside CompanyViewModel class
    fun logout() {
        repo.signOut()
        _companyData.value = null
        _myJobs.value = emptyList()
        _loginSuccess.value = false
    }
}