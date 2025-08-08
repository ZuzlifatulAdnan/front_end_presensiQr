package com.smakartika.absensiqr.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.smakartika.absensiqr.R
import com.smakartika.absensiqr.ui.auth.LoginActivity

class SplashActivity : AppCompatActivity() {

    // Durasi splash screen dalam milidetik
    private val SPLASH_TIME_OUT: Long = 3000 // 3 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Sembunyikan ActionBar jika ada
        supportActionBar?.hide()

        // Handler untuk menunda perpindahan ke MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            // Intent untuk memulai MainActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // Tutup SplashActivity agar tidak bisa kembali dengan tombol back
            finish()
        }, SPLASH_TIME_OUT)
    }
}