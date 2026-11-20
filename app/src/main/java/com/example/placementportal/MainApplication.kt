package com.example.placementportal

import android.app.Application
import com.cloudinary.android.MediaManager

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // This runs ONLY ONCE when the app process starts
        val config = mapOf(
            "cloud_name" to "dvl6baquu", // Your Cloudinary name
            "secure" to true
        )
        MediaManager.init(this, config)
    }
}