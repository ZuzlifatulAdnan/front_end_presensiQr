package com.smakartika.absensiqr.ui.absen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.journeyapps.barcodescanner.CaptureManager
import com.smakartika.absensiqr.data.model.KelasDetail
import com.smakartika.absensiqr.databinding.ActivityAbsenScanBinding
import com.smakartika.absensiqr.ui.absen.JadwalTableAdapter
import com.smakartika.absensiqr.utils.Result

class AbsenScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAbsenScanBinding
    private val viewModel: AbsenScanViewModel by viewModels()
    private var captureManager: CaptureManager? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var jadwalId: Int = -1
    private var tokenQr: String? = null
    private var currentLat: Double? = null
    private var currentLong: Double? = null

    // Handler dan CancellationToken untuk timeout lokasi
    private val locationTimeoutHandler = Handler(Looper.getMainLooper())
    private var locationCancellationTokenSource: CancellationTokenSource? = null

    // Launcher untuk meminta izin kamera
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startScanner()
            } else {
                Toast.makeText(this, "Izin kamera diperlukan untuk scan QR", Toast.LENGTH_LONG).show()
                finish()
            }
        }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Izin lokasi diperlukan untuk absensi", Toast.LENGTH_LONG).show()
                binding.btnAbsenSekarang.isEnabled = false
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsenScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jadwalId = intent.getIntExtra(JadwalTableAdapter.EXTRA_JADWAL_ID, -1)
        if (jadwalId == -1) {
            Toast.makeText(this, "ID Jadwal tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setupClickListener()
        observeViewModel()
        checkLocationPermission()
        checkCameraPermission()

        viewModel.fetchScanFormData()
    }

    private fun observeViewModel() {
        viewModel.kelasData.observe(this) { result ->
            when (result) {
                is Result.Loading -> {}
                is Result.Success -> {
                    setupMap(result.data)
                }
                is Result.Error -> {
                    Toast.makeText(this, "Gagal memuat data lokasi kelas. Coba lagi nanti.", Toast.LENGTH_LONG).show()
                    binding.btnAbsenSekarang.isEnabled = false
                    binding.btnAbsenSekarang.text = "Gagal Memuat Data"
                }
            }
        }

        viewModel.scanResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.btnAbsenSekarang.isEnabled = false
                    binding.btnAbsenSekarang.text = "Mengirim..."
                }
                is Result.Success -> {
                    AlertDialog.Builder(this)
                        .setTitle("Absen Berhasil")
                        .setMessage(result.data)
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                }
                is Result.Error -> {
                    binding.btnAbsenSekarang.isEnabled = true
                    binding.btnAbsenSekarang.text = "Absen Sekarang"
                    AlertDialog.Builder(this)
                        .setTitle("Absen Gagal")
                        .setMessage(result.message)
                        .setPositiveButton("Coba Lagi") { dialog, _ ->
                            dialog.dismiss()
                            tokenQr = null
                            binding.barcodeScanner.resume()
                        }
                        .show()
                }
            }
        }
    }

    private fun setupMap(kelas: KelasDetail) {
        binding.mapView.settings.javaScriptEnabled = true
        binding.mapView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("AbsenScanActivity", "Peta selesai dimuat.")
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                val errorMessage = "Gagal memuat peta: ${error?.description}"
                Log.e("AbsenScanActivity", errorMessage)
                Toast.makeText(this@AbsenScanActivity, "Gagal memuat peta, periksa koneksi internet Anda.", Toast.LENGTH_LONG).show()
            }
        }

        val kelasLat = kelas.latitude ?: -5.3971
        val kelasLong = kelas.longitude ?: 105.2667
        val radius = kelas.radius?.toIntOrNull() ?: 100

        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Peta Lokasi</title>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
                <style> body { padding: 0; margin: 0; } html, body, #map { height: 100%; width: 100%; } </style>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    var map = L.map('map').setView([$kelasLat, $kelasLong], 16);
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);
                    L.marker([$kelasLat, $kelasLong]).addTo(map).bindPopup('<b>Lokasi Kelas</b>').openPopup();
                    L.circle([$kelasLat, $kelasLong], { color: 'green', fillColor: '#0f0', fillOpacity: 0.3, radius: $radius }).addTo(map);
                </script>
            </body>
            </html>
        """.trimIndent()
        binding.mapView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startScanner()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startScanner() {
        captureManager = CaptureManager(this, binding.barcodeScanner)
        captureManager?.initializeFromIntent(intent, null)
        captureManager?.decode()

        binding.barcodeScanner.decodeSingle { result ->
            binding.barcodeScanner.pause()
            tokenQr = result.text
            Toast.makeText(this, "QR Terdeteksi!", Toast.LENGTH_SHORT).show()
            submitAbsen()
        }
    }

    private fun setupClickListener() {
        binding.btnAbsenSekarang.setOnClickListener {
            submitAbsen()
        }
    }

    private fun submitAbsen() {
        val token = tokenQr
        val lat = currentLat
        val long = currentLong

        if (token == null) {
            Toast.makeText(this, "Silakan scan QR code terlebih dahulu", Toast.LENGTH_SHORT).show()
            binding.barcodeScanner.resume()
            return
        }
        if (lat == null || long == null) {
            Toast.makeText(this, "Lokasi Anda belum terdeteksi. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
            getCurrentLocation()
            return
        }

        viewModel.submitAbsen(token, lat, long)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(this)
                .setMessage("GPS Anda tidak aktif. Mohon aktifkan untuk melanjutkan absensi.")
                .setPositiveButton("Buka Pengaturan") { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            return
        }

        Toast.makeText(this, "Mencari lokasi Anda...", Toast.LENGTH_SHORT).show()

        locationCancellationTokenSource?.cancel()
        locationCancellationTokenSource = CancellationTokenSource()

        val timeoutRunnable = Runnable {
            locationCancellationTokenSource?.cancel()
            Toast.makeText(this, "Waktu pencarian habis. Pastikan sinyal GPS baik & coba lagi.", Toast.LENGTH_LONG).show()
        }
        locationTimeoutHandler.postDelayed(timeoutRunnable, 15000)

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, locationCancellationTokenSource!!.token)
            .addOnSuccessListener { location ->
                locationTimeoutHandler.removeCallbacks(timeoutRunnable)
                if (location != null) {
                    currentLat = location.latitude
                    currentLong = location.longitude
                    Toast.makeText(this, "Lokasi berhasil didapatkan", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Gagal mendapatkan lokasi. Silakan coba lagi.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                locationTimeoutHandler.removeCallbacks(timeoutRunnable)
                if (exception.message?.contains("Cancelled") == false) {
                    Toast.makeText(this, "Error lokasi: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        captureManager?.onResume()
    }

    override fun onPause() {
        super.onPause()
        captureManager?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager?.onDestroy()
        locationTimeoutHandler.removeCallbacksAndMessages(null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        captureManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}