package com.smakartika.absensiqr.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.smakartika.absensiqr.data.local.SessionManager
import com.smakartika.absensiqr.databinding.ActivitySplashBinding
import com.smakartika.absensiqr.ui.auth.LoginActivity
import com.smakartika.absensiqr.ui.beranda.BerandaActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Gunakan Handler untuk memberi jeda sesaat sebelum pindah halaman
        Handler(Looper.getMainLooper()).postDelayed({
            checkLoginStatus()
        }, 2000) // Jeda 2 detik
    }

    private fun checkLoginStatus() {
        // Ambil token dari SessionManager
        val token = sessionManager.fetchAuthToken()

        if (token.isNullOrEmpty()) {
            // Jika token tidak ada, pergi ke Halaman Login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else {
            // Jika token ada, pergi ke Halaman Beranda
            val intent = Intent(this, BerandaActivity::class.java)
            startActivity(intent)
        }

        // Tutup SplashActivity agar tidak bisa kembali ke sini
        finish()
    }
}