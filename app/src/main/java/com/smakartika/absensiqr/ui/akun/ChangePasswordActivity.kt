package com.smakartika.absensiqr.ui.akun

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smakartika.absensiqr.data.model.ChangePasswordRequest
import com.smakartika.absensiqr.databinding.ActivityChangePasswordBinding
// PASTIKAN IMPORT INI BENAR, merujuk ke sealed class yang kita buat
import com.smakartika.absensiqr.utils.Result

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val viewModel: AkunViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.btnSimpanPassword.setOnClickListener { validateAndSubmit() }
        observeViewModel()
    }

    private fun validateAndSubmit() {
        val currentPass = binding.etCurrentPassword.text.toString()
        val newPass = binding.etNewPassword.text.toString()
        val confirmPass = binding.etConfirmPassword.text.toString()

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPass.length < 8) {
            Toast.makeText(this, "Password baru minimal 8 karakter", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPass != confirmPass) {
            Toast.makeText(this, "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        val request = ChangePasswordRequest(
            currentPassword = currentPass,
            newPassword = newPass,
            newPasswordConfirmation = confirmPass
        )
        viewModel.changePassword(request)
    }

    private fun observeViewModel() {
        viewModel.changePasswordResult.observe(this) { result ->
            // 'when' di sini akan bekerja dengan benar karena 'result'
            // sekarang jelas merujuk pada sealed class kita.
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSimpanPassword.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSimpanPassword.isEnabled = true
                    Toast.makeText(this, result.data, Toast.LENGTH_LONG).show()
                    finish()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSimpanPassword.isEnabled = true
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
