package com.rezha.azis.zakat

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.*
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.*
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.VerticalAlignment
import com.rezha.azis.R
import com.rezha.azis.model.Zakat
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.fragment_zakat.*
import java.io.File
import java.io.FileOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class ZakatFragment : Fragment() {

    private var dataList = ArrayList<Zakat>()
    var handler = Handler()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_zakat, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_add_zakat.setOnClickListener {
            var intent = Intent(activity, FormZakatActivity::class.java)
            startActivity(intent)
        }


        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val formatted = today.plusDays(1).format(formatter)
        val formattedMonth = today.withDayOfMonth(1).format(formatter)
        tv_tgl_start.setText(formattedMonth)
        tv_tgl_end.setText(formatted)

        val datePickerStart = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .build()

        tv_tgl_start.setOnClickListener {
            handler.postDelayed({
                var dice = false
                if (!dice) {
                    tv_tgl_start.setBackgroundResource(R.color.premier)
                } else {
                    tv_tgl_start.setBackgroundResource(R.color.quartener)
                }
                dice = !dice
            }, 10)
            tv_tgl_start.setBackgroundResource(R.color.quartener)
            datePickerStart.show(fragmentManager!!, datePickerStart.toString())
        }

        datePickerStart.addOnPositiveButtonClickListener {
            var tgl = formatDate(datePickerStart.headerText, "dd MMMM yyyy")
            tv_tgl_start.setText(tgl)
            getData()
        }

        val datePickerEnd = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .build()

        tv_tgl_end.setOnClickListener {
            handler.postDelayed({
                var dice = false
                if (!dice) {
                    tv_tgl_end.setBackgroundResource(R.color.premier)
                } else {
                    tv_tgl_end.setBackgroundResource(R.color.quartener)
                }
                dice = !dice
            }, 10)
            tv_tgl_end.setBackgroundResource(R.color.quartener)
            datePickerEnd.show(fragmentManager!!, datePickerEnd.toString())
        }

        datePickerEnd.addOnPositiveButtonClickListener {
            tv_tgl_end.setText(formatDate(datePickerEnd.headerText, "dd MMMM yyyy"))
            getData()
        }

        rv_zakat.layoutManager = LinearLayoutManager(context)
        getData()

        iv_share_zakat.setOnClickListener {
            printPdf()
        }
    }


