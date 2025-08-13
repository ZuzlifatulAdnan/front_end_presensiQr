package com.smakartika.absensiqr.ui.absen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smakartika.absensiqr.databinding.ActivityAbsenSuksesBinding
import com.smakartika.absensiqr.ui.beranda.BerandaActivity

class AbsenSuksesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAbsenSuksesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsenSuksesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBeranda.setOnClickListener {
             val intent = Intent(this, BerandaActivity::class.java)
             intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
             startActivity(intent)
            finish() // Tutup semua activity di atasnya dan kembali ke Beranda
        }
    }
}