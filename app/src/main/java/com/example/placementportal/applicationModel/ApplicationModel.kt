package com.example.placementportal.applicationModel

import androidx.annotation.Keep

@Keep
data class ApplicationModel(
    val applicationId: String = "",
    val jobId: String = "",
    val companyId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val studentEmail: String = "",
    val studentGpa: String = "",
    val studentSkills: String = "",
    val studentProfilePic: String = "",
    val timestamp: Long = 0L
)