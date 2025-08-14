package com.smakartika.absensiqr.ui.izin

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.smakartika.absensiqr.R
import com.smakartika.absensiqr.data.model.Izin
import com.smakartika.absensiqr.databinding.ItemIzinRowBinding

class IzinTableAdapter(
    private val context: Context,
    private val onDetailClick: (Izin) -> Unit
) {

    fun createHeaderRow(): TableRow {
        val tableRow = TableRow(context)
        tableRow.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))

        tableRow.addView(createHeaderCell("No", 40))
        tableRow.addView(createHeaderCell("Nama Siswa", 150))
        tableRow.addView(createHeaderCell("Kelas", 80))
        tableRow.addView(createHeaderCell("Tanggal Izin", 120))
        tableRow.addView(createHeaderCell("Aksi", 100))

        return tableRow
    }

    fun createDataRow(izin: Izin, index: Int): TableRow {
        val binding = ItemIzinRowBinding.inflate(LayoutInflater.from(context))
        val row = binding.root as TableRow

        binding.tvNomor.text = (index + 1).toString()
        binding.tvNamaSiswa.text = izin.siswa?.user?.name ?: "N/A"
        binding.tvKelas.text = izin.siswa?.kelas?.nama ?: "N/A"
        binding.tvTanggal.text = izin.tanggalIzin
        binding.btnDetail.setOnClickListener { onDetailClick(izin) }

        return row
    }

    private fun createHeaderCell(text: String, widthDp: Int): TextView {
        val textView = TextView(context)
        textView.text = text
        textView.setTextAppearance(R.style.TableHeader)
        textView.width = dpToPx(widthDp)
        textView.gravity = Gravity.CENTER
        return textView
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}