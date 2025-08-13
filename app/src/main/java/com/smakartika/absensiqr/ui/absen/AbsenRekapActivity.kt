package com.smakartika.absensiqr.ui.absen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smakartika.absensiqr.R
import com.smakartika.absensiqr.data.model.RekapAbsenResponse
import com.smakartika.absensiqr.databinding.ActivityAbsenRekapBinding
import com.smakartika.absensiqr.ui.absen.JadwalTableAdapter
import com.smakartika.absensiqr.ui.akun.AkunActivity
import com.smakartika.absensiqr.ui.beranda.BerandaActivity
import com.smakartika.absensiqr.ui.izin.IzinActivity
import com.smakartika.absensiqr.utils.Result

class AbsenRekapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAbsenRekapBinding
    private val viewModel: AbsenRekapViewModel by viewModels()
    private lateinit var tableAdapter: RekapTableAdapter
    private var jadwalId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsenRekapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tableAdapter = RekapTableAdapter(this)
        jadwalId = intent.getIntExtra(JadwalTableAdapter.EXTRA_JADWAL_ID, -1)

        if (jadwalId == -1) {
            Toast.makeText(this, "ID Jadwal tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnKembali.setOnClickListener { finish() }
        setupBottomNavigation()
        observeViewModel()
        viewModel.fetchRekapAbsen(jadwalId)
    }

    private fun observeViewModel() {
        viewModel.rekapState.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                    binding.scrollViewTable.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (result.data.rekap.isEmpty()) {
                        binding.tvEmpty.visibility = View.VISIBLE
                        binding.scrollViewTable.visibility = View.GONE
                    } else {
                        binding.tvEmpty.visibility = View.GONE
                        binding.scrollViewTable.visibility = View.VISIBLE
                        updateTable(result.data)
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.tvEmpty.text = result.message
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateTable(data: RekapAbsenResponse) {
        binding.tableLayoutRekap.removeAllViews()

        binding.tableLayoutRekap.addView(tableAdapter.createHeaderRow())

        data.rekap.keys.forEachIndexed { index, siswaId ->
            val row = tableAdapter.createDataRow(siswaId, data, index)
            binding.tableLayoutRekap.addView(row)
        }
    }

    private fun setupBottomNavigation() {
        // Tandai item 'Absensi' sebagai yang aktif
        binding.bottomNavigation.selectedItemId = R.id.nav_absensi
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> {
                    startActivity(Intent(this, BerandaActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_absensi -> true // Kita sudah di sini, tidak perlu aksi
                R.id.nav_izin -> {
                    startActivity(Intent(this, IzinActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_akun -> {
                    startActivity(Intent(this, AkunActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}

