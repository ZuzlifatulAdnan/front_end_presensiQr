package com.smakartika.absensiqr.ui.izin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.smakartika.absensiqr.R
import com.smakartika.absensiqr.data.model.Izin
import com.smakartika.absensiqr.databinding.ActivityIzinBinding
import com.smakartika.absensiqr.ui.absen.AbsenActivity
import com.smakartika.absensiqr.ui.akun.AkunActivity
import com.smakartika.absensiqr.ui.beranda.BerandaActivity
import com.smakartika.absensiqr.utils.Result
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IzinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIzinBinding
    private val viewModel: IzinViewModel by viewModels()
    private lateinit var tableAdapter: IzinTableAdapter
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIzinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tableAdapter = IzinTableAdapter(this) { izin ->
            val intent = Intent(this, DetailIzinActivity::class.java)
            intent.putExtra("IZIN_ID", izin.id)
            startActivity(intent)
        }

        setupClickListener()
        setupBottomNavigation()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchIzinList(1, null, null)
    }

    private fun setupClickListener() {
        binding.btnTambahIzin.setOnClickListener {
            startActivity(Intent(this, InputIzinActivity::class.java))
        }

        binding.tilTanggalFilter.setEndIconOnClickListener { showDatePicker() }
        binding.etTanggalFilter.setOnClickListener { showDatePicker() }

        binding.btnFilter.setOnClickListener {
            val search = binding.etSearchFilter.text.toString().trim()
            viewModel.fetchIzinList(1, selectedDate, search.ifEmpty { null })
        }

        binding.btnReset.setOnClickListener {
            binding.etTanggalFilter.text = null
            binding.etSearchFilter.text = null
            selectedDate = null
            viewModel.fetchIzinList(1, null, null)
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Pilih Tanggal")
            .build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = sdf.format(Date(selection))
            val displaySdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.etTanggalFilter.setText(displaySdf.format(Date(selection)))
        }
        datePicker.show(supportFragmentManager, "DATE_PICKER_FILTER")
    }

    private fun observeViewModel() {
        viewModel.izinListState.observe(this) { result ->
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
                    binding.scrollViewTable.visibility = View.GONE
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateTable(data: List<Izin>) {
        binding.tableLayoutIzin.removeAllViews()
        binding.tableLayoutIzin.addView(tableAdapter.createHeaderRow())
        data.forEachIndexed { index, izin ->
            val row = tableAdapter.createDataRow(izin, index)
            binding.tableLayoutIzin.addView(row)
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
