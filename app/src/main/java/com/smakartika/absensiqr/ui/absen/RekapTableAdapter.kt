package com.smakartika.absensiqr.ui.absen

import android.content.Context
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.smakartika.absensiqr.R
import com.smakartika.absensiqr.data.model.RekapAbsenResponse

class RekapTableAdapter(private val context: Context) {

    fun createHeaderRow(): TableRow {
        val tableRow = TableRow(context)
        tableRow.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))

        tableRow.addView(createHeaderCell("No", 40))
        tableRow.addView(createHeaderCell("Nama", 150))
        tableRow.addView(createHeaderCell("Kelas", 80))
        tableRow.addView(createHeaderCell("H", 40))
        tableRow.addView(createHeaderCell("S", 40))
        tableRow.addView(createHeaderCell("I", 40))
        tableRow.addView(createHeaderCell("A", 40))
        tableRow.addView(createHeaderCell("Jml Pertemuan", 120))
        tableRow.addView(createHeaderCell("Total Hadir", 120))

        return tableRow
    }

    fun createDataRow(siswaId: String, response: RekapAbsenResponse, index: Int): TableRow {
        val tableRow = TableRow(context)
        val rekapSiswa = response.rekap[siswaId] ?: emptyList()
        val siswaData = rekapSiswa.firstOrNull()?.siswa

        val countH = rekapSiswa.count { it.status == "H" }
        val countS = rekapSiswa.count { it.status == "S" }
        val countI = rekapSiswa.count { it.status == "I" }
        val countA = rekapSiswa.count { it.status == "A" }

        tableRow.addView(createDataCell((index + 1).toString(), 40))
        tableRow.addView(createDataCell(siswaData?.user?.name ?: "N/A", 150, Gravity.START))
        tableRow.addView(createDataCell(siswaData?.kelas?.nama ?: "N/A", 80))
        tableRow.addView(createDataCell(countH.toString(), 40))
        tableRow.addView(createDataCell(countS.toString(), 40))
        tableRow.addView(createDataCell(countI.toString(), 40))
        tableRow.addView(createDataCell(countA.toString(), 40))
        tableRow.addView(createDataCell(response.jumlahPertemuan.toString(), 120))
        tableRow.addView(createDataCell(countH.toString(), 120))

        return tableRow
    }

    private fun createHeaderCell(text: String, widthDp: Int): TextView {
        val textView = TextView(context)
        textView.text = text
        textView.setTextAppearance(R.style.TableHeader)
        textView.width = dpToPx(widthDp)
        // PERBAIKAN: Menambahkan gravity center untuk header
        textView.gravity = Gravity.CENTER
        return textView
    }

    private fun createDataCell(text: String, widthDp: Int, gravity: Int = Gravity.CENTER): TextView {
        val textView = TextView(context)
        textView.text = text
        textView.gravity = gravity
        textView.setTextAppearance(R.style.TableCell)
        textView.width = dpToPx(widthDp)
        textView.setBackgroundResource(R.drawable.table_cell_border)
        return textView
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}
