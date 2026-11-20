package com.example.placementportal.jobmodel

import androidx.annotation.Keep

@Keep
data class JobModel(
    val jobId: String = "",
    val companyId: String = "",
    val companyName: String = "",
    val companyLogoUrl: String = "",
    val jobTitle: String = "",
    val description: String = "",
    val benefits: String = "",
    val eligibility: String = "",
    val targetCourse: String = "All Courses",
    val maxBacklogs: Int = 0, // NEW
    val minCgpa: Double = 0.0, // NEW
    val appliedCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)