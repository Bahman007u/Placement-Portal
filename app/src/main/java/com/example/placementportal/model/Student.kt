package com.example.placementportal.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Student(
    var fullName: String = "",
    var email: String = "",
    var password: String = "",
    var phone: String = "",
    var branch: String = "",
    var cgpa: String = "",
    var backlogs: String = "",
    var startYear: String = "",
    var endYear: String = "",
    var skills: String = "",
    var linkedin: String = "",
    var resumeUrl: String = "",
    var profilePicUrl: String = ""// Cloudinary link will be saved here
) : Parcelable