package com.smakartika.absensiqr.ui.absen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smakartika.absensiqr.databinding.ActivityAbsenBinding
import com.smakartika.absensiqr.utils.Result
import com.smakartika.absensiqr.R
 import com.smakartika.absensiqr.ui.beranda.BerandaActivity
 import com.smakartika.absensiqr.ui.izin.IzinActivity
 import com.smakartika.absensiqr.ui.akun.AkunActivity

class AbsenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAbsenBinding
    private val viewModel: AbsenViewModel by viewModels()
    private lateinit var tableAdapter: JadwalTableAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tableAdapter = JadwalTableAdapter(this)

        setupFilter()
        observeViewModel()
        setupBottomNavigation()
    }

    private fun setupFilter() {
        val hariOptions = listOf("Semua Hari", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, hariOptions)
        binding.actvHari.setAdapter(adapter)

        binding.btnFilter.setOnClickListener {
            val searchQuery = binding.etSearch.text.toString().trim()
            val hari = binding.actvHari.text.toString()
            viewModel.applyFilter(searchQuery.ifEmpty { null }, hari)
        }

        binding.btnReset.setOnClickListener {
            binding.etSearch.text = null
            binding.actvHari.setText("", false)
            viewModel.resetFilter()
        }
    }

    private fun observeViewModel() {
        viewModel.jadwalState.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                    binding.scrollViewTable.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (result.data.isEmpty()) {
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

    private fun updateTable(jadwalList: List<com.smakartika.absensiqr.data.model.JadwalPelajaran>) {
        // Hapus semua baris data sebelumnya (indeks 0 adalah header)
        if (binding.tableLayoutJadwal.childCount > 1) {
            binding.tableLayoutJadwal.removeViews(1, binding.tableLayoutJadwal.childCount - 1)
        }
        // Tambahkan baris baru
        jadwalList.forEachIndexed { index, jadwal ->
            val row = tableAdapter.createRowView(jadwal, index)
            binding.tableLayoutJadwal.addView(row)
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
