package com.smakartika.absensiqr.ui.izin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.smakartika.absensiqr.R
import com.smakartika.absensiqr.data.model.Izin
import com.smakartika.absensiqr.data.remote.ApiClient // Import ApiClient
import com.smakartika.absensiqr.databinding.ActivityDetailIzinBinding
import com.smakartika.absensiqr.ui.absen.AbsenActivity
import com.smakartika.absensiqr.ui.akun.AkunActivity
import com.smakartika.absensiqr.ui.beranda.BerandaActivity
import com.smakartika.absensiqr.utils.Result
import java.text.SimpleDateFormat
import java.util.Locale

class DetailIzinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailIzinBinding
    private val viewModel: IzinViewModel by viewModels()

    companion object {
        const val EXTRA_IZIN_ID = "IZIN_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailIzinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val izinId = intent.getIntExtra(EXTRA_IZIN_ID, -1)
        if (izinId != -1) {
            viewModel.fetchIzinDetail(izinId)
        } else {
            Toast.makeText(this, "ID Izin tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnKembali.setOnClickListener { finish() }
        setupBottomNavigation()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.izinDetailState.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    populateData(result.data)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun populateData(izin: Izin) {
        binding.tvNamaSiswa.text = izin.siswa?.user?.name ?: "N/A"
        binding.tvKelas.text = izin.siswa?.kelas?.nama ?: "N/A"
        binding.tvTanggalIzin.text = formatTanggal(izin.tanggalIzin)
        binding.tvAlasan.text = izin.alasan

        // PERBAIKAN: Ambil base URL dari ApiClient
        val imageUrl = "${ApiClient.BASE_URL}img/izin/${izin.buktiSurat}"

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_image_placeholder)
            .into(binding.ivBuktiSurat)
    }

    private fun formatTanggal(tanggal: String?): String {
        if (tanggal.isNullOrEmpty()) return "N/A"
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatter.format(parser.parse(tanggal)!!)
        } catch (e: Exception) {
            tanggal
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_izin
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> { startActivity(Intent(this, BerandaActivity::class.java)); overridePendingTransition(0,0); finish(); true }
                R.id.nav_absensi -> { startActivity(Intent(this, AbsenActivity::class.java)); overridePendingTransition(0,0); finish(); true }
                R.id.nav_izin ->true
                R.id.nav_akun -> { startActivity(Intent(this, AkunActivity::class.java)); overridePendingTransition(0,0); finish(); true }
                else -> false
            }
        }
    }
}