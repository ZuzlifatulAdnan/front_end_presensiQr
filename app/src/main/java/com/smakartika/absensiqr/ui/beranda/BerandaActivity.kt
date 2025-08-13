package com.smakartika.absensiqr.ui.beranda

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.smakartika.absensiqr.R
import com.smakartika.absensiqr.data.model.UserData
import com.smakartika.absensiqr.databinding.ActivityBerandaBinding
import com.smakartika.absensiqr.ui.absen.AbsenActivity
import com.smakartika.absensiqr.ui.absen.JadwalTableAdapter
import com.smakartika.absensiqr.ui.akun.AkunActivity
import com.smakartika.absensiqr.ui.izin.IzinActivity

class BerandaActivity : AppCompatActivity() {


    private lateinit var binding: ActivityBerandaBinding
    private val viewModel: BerandaViewModel by viewModels()
    private lateinit var jadwalAdapter: JadwalAdapter
    private var selectedJadwalId: Int? = null

    private val qrScannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val intentResult: IntentResult? = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
        if (intentResult != null && intentResult.contents != null) {
            val scannedData = intentResult.contents

            // Saat ini kita punya ID Jadwal dan Data dari QR code
            Toast.makeText(
                this,
                "Scan Berhasil!\nJadwal ID: $selectedJadwalId\nIsi QR: $scannedData",
                Toast.LENGTH_LONG
            ).show()

        } else {
            Toast.makeText(this, "Scan dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBerandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupFilterSpinner()
        setupBottomNavigation()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.berandaState.observe(this) { state ->
            binding.progressBar.visibility = if (state is BerandaState.Loading) View.VISIBLE else View.GONE
            when (state) {
                is BerandaState.Success -> {
                    val response = state.data
                    setupUserHeader(response.user)

                    response.jadwal.let { jadwalPaginator ->
                        jadwalAdapter.submitList(jadwalPaginator.data)
                        setupPagination(jadwalPaginator.currentPage, jadwalPaginator.lastPage)
                    }
                }
                is BerandaState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                is BerandaState.Loading -> { /* Handled above */ }
            }
        }
    }

    private fun setupUserHeader(data: UserData?) {
        data?.let { userData ->
            binding.tvGreeting.text = "Hi, ${userData.name}"
            Glide.with(this).load(userData.imageUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_person_placeholder) // Diubah
                .error(R.drawable.ic_person_placeholder)      // Diubah
                .into(binding.ivProfile)
        }
    }
    private fun setupFilterSpinner() {
        val hariArray = resources.getStringArray(R.array.list_hari)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hariArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHari.adapter = adapter

        binding.spinnerHari.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val hariTerpilih = if (position == 0) null else hariArray[position]

                // LANGSUNG PANGGIL: Activity hanya memberi perintah, tidak perlu tahu state sebelumnya.
                viewModel.fetchBerandaData(page = 1, hari = hariTerpilih)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupPagination(currentPage: Int, lastPage: Int) {
        binding.layoutPagination.removeAllViews()
        for (i in 1..lastPage) {
            val pageButton = TextView(this).apply {
                text = i.toString()
                textSize = 16f
                setPadding(24, 16, 24, 16)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { marginEnd = 16 }

                if (i == currentPage) {
                    setBackgroundResource(R.drawable.pagination_button_background_selected)
                    // PERBAIKAN: Ganti 'context' menjadi 'this@BerandaActivity' atau cukup 'this'
                    setTextColor(ContextCompat.getColor(this@BerandaActivity, android.R.color.white))
                    setTypeface(null, Typeface.BOLD)
                } else {
                    setBackgroundResource(R.drawable.pagination_button_background)
                    // PERBAIKAN: Ganti 'context' menjadi 'this@BerandaActivity' atau cukup 'this'
                    setTextColor(ContextCompat.getColor(this@BerandaActivity, R.color.colorPrimary))
                }
                setOnClickListener {
                    // LANGSUNG PANGGIL: Biarkan ViewModel yang menangani logikanya.
                    viewModel.fetchBerandaData(page = i)
                }
            }
            binding.layoutPagination.addView(pageButton)
        }
    }

    private fun setupRecyclerView() {
        jadwalAdapter = JadwalAdapter { jadwalItem ->
            // Simpan ID jadwal yang akan di-scan
            selectedJadwalId = jadwalItem.id

            // Konfigurasi dan mulai scanner
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Arahkan kamera ke QR Code Presensi")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(true)

            // Jalankan scanner menggunakan launcher
            qrScannerLauncher.launch(integrator.createScanIntent())
        }
        binding.rvJadwal.apply {
            adapter = jadwalAdapter
            layoutManager = LinearLayoutManager(this@BerandaActivity)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_beranda
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> true
                 R.id.nav_absensi -> { startActivity(Intent(this, AbsenActivity::class.java)); overridePendingTransition(0,0); finish(); true }
                 R.id.nav_izin -> { startActivity(Intent(this, IzinActivity::class.java)); overridePendingTransition(0,0); finish(); true }
                 R.id.nav_akun -> { startActivity(Intent(this, AkunActivity::class.java)); overridePendingTransition(0,0); finish(); true }
                else -> false
            }
        }
    }
}