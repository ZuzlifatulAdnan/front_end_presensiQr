package com.smakartika.absensiqr.ui.izin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smakartika.absensiqr.databinding.ActivityIzinSuksesBinding

class IzinSuksesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIzinSuksesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIzinSuksesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menangani klik pada tombol "Kembali ke Daftar Izin"
        binding.btnKembaliKeIzin.setOnClickListener {
            // Buat intent untuk kembali ke IzinActivity
            val intent = Intent(this, IzinActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(intent)
            finish() // Tutup activity ini
        }
    }
}