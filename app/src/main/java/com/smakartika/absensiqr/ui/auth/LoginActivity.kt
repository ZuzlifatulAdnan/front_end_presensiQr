package com.smakartika.absensiqr.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smakartika.absensiqr.data.local.SessionManager
import com.smakartika.absensiqr.databinding.ActivityLoginBinding
import com.smakartika.absensiqr.ui.beranda.BerandaActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    // Inisialisasi ViewModel menggunakan KTX delegate
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Panggil fungsi untuk listener dan observer
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Validasi input sebelum memanggil ViewModel
            if (validateInput(email, password)) {
                loginViewModel.executeLogin(email, password)
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            // Buka link lupa password di browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://situs-anda.com/lupa-password"))
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        // Amati perubahan pada loginResult LiveData dari ViewModel
        loginViewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Loading -> showLoading(true)
                is LoginResult.Success -> {
                    showLoading(false)
                    val response = result.data

                    // Logika untuk cek role user
                    if (response.token != null && response.user?.role?.equals("Siswa", ignoreCase = true) == true) {
                        handleLoginSuccess(response.token)
                    } else if (response.token != null) {
                        Toast.makeText(this, "Hanya Siswa yang dapat login.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Username atau password salah.", Toast.LENGTH_LONG).show()
                    }
                }
                is LoginResult.Error -> {
                    showLoading(false)
                    val errorMessage = result.message ?: "Terjadi kesalahan, coba lagi."
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun handleLoginSuccess(token: String) {
        Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()

        // Simpan token ke session
        val sessionManager = SessionManager(this)
        sessionManager.saveAuthToken(token)

        // Pindah ke BerandaActivity dan hapus riwayat activity sebelumnya
        val intent = Intent(this, BerandaActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun validateInput(email: String, password: String): Boolean {
        binding.etEmail.error = null
        binding.etPassword.error = null
        if (email.isEmpty()) {
            binding.etEmail.error = "Email tidak boleh kosong"
            binding.etEmail.requestFocus()
            return false
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Password tidak boleh kosong"
            binding.etPassword.requestFocus()
            return false
        }
        return true
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
    }
}