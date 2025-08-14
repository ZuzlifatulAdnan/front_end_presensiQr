package com.smakartika.absensiqr.ui.izin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.smakartika.absensiqr.databinding.ActivityInputIzinBinding
import com.smakartika.absensiqr.utils.Result
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InputIzinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputIzinBinding
    private val viewModel: IzinViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var cameraImageUri: Uri? = null
    private var selectedDate: String? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivBuktiPreview.setImageURI(it)
            binding.layoutPlaceholder.visibility = View.GONE
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            cameraImageUri?.let {
                selectedImageUri = it
                binding.ivBuktiPreview.setImageURI(it)
                binding.layoutPlaceholder.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputIzinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListener()
        observeViewModel()
    }

    private fun setupClickListener() {
        binding.btnKembali.setOnClickListener { finish() }
        binding.frameBukti.setOnClickListener {
            showImageSourceDialog()
        }
        binding.tilTanggal.setEndIconOnClickListener {
            showDatePicker()
        }
        binding.etTanggal.setOnClickListener {
            showDatePicker()
        }
        binding.btnSimpan.setOnClickListener {
            submitIzin()
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Ambil Foto dari Kamera", "Pilih dari Galeri")
        AlertDialog.Builder(this)
            .setTitle("Pilih Sumber Gambar")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> launchCamera()
                    1 -> galleryLauncher.launch("image/*")
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun launchCamera() {
        try {
            val imageFile = createImageFile()
            cameraImageUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.provider",
                imageFile
            )
            // PERBAIKAN: Gunakan safe call untuk memastikan cameraImageUri tidak null
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
        return File.createTempFile("IZIN_${timeStamp}_", ".jpg", storageDir)
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Pilih Tanggal Izin")
            .build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = sdf.format(Date(selection))
            val displaySdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.etTanggal.setText(displaySdf.format(Date(selection)))
        }
        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun submitIzin() {
        val tanggal = selectedDate
        val alasan = binding.etAlasan.text.toString().trim()
        val imageUri = selectedImageUri

        if (tanggal.isNullOrEmpty()) {
            Toast.makeText(this, "Tanggal izin wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }
        if (alasan.isEmpty()) {
            binding.etAlasan.error = "Alasan wajib diisi"
            return
        }
        if (imageUri == null) {
            Toast.makeText(this, "Bukti surat wajib diunggah", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.storeIzin(tanggal, alasan, imageUri)
    }

    private fun observeViewModel() {
        viewModel.storeIzinResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSimpan.isEnabled = false
                    binding.btnSimpan.text = "Mengirim..."
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val intent = Intent(this, IzinSuksesActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSimpan.isEnabled = true
                    binding.btnSimpan.text = "Simpan"
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}