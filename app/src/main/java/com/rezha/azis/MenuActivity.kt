package com.rezha.azis

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.VerticalAlignment
import com.rezha.azis.model.Transaksi
import com.rezha.azis.model.Zakat
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.fragment_sedekah.*
import kotlinx.android.synthetic.main.fragment_zakat.*
import kotlinx.android.synthetic.main.print_dialog.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MenuActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        var handler=Handler()

        iv_zakat.setOnClickListener{
            handler.postDelayed({
               var dice=false
               if(!dice){
                   iv_zakat.setImageResource(R.drawable.zakat_menu)
               }else{
                   iv_zakat.setImageResource(R.drawable.zakat_menu_active)
               }
                dice=!dice
            },100)
            iv_zakat.setImageResource(R.drawable.zakat_menu_active)
            var intent=Intent(this@MenuActivity,HomeActivity::class.java).putExtra("toLoad","ZakatFragment")
            startActivity(intent)
        }
        iv_infaq.setOnClickListener{
            handler.postDelayed({
                var dice=false
                if(!dice){
                    iv_infaq.setImageResource(R.drawable.infaq_menu)
                }else{
                    iv_infaq.setImageResource(R.drawable.infaq_menu_active)
                }
                dice=!dice
            },100)
            iv_infaq.setImageResource(R.drawable.infaq_menu_active)
            var intent=Intent(this@MenuActivity,HomeActivity::class.java).putExtra("toLoad","InfaqFragment")
            startActivity(intent)
        }
        iv_sedekah.setOnClickListener{
            handler.postDelayed({
                var dice=false
                if(!dice){
                    iv_sedekah.setImageResource(R.drawable.sedekah_menu)
                }else{
                    iv_sedekah.setImageResource(R.drawable.sedekah_menu_active)
                }
                dice=!dice
            },100)
            iv_sedekah.setImageResource(R.drawable.sedekah_menu_active)
            var intent=Intent(this@MenuActivity,HomeActivity::class.java).putExtra("toLoad","SedekahFragment")
            startActivity(intent)
        }

        btn_rekap.setOnClickListener{
            initDialog()
        }
    }

    var tglAwal=""
    var tglAkhir=""
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initDialog() {
        var dialog=Dialog(this)
        dialog.setContentView(R.layout.print_dialog)
        dialog.setCancelable(true)
        dialog.show()

        var btnCancel=dialog.findViewById<Button>(R.id.btn_cancel)
        var btnCetak=dialog.findViewById<Button>(R.id.btn_cetak)
        var tglStart=dialog.findViewById<TextView>(R.id.tv_tgl_start_print)
        var tglEnd=dialog.findViewById<TextView>(R.id.tv_tgl_end_print)

        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val formatted = today.format(formatter)
        val formattedMonth = today.withDayOfMonth(1).format(formatter)
        val handler=Handler()

        tglStart.setText(formattedMonth)
        tglEnd.setText(formatted)

        val datePickerStart = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .build()

        tglStart.setOnClickListener {
            handler.postDelayed({
                var dice = false
                if (!dice) {
                    tglStart.setBackgroundResource(R.color.premier)
                } else {
                    tglStart.setBackgroundResource(R.color.quartener)
                }
                dice = !dice
            }, 10)
            tglStart.setBackgroundResource(R.color.quartener)
            datePickerStart.show(supportFragmentManager, datePickerStart.toString())
        }

        datePickerStart.addOnPositiveButtonClickListener {
            var tgl = formatDate(datePickerStart.headerText, "dd MMMM yyyy")
            tglStart.setText(tgl)
        }

        val datePickerEnd = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .build()

        tglEnd.setOnClickListener {
            handler.postDelayed({
                var dice = false
                if (!dice) {
                    tglEnd.setBackgroundResource(R.color.premier)
                } else {
                    tglEnd.setBackgroundResource(R.color.quartener)
                }
                dice = !dice
            }, 10)
            tglEnd.setBackgroundResource(R.color.quartener)
            datePickerEnd.show(supportFragmentManager!!, datePickerEnd.toString())
        }

        datePickerEnd.addOnPositiveButtonClickListener {
            tglEnd.setText(formatDate(datePickerEnd.headerText, "dd MMMM yyyy"))
        }

        btnCancel.setOnClickListener{
            dialog.dismiss()
        }

        btnCetak.setOnClickListener{
            printPdf(tglStart.text,tglEnd.text)
            tglAwal=tglStart.text.toString()
            tglAkhir=tglEnd.text.toString()
        }

    }

    private fun printPdf(start: CharSequence, end: CharSequence) {
        var tglMulai = formatDate(start.toString(), "yyyy-MM-dd")
        var tglAkhir = formatDate(end.toString(), "yyyy-MM-dd")

        var intent=Intent(Intent.ACTION_CREATE_DOCUMENT)
                .putExtra("start", "tglMulai")
                .putExtra("end", "tglAkhir")
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("text/pdf")
                .putExtra(Intent.EXTRA_TITLE,"Rekapitulasi ("+tglMulai+"~"+tglAkhir+").pdf")
        startActivityForResult(intent,1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var start= tglAwal
        var end= tglAkhir
        var tglMulai = formatDate(start.toString(), "yyyy-MM-dd")
        var tglAkhir = formatDate(end.toString(), "yyyy-MM-dd")

        if (requestCode==1){
            if (resultCode== RESULT_OK){
                var mDatabaseQuery = FirebaseDatabase.getInstance().getReference("Transaksi").orderByChild("tanggal").startAt(tglMulai).endAt(tglAkhir)

                mDatabaseQuery.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var no=1
                        var pdfPath = data?.data
//                        var outputStream = pdfPath?.let { context?.contentResolver?.openOutputStream(it) }
                        var outputStream = pdfPath?.let { contentResolver.openOutputStream(it) }

                        var writer = PdfWriter(outputStream)
                        var pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(writer)
                        pdfDocument.addNewPage(PageSize.A4)
                        var document = Document(pdfDocument)

                        var textCenter= TextAlignment.CENTER
                        var textLeft= TextAlignment.LEFT
                        var textMiddle= VerticalAlignment.MIDDLE
                        var bgLGray= ColorConstants.CYAN
                        var noBorder= Border.NO_BORDER

                        var columnwidth= floatArrayOf(200f,200f,200f,200f,200f,200f,200f,200f,200f,200f,200f,100f)
                        var table = Table(columnwidth)

                        table.addCell(Cell(1,12).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBorder(noBorder).add(Paragraph("REKAPITULASI").setBold()))
                        table.addCell(Cell(1,12).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBorder(noBorder).add(Paragraph("DAFTAR PEMBAYARAN ZAKAT, FIDYAH, INFAQ, DAN SEDEKAH").setBold()))
                        table.addCell(Cell(1,12).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBorder(noBorder).add(Paragraph("MASJID ISTIQOMAH, KOMPLEKS TAMAN SAKURA INDAH").setBold()))
                        table.addCell(Cell(1,12).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBorder(noBorder).add(Paragraph("TAHUN "+start.toString().takeLast(4)+" M").setBold()))

                        table.addCell(Cell(2,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("NO.").setBold()))
                        table.addCell(Cell(2,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("NAMA").setBold()))
                        table.addCell(Cell(2,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("ALAMAT").setBold()))
                        table.addCell(Cell(1,2).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("ZAKAT FITRAH").setBold()))
                        table.addCell(Cell(2,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("ZAKAT HARTA (RP)").setBold()))
                        table.addCell(Cell(1,2).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("INFAQ").setBold()))
                        table.addCell(Cell(2,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("FIDYAH (RP)").setBold()))
                        table.addCell(Cell(1,2).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("SEDEKAH").setBold()))
                        table.addCell(Cell(2,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("KET").setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("BERAS (KG)").setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("UANG (RP)").setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("BERAS (KG)").setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("UANG (RP)").setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("BERAS (KG)").setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("UANG (RP)").setBold()))

                        var tBZF=0
                        var tUZF=0
                        var tUZH=0
                        var tBI=0
                        var tUI=0
                        var tBS=0
                        var tUS=0
                        var tUF=0
                        for (getdataSnapshot in snapshot.children) {
                            var data=getdataSnapshot.getValue(Transaksi::class.java)
                            Log.v("isi",""+data)
                                table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(no.toString())))
                                table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.nama.toString())))
                                table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.alamat.toString())))
                                if (data?.jenis == "Zakat Fitrah") {
                                    tBZF+=data?.beras.toString().toInt()
                                    tUZF+=data?.uang.toString().toInt()

                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.beras.toString())))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.uang.toString())))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                } else if (data?.jenis == "Zakat Harta") {
                                    tUZH+=data?.uang.toString().toInt()

                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.uang.toString())))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                }else if (data?.jenis == "Infaq") {
                                    tBI+=data?.beras.toString().toInt()
                                    tUI+=data?.uang.toString().toInt()

                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.beras.toString())))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.uang.toString())))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                }else if (data?.jenis == "Fidyah") {
                                    tUF+=data?.uang.toString().toInt()

                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.uang.toString())))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                }else if (data?.jenis == "Sedekah") {
                                    tUS+=data?.uang.toString().toInt()
                                    tBS+=data?.beras.toString().toInt()

                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.beras.toString())))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.uang.toString())))
                                }
                                table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.keterangan.toString())))
                                no++
                        }
                        table.addCell(Cell(1,3).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("JUMLAH").setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tBZF.toString()).setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tUZF.toString()).setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tUZH.toString()).setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tBI.toString()).setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tUI.toString()).setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tUF.toString()).setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tBS.toString()).setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tUS.toString()).setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("")))

                        document.add(table)
                        document.close()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@MenuActivity, "" + error.message, Toast.LENGTH_LONG).show()
                    }
                })
                Toast.makeText(this@MenuActivity, "Pdf Created", Toast.LENGTH_LONG).show()
            }
            var intent=Intent(this@MenuActivity,MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun formatDate(date: String, format: String): String {
        var formattedDate = ""
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        try {
            val parseDate = sdf.parse(date)
            formattedDate = SimpleDateFormat(format).format(parseDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return formattedDate
    }
}

