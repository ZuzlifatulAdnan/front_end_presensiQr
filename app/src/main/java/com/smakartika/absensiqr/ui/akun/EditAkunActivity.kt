package com.smakartika.absensiqr.ui.akun

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.smakartika.absensiqr.R
import com.smakartika.absensiqr.data.model.User
import com.smakartika.absensiqr.data.remote.ApiClient
import com.smakartika.absensiqr.databinding.ActivityEditAkunBinding
import com.smakartika.absensiqr.utils.Result
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditAkunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditAkunBinding
    private val viewModel: AkunViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var cameraImageUri: Uri? = null
    private var currentUser: User? = null
    private var selectedKelasId: Int? = null

    // Launcher untuk memilih gambar dari galeri
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this).load(it).into(binding.ivProfileEdit)
        }
    }

    // Launcher untuk mengambil gambar dari kamera
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            cameraImageUri?.let {
                selectedImageUri = it
                Glide.with(this).load(it).into(binding.ivProfileEdit)
            }
        }
    }

    // Launcher untuk meminta izin galeri
    private val requestGalleryPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(this, "Izin galeri diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher untuk meminta izin kamera
    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(this, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EXTRA_USER", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("EXTRA_USER") as? User
        }

        if (currentUser == null || currentUser?.role != "Siswa") {
            Toast.makeText(this, "Data siswa tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel.fetchEditProfileData()

        setupUI()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupUI() {
        populateData(currentUser!!)
    }

    private fun populateData(user: User) {
        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        val imageUrl = "${ApiClient.BASE_URL}img/user/${user.image}"
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_person_placeholder)
            .error(R.drawable.ic_person_placeholder)
            .into(binding.ivProfileEdit)

        user.siswa?.let { siswa ->
            binding.etNis.setText(siswa.nis)
            binding.etNisn.setText(siswa.nisn)
            binding.etNoTeleponSiswa.setText(siswa.noTelepon)
            binding.actvJenisKelaminSiswa.setText(siswa.jenisKelamin, false)
            selectedKelasId = siswa.kelas?.id
        }

        user.siswa?.ortu?.let { ortu ->
            binding.etNamaWali.setText(ortu.nama)
            binding.etAlamatWali.setText(ortu.alamat)
            binding.etPekerjaanWali.setText(ortu.pekerjaan)
            binding.etNoTeleponWali.setText(ortu.noTelepon)
            binding.actvHubunganWali.setText(ortu.hubungan, false)
        }

        val jenisKelaminOptions = listOf("Laki-laki", "Perempuan")
        val hubunganOptions = listOf("Ayah", "Ibu", "Wali")
        val adapterJk = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, jenisKelaminOptions)
        val adapterHubungan = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, hubunganOptions)
        binding.actvJenisKelaminSiswa.setAdapter(adapterJk)
        binding.actvHubunganWali.setAdapter(adapterHubungan)
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.fabEditImage.setOnClickListener { showImageSourceDialog() }
        binding.btnSimpan.setOnClickListener {
            val fields = mutableMapOf<String, String>()
            fields["name"] = binding.etName.text.toString()
            fields["email"] = binding.etEmail.text.toString()
            fields["nis"] = binding.etNis.text.toString()
            fields["nisn"] = binding.etNisn.text.toString()
            fields["kelas_id"] = selectedKelasId?.toString() ?: ""
            fields["jenis_kelamin"] = binding.actvJenisKelaminSiswa.text.toString()
            fields["no_telepon"] = binding.etNoTeleponSiswa.text.toString()
            fields["nama"] = binding.etNamaWali.text.toString()
            fields["alamat"] = binding.etAlamatWali.text.toString()
            fields["pekerjaan"] = binding.etPekerjaanWali.text.toString()
            fields["no_telepon_ortu"] = binding.etNoTeleponWali.text.toString()
            fields["hubungan"] = binding.actvHubunganWali.text.toString()

            viewModel.updateProfile(fields, selectedImageUri)
        }
    }

    private fun observeViewModel() {
        viewModel.kelasList.observe(this) { kelasList ->
            val kelasNames = kelasList.map { it.nama }
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, kelasNames)
            binding.actvKelas.setAdapter(adapter)

            currentUser?.siswa?.kelas?.let { kelasSekarang ->
                binding.actvKelas.setText(kelasSekarang.nama, false)
            }

            binding.actvKelas.setOnItemClickListener { _, _, position, _ ->
                selectedKelasId = kelasList[position].id
            }
        }

        viewModel.updateResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSimpan.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSimpan.isEnabled = true
                    Toast.makeText(this, result.data, Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSimpan.isEnabled = true
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Ambil Foto dari Kamera", "Pilih dari Galeri")
        AlertDialog.Builder(this)
            .setTitle("Ubah Foto Profil")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> checkGalleryPermission()
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun checkGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            galleryLauncher.launch("image/*")
        } else {
            requestGalleryPermissionLauncher.launch(permission)
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        try {
            val imageFile = createImageFile()
            cameraImageUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.provider",
                imageFile
            )
            // PERBAIKAN: Gunakan safe call (?.) dan scope function (let)
            // untuk memastikan cameraImageUri tidak null saat dipanggil.
            cameraImageUri?.let { uri ->
                cameraLauncher.launch(uri)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal membuka kamera: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }
}