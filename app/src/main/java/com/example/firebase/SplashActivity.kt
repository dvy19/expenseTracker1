package com.example.firebase


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        // Optional splash delay (e.g. 2 seconds)
        Handler(Looper.getMainLooper()).postDelayed({

            val currentUser = auth.currentUser

            if (currentUser != null) {
                // ✅ User already logged in
                startActivity(Intent(this, UserBudget::class.java))
            } else {
                // ❌ User not logged in
                startActivity(Intent(this, LoginActivity::class.java))
            }

            finish()

        }, 2000)
    }
}
