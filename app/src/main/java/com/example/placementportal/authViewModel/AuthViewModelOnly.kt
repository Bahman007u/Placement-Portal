package com.example.placementportal.authViewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.placementportal.authRepository.AuthRepo
import com.example.placementportal.model.Student

class AuthViewModelOnly : ViewModel() {
    private val repository = AuthRepo()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _regStatus = MutableLiveData<Pair<Boolean, String?>>()
    val regStatus: LiveData<Pair<Boolean, String?>> = _regStatus

    private val _loginStatus = MutableLiveData<Pair<Boolean, String?>>()
    val loginStatus: LiveData<Pair<Boolean, String?>> = _loginStatus

    private val _studentData = MutableLiveData<Student?>()
    val studentData: LiveData<Student?> = _studentData

    private val _imageUploadStatus = MutableLiveData<Boolean>()
    val imageUploadStatus: LiveData<Boolean> = _imageUploadStatus

    // --- SESSION MANAGEMENT ---

    /**
     * Call this in your LoginActivity's onStart or onCreate
     * to check if the user should skip the login screen.
     */
    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }

    /**
     * Triggered by the Navigation Drawer or Slider click.
     */
    fun logout() {
        repository.logoutUser()
        // Optional: Clear the student data state upon logout
        _studentData.value = null
    }

    // --- AUTHENTICATION ---

    fun register(student: Student, pdfUri: Uri?) {
        _isLoading.value = true
        repository.completeRegistration(student, pdfUri) { success, msg ->
            _isLoading.value = false
            _regStatus.value = Pair(success, msg)
        }
    }

    fun login(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            _loginStatus.value = Pair(false, "Email and Password cannot be empty")
            return
        }
        _isLoading.value = true
        repository.loginUser(email, pass) { success, msg ->
            _isLoading.value = false
            _loginStatus.value = Pair(success, msg)
        }
    }

    // --- PROFILE MANAGEMENT ---

    fun fetchProfile() {
        _isLoading.value = true
        repository.getStudentProfile { student ->
            _isLoading.value = false
            _studentData.postValue(student)
        }
    }

    fun uploadProfileImage(uri: Uri) {
        _isLoading.value = true
        repository.uploadProfilePic(uri) { success, newUrl ->
            _isLoading.postValue(false)
            if (success && newUrl != null) {
                val current = _studentData.value
                current?.profilePicUrl = newUrl
                _studentData.postValue(current)
                _imageUploadStatus.postValue(true)
            } else {
                _imageUploadStatus.postValue(false)
            }
        }
    }
}