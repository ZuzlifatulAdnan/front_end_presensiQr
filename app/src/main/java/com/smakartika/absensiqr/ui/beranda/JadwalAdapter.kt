package com.smakartika.absensiqr.ui.beranda

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smakartika.absensiqr.data.model.JadwalPelajaran
import com.smakartika.absensiqr.databinding.ItemJadwalBinding
import com.smakartika.absensiqr.ui.absen.AbsenScanActivity
import java.text.SimpleDateFormat
import java.util.Locale

class JadwalAdapter(
    private val onScanClicked: (JadwalPelajaran) -> Unit
) : ListAdapter<JadwalPelajaran, JadwalAdapter.JadwalViewHolder>(JadwalDiffCallback()) {

    // Konstanta untuk key ID, agar konsisten
    companion object {
        const val EXTRA_JADWAL_ID = "EXTRA_JADWAL_ID"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JadwalViewHolder {
        val binding = ItemJadwalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JadwalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JadwalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class JadwalViewHolder(private val binding: ItemJadwalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(jadwal: JadwalPelajaran) {
            binding.tvMapel.text = jadwal.mapel.nama
            binding.tvHari.text = "Hari : ${jadwal.hari}"
            binding.tvWaktu.text = "${jadwal.jamMulai} - ${jadwal.jamSelesai}"
            binding.tvKelas.text = jadwal.kelas.nama

            // 👇 GANTI LOGIKA INI: Tampilkan tanggal yang sudah diformat
            binding.tvTanggal.text = formatTanggal(jadwal.tanggalPertemuan)

            // Logika untuk tombol Scan QR
            binding.btnScanQr.setOnClickListener {
                val context = it.context
                // 1. Buat Intent untuk membuka AbsenScanActivity
                val intent = Intent(context, AbsenScanActivity::class.java)

                // 2. Masukkan ID jadwal ke dalam Intent
                intent.putExtra(EXTRA_JADWAL_ID, jadwal.id)

                // 3. Jalankan Activity
                context.startActivity(intent)
            }
        }
    }

    // Fungsi helper untuk mengubah format tanggal
    private fun formatTanggal(tanggal: String?): String {
        if (tanggal == null) return "Tanggal tidak tersedia"
        return try {
            // Format input dari API Anda adalah "dd-MM-yyyy"
            val parser = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            // Format output yang kita inginkan
            val formatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
            val date = parser.parse(tanggal)
            formatter.format(date!!)
        } catch (e: Exception) {
            tanggal // Jika gagal, tampilkan tanggal aslinya
        }
    }
}

// Class JadwalDiffCallback tidak perlu diubah
class JadwalDiffCallback : DiffUtil.ItemCallback<JadwalPelajaran>() {
    override fun areItemsTheSame(oldItem: JadwalPelajaran, newItem: JadwalPelajaran): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: JadwalPelajaran, newItem: JadwalPelajaran): Boolean {
        return oldItem == newItem
    }
}