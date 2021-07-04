package com.rezha.azis.zakat

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.VerticalAlignment
import com.rezha.azis.HomeActivity
import com.rezha.azis.R
import com.rezha.azis.model.KK
import com.rezha.azis.model.Panitia
import com.rezha.azis.model.Zakat
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.activity_zakat_detail.*
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ZakatDetailActivity : AppCompatActivity() {

    val refKK = FirebaseDatabase.getInstance().getReference("KK")
    val refPanitia = FirebaseDatabase.getInstance().getReference("Panitia")
    private lateinit var preferences: Preferences
    private lateinit var dataZakat:Zakat
    private lateinit var dataMesjid:String
    private lateinit var dataAlamatMesjid:String
    private var dataListKK=ArrayList<KK>()

    @ExperimentalStdlibApi
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zakat_detail)

        preferences= Preferences(this)
        dataZakat=intent.getParcelableExtra("data")


        tv_nama.text=dataZakat.nama
        tv_alamat_zakat.text=dataZakat.alamat
        tv_anggota_zakat2.text=dataZakat.anggota+" Orang"
        tv_panitia_zakat.text=dataZakat.panitia
        tv_mesjid.text=dataZakat.jenis
        tv_tanggal.text=formatDate(dataZakat.tanggal.toString(),"dd MMMM yyyy")
        if (dataZakat.harga_beras.equals("")){
            tv_harga_detail.text="-"
        }
        else {
            tv_harga_detail.text = "Rp." + dataZakat.harga_beras
        }
        if (dataZakat.zakat_harta.equals("")){
            tv_zakat_harta.text="-"
        }
        else{
            tv_zakat_harta.text="Rp."+dataZakat.zakat_harta
        }
        if (dataZakat.fidyah.equals("")){
            tv_fidyah_detail.text="-"
        }
        else {
            tv_fidyah_detail.text = "Rp." + dataZakat.fidyah
        }
        tv_ket.text=dataZakat.keterangan
        if(dataZakat.beras.equals("")){
            tv_beras.text="-"
        }else{
            tv_beras.text=dataZakat.beras+" Kg"
        }
        if(dataZakat.uang.equals("")){
            tv_uang.text="-"
        }else{
            tv_uang.text="Rp. "+dataZakat.uang
        }

        iv_back.setOnClickListener {
            var intent=Intent(this@ZakatDetailActivity,HomeActivity::class.java).putExtra("toLoad","ZakatFragment")
            startActivity(intent)
        }

        btn_edit.setOnClickListener{
            var intent=Intent(this@ZakatDetailActivity,FormZakatActivity::class.java).putExtra("data",dataZakat)
            startActivity(intent)
        }

        btn_delete_zakat.setOnClickListener{
            hapusData()
        }

        btn_print_zakat.setOnClickListener{
            printPdf()
        }

        refKK.child(preferences.getValues("mesjid").toString()).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot in snapshot.children){
                    var kk=snapshot.getValue(KK::class.java)
                    if (kk?.kk?.capitalize()==dataZakat.nama){
                        dataListKK.add(kk!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        refPanitia.child(preferences.getValues("mesjid").toString()).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot in snapshot.children){
                    var panitia=snapshot.getValue(Panitia::class.java)
                    if (dataZakat.panitia==panitia?.nama){
                        dataMesjid=panitia?.mesjid.toString().uppercase()
                        dataAlamatMesjid=panitia?.alamat_mesjid.toString().uppercase()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun hapusData() {
        val dataZakat=intent.getParcelableExtra<Zakat>("data")
        var alert=AlertDialog.Builder(this)
        alert.setTitle("Hapus Data")
        alert.setPositiveButton("Hapus",DialogInterface.OnClickListener{
            dialog, which ->
            val dbZakat=FirebaseDatabase.getInstance().getReference("Zakat").child(preferences.getValues("mesjid").toString()).child(dataZakat.id.toString())
            dbZakat.removeValue()
            var intent=Intent(this@ZakatDetailActivity,HomeActivity::class.java).putExtra("toLoad","ZakatFragment")
            startActivity(intent)
            finish()
            Toast.makeText(this,"Data telah dihapus",Toast.LENGTH_SHORT).show()
        })
        alert.setNegativeButton("Batal",DialogInterface.OnClickListener{
            dialog, which ->
            dialog.cancel()
            dialog.dismiss()
        })
        alert.create()
        alert.show()
    }

    private fun formatDate(date: String, format: String):String{
        var formattedDate=""
        val sdf= SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.timeZone= TimeZone.getTimeZone("UTC")

        try{
            val parseDate=sdf.parse(date)
            formattedDate= SimpleDateFormat(format).format(parseDate)
        }catch (e: ParseException){
            e.printStackTrace()
        }
        return formattedDate
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun printPdf() {
        var intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("text/pdf")
                .putExtra(Intent.EXTRA_TITLE, "Zakat_" + dataZakat.nama + "_" + dataZakat.tanggal + ".pdf")

        startActivityForResult(intent, 1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                var no = 1
                var pdfPath = data?.data
//                var outputStream = pdfPath?.let { context?.contentResolver?.openOutputStream(it) }
                var outputStream = pdfPath?.let { this.contentResolver.openOutputStream(it) }

                var writer = PdfWriter(outputStream)
                var pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(writer)
                var document = Document(pdfDocument)

                var textCenter = TextAlignment.CENTER
                var textLeft = TextAlignment.LEFT
                var textMiddle = VerticalAlignment.MIDDLE
                var bgLGray = ColorConstants.LIGHT_GRAY
                var noBorder = Border.NO_BORDER

                var columnwidth = floatArrayOf(200f, 200f, 200f, 200f, 200f, 200f, 200f, 200f, 200f, 200f)
                var table = Table(columnwidth)

                table.addCell(Cell(1, 8).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBorderBottom(noBorder).add(Paragraph("BKM "+dataMesjid).setBold()))
                table.addCell(Cell(2, 2).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("BUKTI ZAKAT").setBold()))
                table.addCell(Cell(1, 8).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBorderTop(noBorder).setBorderBottom(noBorder).add(Paragraph(dataAlamatMesjid).setBold()))
                table.addCell(Cell(1, 8).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBorderTop(noBorder).add(Paragraph("PANITIA ZAKAT").setBold()))
                table.addCell(Cell(1, 1).setTextAlignment(textCenter).setBackgroundColor(bgLGray).add(Paragraph("No.").setBold()))
                table.addCell(Cell(1, 1).add(Paragraph("")))

                table.addCell(Cell(1, 10).setBorderBottom(noBorder).add(Paragraph("Sudah diterima dari")))
                table.addCell(Cell(1, 10).setBorderTop(noBorder).setBorderBottom(noBorder).add(Paragraph("Nama   : "  +dataZakat.nama)))
                table.addCell(Cell(1, 10).setBorderTop(noBorder).add(Paragraph("Alamat : " +dataZakat.alamat)))

                table.addCell(Cell(2, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("NO.").setBold()))
                table.addCell(Cell(2, 2).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("NAMA").setBold()))
                table.addCell(Cell(2, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("HUBUNGAN KELUARGA").setBold()))
                table.addCell(Cell(1, 2).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("ZAKAT FITRAH").setBold()))
                table.addCell(Cell(2, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("ZAKAT HARTA (RP)").setBold()))
                table.addCell(Cell(2, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("FIDYAH (RP)").setBold()))
                table.addCell(Cell(1, 2).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("INFAQ/SEDEKAH").setBold()))
                table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("BERAS (KG)").setBold()))
                table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("UANG (RP)").setBold()))
                table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("BERAS (KG)").setBold()))
                table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("UANG (RP)").setBold()))

                val localID=Locale("in","ID")
                val format= NumberFormat.getNumberInstance(localID)

                var zU=0.toDouble()
                var zB = 0.toDouble()
                if (dataZakat.uang!="")zU = dataZakat.uang?.toDouble()!!
                if (dataZakat.beras!="")zB = dataZakat.beras?.toDouble()!!
                var tB=""
                var tU=""
                var jA = dataZakat.anggota?.toInt()
                var hB=0
                if (dataZakat.harga_beras!="")hB = dataZakat.harga_beras?.toInt()!!
                var tBZF=""
                var tUZF=""
                var tZH=0
                if (dataZakat.jenis=="Beras"){
                    tBZF = String.format("%.1f",(jA!!*2.7)).replace(".",",")
                    tB=String.format("%.1f",(zB!!-(jA!!*2.7))).replace(".",",")
                }
                else if (dataZakat.jenis=="Uang"){
                    tUZF = format.format(jA!!*3.8*hB!!).toString()
                    tU=format.format(zU!!-(hB!!*3.8*jA!!)).toString()
                }

                for (kk in dataListKK){
                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(no.toString())))
                    table.addCell(Cell(1, 2).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(kk.nama.toString())))
                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(kk.hubungan.toString())))
                    if (dataZakat.jenis=="Beras"){
                        table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("2,7")))
                        table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("")))
                    }
                    else if (dataZakat.jenis=="Uang"){
                        table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("")))
                        table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(format.format(dataZakat.harga_beras!!.toInt()*3.8).toString())))
                    }
                    if (kk.status=="Kepala Keluarga"){
                        if (dataZakat.zakat_harta!=null){
                            table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(format.format(dataZakat.zakat_harta!!.toDouble()).toString())))
                        }
                        else{
                            table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("")))
                        }
                        if (dataZakat.fidyah!=null){
                            table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(format.format(dataZakat.fidyah!!.toDouble()).toString())))
                        }
                        else{
                            table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("")))
                        }
                        if (dataZakat.jenis=="Beras"){
                            table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tB)))
                            table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("")))
                        }
                        else if (dataZakat.jenis=="Uang"){
                            table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("")))
                            table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tU)))
                        }
                    }
                    else{
                        table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("")))
                        table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("")))
                        table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("")))
                        table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("")))
                    }
                    no++
                }
                table.addCell(Cell(1, 4).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("JUMLAH").setBold()))
                table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tBZF).setBold()))
                table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tUZF).setBold()))
                if (dataZakat.zakat_harta!=null){
                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(format.format(dataZakat.zakat_harta!!.toDouble()).toString()).setBold()))
                }
                else{
                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("")))
                }
                if (dataZakat.fidyah!=null){
                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(format.format(dataZakat.fidyah!!.toDouble()).toString()).setBold()))
                }
                else{
                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("")))
                }
                if (dataZakat.jenis=="Beras"){
                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tB).setBold()))
                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("")))
                }
                else if (dataZakat.jenis=="Uang"){
                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("")))
                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tU).setBold()))
                }

                table.addCell(Cell(1, 4).setTextAlignment(textCenter).setBorder(noBorder).add(Paragraph("\nDiterima Oleh\nPetugas Penerima\n\n\n\n\n\n")))
                table.addCell(Cell(1, 2).setBorder(noBorder).add(Paragraph("")))
                table.addCell(Cell(1, 4).setTextAlignment(textCenter).setBorder(noBorder).add(Paragraph("\nDibayarkan Tanggal "+LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))+"\noleh\n\n\n\n\n\n")))
                table.addCell(Cell(1, 4).setTextAlignment(textCenter).setBorder(noBorder).add(Paragraph(dataZakat.panitia).setUnderline().setBold()))
                table.addCell(Cell(1, 2).setBorder(noBorder).add(Paragraph("")))
                table.addCell(Cell(1, 4).setTextAlignment(textCenter).setBorder(noBorder).add(Paragraph(dataZakat.nama).setUnderline().setBold()))
                document.add(table)
                document.close()
                Toast.makeText(this, "PDF Disimpan", Toast.LENGTH_LONG).show()
            }
            startActivity(Intent(this,HomeActivity::class.java).putExtra("toLoad","ZakatFragment"))
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@ZakatDetailActivity,HomeActivity::class.java).putExtra("toLoad","ZakatFragment"))
        finish()
    }

}