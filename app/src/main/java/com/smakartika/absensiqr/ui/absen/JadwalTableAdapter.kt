package com.smakartika.absensiqr.ui.absen

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.TableRow
import com.smakartika.absensiqr.data.model.JadwalPelajaran
import com.smakartika.absensiqr.databinding.ItemJadwalRowBinding

class JadwalTableAdapter(private val context: Context) {

    // Definisikan konstanta untuk key ID agar konsisten
    companion object {
        const val EXTRA_JADWAL_ID = "EXTRA_JADWAL_ID"
    }

    fun createRowView(jadwal: JadwalPelajaran, index: Int): TableRow {
        val binding = ItemJadwalRowBinding.inflate(LayoutInflater.from(context), null, false)

        binding.tvNo.text = (index + 1).toString()
        binding.tvNamaGuru.text = jadwal.guru.user?.name ?: "N/A"
        binding.tvMapel.text = jadwal.mapel.nama
        binding.tvKelas.text = jadwal.kelas.nama
        binding.tvHari.text = jadwal.hari
        binding.tvJam.text = "${jadwal.jamMulai} - ${jadwal.jamSelesai}"

        // Aksi untuk tombol Scan QR
        binding.btnScanQr.setOnClickListener {
            // Buat Intent untuk membuka AbsenScanActivity
             val intent = Intent(context, AbsenScanActivity::class.java)

            // Masukkan ID jadwal ke dalam Intent
             intent.putExtra(EXTRA_JADWAL_ID, jadwal.id)

            // Jalankan Activity
             context.startActivity(intent)
        }

        // Aksi untuk tombol Rekap (Print)
        binding.btnPrint.setOnClickListener {
            // Buat Intent untuk membuka AbsenRekapActivity
             val intent = Intent(context, AbsenRekapActivity::class.java)

            // Masukkan ID jadwal ke dalam Intent
             intent.putExtra(EXTRA_JADWAL_ID, jadwal.id)

            // Jalankan Activity
             context.startActivity(intent)
        }

        return binding.root
    }
}