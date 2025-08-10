package com.smakartika.absensiqr.ui.akun

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.smakartika.absensiqr.R
import com.smakartika.absensiqr.data.local.SessionManager
import com.smakartika.absensiqr.data.model.User
import com.smakartika.absensiqr.databinding.ActivityAkunBinding
import com.smakartika.absensiqr.ui.absen.AbsenActivity
import com.smakartika.absensiqr.ui.beranda.BerandaActivity
import com.smakartika.absensiqr.ui.izin.IzinActivity
import com.smakartika.absensiqr.utils.Result

class AkunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAkunBinding
    private val viewModel: AkunViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        setSupportActionBar(binding.toolbar)
        setupClickListeners()
        observeViewModel()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        // Selalu fetch data terbaru saat kembali ke halaman ini
        viewModel.fetchProfile()
    }

    private fun setupClickListeners() {
        binding.btnEdit.setOnClickListener {
            currentUser?.let {
                val intent = Intent(this, EditAkunActivity::class.java)
                intent.putExtra("EXTRA_USER", it)
                startActivity(intent)
            }
        }

        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        binding.btnKeluar.setOnClickListener {
            // Tambahkan dialog konfirmasi sebelum keluar
            sessionManager.clearSession()
            // Navigasi ke LoginActivity...
        }
    }

    private fun observeViewModel() {
        viewModel.profileState.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.profileContent.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.profileContent.visibility = View.VISIBLE
                    this.currentUser = result.data
                    populateData(result.data)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun populateData(user: User) {
        // Header
        binding.tvNamaHeader.text = user.name
//        binding.tvRoleHeader.text = user.role
        val imageUrl = "http://192.168.1.6:8000/img/user/${user.image}" // Ganti dengan IP Anda
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_person_placeholder)
            .error(R.drawable.ic_person_placeholder)
            .into(binding.ivProfile)

        // Informasi Akun
        binding.tvEmail.text = user.email

        // Tampilkan data berdasarkan role, karena kita fokus ke Siswa
        if (user.role == "Siswa" && user.siswa != null) {
            binding.cardDataSiswa.visibility = View.VISIBLE
            binding.cardDataWali.visibility = View.VISIBLE

            binding.tvNoTelepon.text = user.siswa.noTelepon ?: "-"
            binding.tvNis.text = user.siswa.nis ?: "-"
            binding.tvNisn.text = user.siswa.nisn ?: "-"
            binding.tvKelas.text = user.siswa.kelas?.nama ?: "-"
            binding.tvJenisKelamin.text = user.siswa.jenisKelamin ?: "-"

            user.siswa.ortu?.let { ortu ->
                binding.tvNamaWali.text = ortu.nama ?: "-"
                binding.tvNoTeleponWali.text = ortu.noTelepon ?: "-"
            }
        } else {
            // Sembunyikan kartu jika bukan siswa
            binding.cardDataSiswa.visibility = View.GONE
            binding.cardDataWali.visibility = View.GONE
        }
    }
    private fun setupBottomNavigation() {
        // Tandai item 'Akun' sebagai yang aktif
        binding.bottomNavigation.selectedItemId = R.id.nav_akun

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> {
                     startActivity(Intent(this, BerandaActivity::class.java))
                     overridePendingTransition(0, 0)
                     finish() // Tutup activity ini agar tidak menumpuk
                    true
                }
                R.id.nav_absensi -> {
                     startActivity(Intent(this, AbsenActivity::class.java))
                     overridePendingTransition(0, 0)
                     finish()
                    true
                }
                R.id.nav_izin -> {
                     startActivity(Intent(this, IzinActivity::class.java))
                     overridePendingTransition(0, 0)
                     finish()
                    true
                }
                R.id.nav_akun -> {
                    // Kita sudah di sini, tidak perlu melakukan apa-apa
                    true
                }
                else -> false
            }
        }
    }
}