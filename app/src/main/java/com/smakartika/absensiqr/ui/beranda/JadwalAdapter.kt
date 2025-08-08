package com.smakartika.absensiqr.ui.beranda

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smakartika.absensiqr.data.model.JadwalPelajaran
import com.smakartika.absensiqr.databinding.ItemJadwalBinding

class JadwalAdapter : ListAdapter<JadwalPelajaran, JadwalAdapter.JadwalViewHolder>(JadwalDiffCallback()) {

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
             binding.tvTanggal.text = "Guru: ${jadwal.guru.user.name}"
        }
    }
}

class JadwalDiffCallback : DiffUtil.ItemCallback<JadwalPelajaran>() {
    override fun areItemsTheSame(oldItem: JadwalPelajaran, newItem: JadwalPelajaran): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: JadwalPelajaran, newItem: JadwalPelajaran): Boolean {
        return oldItem == newItem
    }
}