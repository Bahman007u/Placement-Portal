package com.example.placementportal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.placementportal.CompanyProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

        // Read navigation flags
        val isFirstTime = sharedPref.getBoolean("isFirstTime", true)
        val savedRole = sharedPref.getString("userRole", "")

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = auth.currentUser

            when {
                // 1. BRAND NEW USER: Never seen the app before
                isFirstTime -> {
                    startActivity(Intent(this, SelectionActivity::class.java))
                    finish()
                }

                // 2. RETURNING USER (NOT LOGGED IN): Send to their specific Login screen
                currentUser == null -> {
                    navigateToLogin(savedRole)
                }

                // 3. LOGGED IN USER: Verify role via Firestore for 100% accuracy
                else -> {
                    verifyRoleAndNavigate(currentUser.uid, savedRole)
                }
            }
        }, 2500) // 2.5 seconds is the "sweet spot" for splash screens
    }

    /**
     * Sends user to the correct Login screen based on their last selected role
     */
    private fun navigateToLogin(role: String?) {
        val intent = when (role) {
            "Student" -> Intent(this, Login_Activity::class.java)
            "Company" -> Intent(this, CompanyLoginActivity::class.java)
            "Admin"   -> Intent(this, AdminLoginActivity::class.java)
            else      -> Intent(this, SelectionActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    /**
     * The "Perfect" Navigation Logic:
     * Checks Firestore to ensure the UID matches the role.
     */
    private fun verifyRoleAndNavigate(uid: String, fallbackRole: String?) {
        // Check Students collection first
        db.collection("Students").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Update SharedPreferences just in case it was wrong
                    updateLocalRole("Student")
                    startActivity(Intent(this, StudentDashboard::class.java))
                    finish()
                } else {
                    // If not a student, check Companies
                    db.collection("Companies").document(uid).get()
                        .addOnSuccessListener { companyDoc ->
                            if (companyDoc.exists()) {
                                updateLocalRole("Company")
                                startActivity(Intent(this, CompanyProfileActivity::class.java))
                            } else {
                                // Fallback: If UID exists but no data in either, force re-login
                                auth.signOut()
                                startActivity(Intent(this, SelectionActivity::class.java))
                            }
                            finish()
                        }
                }
            }
            .addOnFailureListener {
                // Offline fallback: Trust SharedPreferences if network fails
                Toast.makeText(this, "Offline Mode: Using saved role", Toast.LENGTH_SHORT).show()
                val intent = when (fallbackRole) {
                    "Student" -> Intent(this, StudentDashboard::class.java)
                    "Company" -> Intent(this, CompanyProfileActivity::class.java)
                    else      -> Intent(this, SelectionActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
    }

    private fun updateLocalRole(role: String) {
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("userRole", role).apply()
    }
}