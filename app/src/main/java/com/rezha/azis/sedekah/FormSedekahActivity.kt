package com.rezha.azis.sedekah

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.FirebaseDatabase
import com.rezha.azis.*
import com.rezha.azis.model.Sedekah
import com.rezha.azis.model.Zakat
import kotlinx.android.synthetic.main.activity_form_sedekah.*
import kotlinx.android.synthetic.main.activity_form_zakat.*
import kotlinx.android.synthetic.main.activity_form_zakat.btn_simpan
import kotlinx.android.synthetic.main.activity_form_zakat.et_alamat
import kotlinx.android.synthetic.main.activity_form_zakat.et_beras
import kotlinx.android.synthetic.main.activity_form_zakat.et_hubungan_keluarga
import kotlinx.android.synthetic.main.activity_form_zakat.et_ket
import kotlinx.android.synthetic.main.activity_form_zakat.et_nama
import kotlinx.android.synthetic.main.activity_form_zakat.et_tanggal
import kotlinx.android.synthetic.main.activity_form_zakat.et_uang
import kotlinx.android.synthetic.main.activity_form_zakat.tv_beras
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class FormSedekahActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {

    val refSedekah=FirebaseDatabase.getInstance().getReference("Sedekah")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_sedekah)

        val today= LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val formatted = today.format(formatter)

        et_tanggal.setText(formatted)

        val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .build()

        et_tanggal.setOnClickListener {
            datePicker.show(supportFragmentManager, datePicker.toString())
        }
        datePicker.addOnPositiveButtonClickListener {
            et_tanggal.setText(formatDate(datePicker.headerText, "dd MMMM yyyy"))
        }

        var adapter=ArrayAdapter.createFromResource(
                this,
                R.array.jenis_sedekah,
                R.layout.color_spinner_layout
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinner_jenis_sedekah.adapter=adapter
        spinner_jenis_sedekah.onItemSelectedListener=this

        val data=intent.getParcelableExtra<Sedekah>("data")
        if(data!=null){
            editData(data)
        }

        btn_simpan.setOnClickListener {
            val sNama=et_nama.text.toString().capitalize().trim()
            val sAlamat=et_alamat.text.toString().capitalize().trim()
            val sHub=et_hubungan_keluarga.text.toString().capitalize().trim()
            val sJenis=spinner_jenis_sedekah.selectedItem.toString().trim()
            val sBeras=et_beras.text.toString().trim()
            val sUang=et_uang.text.toString().trim()
            val sTanggal=formatDate(et_tanggal.text.toString(),"yyyy-MM-dd")
            val sKet=et_ket.text.toString().trim()
            var sedekahID:String
            if(data!=null){
                sedekahID=data.id.toString()
            }
            else{
                sedekahID=refSedekah.push().key.toString()
            }

            if(sNama.equals("")){
                et_nama.error="Isi Nama"
                et_nama.requestFocus()
            }
            if(sAlamat.equals("")){
                et_alamat.error="Isi Alamat"
                et_alamat.requestFocus()
            }
            if(sHub.equals("")){
                et_hubungan_keluarga.error="Isi Hubungan Keluarga"
                et_hubungan_keluarga.requestFocus()
            }
            if(sUang.equals("") && sBeras.equals("")){
                et_uang.error="Isi Jumlah Uang"
                et_beras.error="Isi Jumlah Beras"
                et_uang.requestFocus()
                et_beras.requestFocus()
            }
            else {
                saveData(sNama, sAlamat, sHub, sJenis, sBeras, sUang, sTanggal,sKet,sedekahID)
            }
        }

    }

    private fun editData(data: Sedekah) {
        et_nama.setText(data.nama)
        et_alamat.setText(data.alamat)
        et_hubungan_keluarga.setText(data.hubungan_keluarga)
        if(data.jenis=="Sedekah"){
            spinner_jenis_sedekah.setSelection(0)
        }else if(data.jenis=="Fidyah"){
            spinner_jenis_sedekah.setSelection(1)
        }
        et_beras.setText(data.beras)
        et_uang.setText(data.uang)
        et_tanggal.setText(formatDate2(data.tanggal.toString(),"dd MMMM yyyy"))
        et_ket.setText(data.keterangan)
    }

    private fun saveData(sNama: String, sAlamat: String, sHub: String, sJenis: String, sBeras: String, sUang: String, sTanggal: String, sKet: String, sedekahID: String?) {
        var sedekah= Sedekah()

        if(sBeras.equals("")){
            sedekah.beras="0"
        }else{
            sedekah.beras=sBeras
        }
        if(sUang.equals("")){
            sedekah.uang="0"
        }else{
            sedekah.uang=sUang
        }
        sedekah.nama=sNama
        sedekah.alamat=sAlamat
        sedekah.hubungan_keluarga=sHub
        sedekah.jenis=sJenis
        sedekah.tanggal=sTanggal
        sedekah.keterangan=sKet
        sedekah.id=sedekahID

        if(sNama!=null && sedekahID!=null){
            simpanData(sedekahID,sedekah)
        }
    }


    private fun simpanData(id: String, data: Sedekah) {
        refSedekah.child(id).setValue(data)

        var intent=Intent(this@FormSedekahActivity,HomeActivity::class.java).putExtra("toLoad","SedekahFragment")
        startActivity(intent)
        finish()

        Toast.makeText(this@FormSedekahActivity,"Berhasil Disimpan",Toast.LENGTH_SHORT).show()
    }

    private fun formatDate(date: String, format: String):String{
        var formattedDate=""
        val sdf= SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        sdf.timeZone= TimeZone.getTimeZone("UTC")

        try{
            val parseDate=sdf.parse(date)
            formattedDate= SimpleDateFormat(format).format(parseDate)
        }catch (e: ParseException){
            e.printStackTrace()
        }
        return formattedDate
    }

    private fun formatDate2(date: String, format: String):String{
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var pilihan=spinner_jenis_sedekah.selectedItem.toString()
        if(pilihan=="Fidyah"){
            tv_beras.visibility=View.GONE
            et_beras.visibility=View.GONE
        }else{
            tv_beras.visibility=View.VISIBLE
            et_beras.visibility=View.VISIBLE
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}