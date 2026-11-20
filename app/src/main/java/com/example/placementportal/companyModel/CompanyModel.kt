package com.example.placementportal.companyModel

import androidx.annotation.Keep

@Keep // Prevents Proguard from breaking the model in release builds
data class CompanyModel(
    val companyId: String = "",
    val companyName: String = "",
    val hrEmail: String = "",
    val industry: String = "",
    val website: String = "",
    val description: String = "",
    val profilePicUrl: String = "",
    val password: String = "",
    // Keep this for logic, Firestore will ignore it if not set
)