//    @RequiresApi(Build.VERSION_CODES.KITKAT)
//    private fun printCsv() {
//        var hssfWorkbook = HSSFWorkbook()
//        var hssfSheet = hssfWorkbook.createSheet()
//        var hssfRow = hssfSheet.createRow(0)
//        var hssfCell = hssfRow.createCell(0)
//
//        hssfCell.setCellValue("Hello")
//        var filePath = File(context?.getExternalFilesDir(null)?.absolutePath + "/Zakat.xls")
//        Log.v("taggy", "" + filePath)
//
//        try {
//            if (!filePath.exists()) {
//                filePath.createNewFile()
//            }
//
//            var fileOutputStream = FileOutputStream(filePath)
//            hssfWorkbook.write(fileOutputStream)
//
//            if (fileOutputStream != null) {
//                fileOutputStream.flush()
//                fileOutputStream.close()
//                Toast.makeText(context, "File telah tersimpan", Toast.LENGTH_LONG).show()
//                var uri = Uri.parse(context?.getExternalFilesDir(null)?.absolutePath)
//                Log.v("taggy", "" + uri)
//                var intent = Intent(Intent.ACTION_GET_CONTENT)
//                intent.setDataAndType(uri, "file/*")
//                startActivity(intent)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

   @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun printPdf() {
       var tglMulai = formatDate(tv_tgl_start.text.toString(), "yyyy-MM-dd")
       var tglAkhir = formatDate(tv_tgl_end.text.toString(), "yyyy-MM-dd")

       var intent=Intent(Intent.ACTION_CREATE_DOCUMENT)
               .addCategory(Intent.CATEGORY_OPENABLE)
               .setType("text/pdf")
               .putExtra(Intent.EXTRA_TITLE,"Zakat ("+tglMulai+"~"+tglAkhir+").pdf")

       startActivityForResult(intent,1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==1){
            if (resultCode==RESULT_OK){
                var tglMulai = formatDate(tv_tgl_start.text.toString(), "yyyy-MM-dd")
                var tglAkhir = formatDate(tv_tgl_end.text.toString(), "yyyy-MM-dd")
                var mDatabaseQuery = FirebaseDatabase.getInstance().getReference("Transaksi").orderByChild("tanggal").startAt(tglMulai).endAt(tglAkhir)

                mDatabaseQuery.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        dataList.clear()
                        var no=1

                        var pdfPath = data?.data
                        var outputStream = pdfPath?.let { context?.contentResolver?.openOutputStream(it) }

                        var writer = PdfWriter(outputStream)
                        var pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(writer)
                        var document = Document(pdfDocument)

                        var textCenter=TextAlignment.CENTER
                        var textLeft=TextAlignment.LEFT
                        var textMiddle=VerticalAlignment.MIDDLE
                        var bgLGray=ColorConstants.CYAN
                        var noBorder=Border.NO_BORDER

                        var columnwidth= floatArrayOf(200f,200f,200f,200f,200f,200f,200f)
                        var table = Table(columnwidth)

                        table.addCell(Cell(1,5).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBorderBottom(noBorder).add(Paragraph("BKM ISTIQOMAH").setBold()))
                        table.addCell(Cell(2,2).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("BUKTI ZAKAT").setBold()))
                        table.addCell(Cell(1,5).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBorderTop(noBorder).setBorderBottom(noBorder).add(Paragraph("KOMPLEKS TAMAN SAKURA INDAH").setBold()))
                        table.addCell(Cell(1,5).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBorderTop(noBorder).add(Paragraph("PANITIA ZAKAT").setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setBackgroundColor(bgLGray).add(Paragraph("No.").setBold()))
                        table.addCell(Cell(1,1).add(Paragraph("")))

                        table.addCell(Cell(1,7).setBorderBottom(noBorder).add(Paragraph("Sudah diterima dari")))
                        table.addCell(Cell(1,7).setBorderTop(noBorder).setBorderBottom(noBorder).add(Paragraph("Nama :")))
                        table.addCell(Cell(1,7).setBorderTop(noBorder).add(Paragraph("Alamat :")))

                        table.addCell(Cell(2,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("NO.").setBold()))
                        table.addCell(Cell(2,2).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("NAMA").setBold()))
                        table.addCell(Cell(2,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("HUBUNGAN KELUARGA").setBold()))
                        table.addCell(Cell(1,2).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("ZAKAT FITRAH").setBold()))
                        table.addCell(Cell(2,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("ZAKAT HARTA (RP)").setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("BERAS (KG)").setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("UANG (RP)").setBold()))

                        var tBZF=0
                        var tUZF=0
                        var tUZH=0
                        for (getdataSnapshot in snapshot.children) {
                            var data=getdataSnapshot.getValue(Zakat::class.java)
                            if (data?.jenis.toString().substring(0..4)=="Zakat") {
                                table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(no.toString()).setBold()))
                                table.addCell(Cell(1, 2).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.nama.toString()).setBold()))
                                table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.hubungan_keluarga.toString()).setBold()))
                                if (data?.jenis == "Zakat Fitrah") {
                                    tBZF+=data?.beras.toString().toInt()
                                    tUZF+=data?.uang.toString().toInt()

                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.beras.toString()).setBold()))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.uang.toString()).setBold()))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                } else if (data?.jenis == "Zakat Harta") {
                                    tUZH+=data?.uang.toString().toInt()

                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph("-")))
                                    table.addCell(Cell(1, 1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(data?.uang.toString()).setBold()))
                                }
                                no++
                            }
                        }
                        table.addCell(Cell(1,4).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).setBackgroundColor(bgLGray).add(Paragraph("JUMLAH").setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tBZF.toString()).setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tUZF.toString()).setBold()))
                        table.addCell(Cell(1,1).setTextAlignment(textCenter).setVerticalAlignment(textMiddle).add(Paragraph(tUZH.toString()).setBold()))

                        table.addCell(Cell(1,3).setTextAlignment(textLeft).setBorder(noBorder).add(Paragraph("\nDiterima Oleh\nPetugas Penerima\n\n\n\n\n\n")))
                        table.addCell(Cell(1,1).setBorder(noBorder).add(Paragraph("")))
                        table.addCell(Cell(1,3).setTextAlignment(textLeft).setBorder(noBorder).add(Paragraph("\nDibayarkan Tanggal\noleh\n\n\n\n\n\n")))
                        table.addCell(Cell(1,3).setTextAlignment(textLeft).setBorder(noBorder).add(Paragraph("_________________________")))
                        table.addCell(Cell(1,1).setBorder(noBorder).add(Paragraph("")))
                        table.addCell(Cell(1,3).setTextAlignment(textLeft).setBorder(noBorder).add(Paragraph("_________________________")))
                        document.add(table)
                        document.close()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "" + error.message, Toast.LENGTH_LONG).show()
                    }
                })
                Toast.makeText(context, "Pdf Created", Toast.LENGTH_LONG).show()
            }
        }
        getData()
    }


    private fun getData() {
        var tglMulai = formatDate(tv_tgl_start.text.toString(), "yyyy-MM-dd")
        var tglAkhir = formatDate(tv_tgl_end.text.toString(), "yyyy-MM-dd")
        Log.v("tgl",""+tglMulai+tglAkhir)
        var preferences = Preferences(activity!!.applicationContext)
        var mDatabaseQuery = FirebaseDatabase.getInstance().getReference("Transaksi").orderByChild("tanggal").startAfter(tglMulai).endBefore(tglAkhir)
        mDatabaseQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (getdataSnapshot in snapshot.children) {
                    var zakat = getdataSnapshot.getValue(Zakat::class.java)
                    Log.v("isi",""+zakat?.jenis.toString().substring(0..4))
                    if(zakat?.jenis.toString().substring(0..4)=="Zakat"){
                        dataList.add(zakat!!)
                    }
                }

                rv_zakat.adapter = ZakatAdapter(dataList) {
                    var intent = Intent(context, ZakatDetailActivity::class.java).putExtra("data", it)
                    startActivity(intent)
                }

                shimmerZakat.stopShimmer()
                shimmerZakat.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "" + error.message, Toast.LENGTH_LONG).show()
            }

        }
        )
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


