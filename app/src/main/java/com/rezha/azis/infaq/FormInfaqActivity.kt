package com.rezha.azis.infaq

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.FirebaseDatabase
import com.rezha.azis.HomeActivity
import com.rezha.azis.R
import com.rezha.azis.model.Infaq
import com.rezha.azis.model.Zakat
import kotlinx.android.synthetic.main.activity_form_zakat.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class FormInfaqActivity : AppCompatActivity() {

    val refInfaq= FirebaseDatabase.getInstance().getReference("Infaq")
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_infaq)

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

        val data=intent.getParcelableExtra<Infaq>("data")
        if(data!=null){
            editData(data)
        }

        btn_simpan.setOnClickListener {
            val sNama=et_nama.text.toString().capitalize().trim()
            val sAlamat=et_alamat.text.toString().capitalize().trim()
            val sHub=et_hubungan_keluarga.text.toString().capitalize().trim()
            val sBeras=et_beras.text.toString().trim()
            val sUang=et_uang.text.toString().trim()
            val sTanggal=formatDate(et_tanggal.text.toString(),"yyyy-MM-dd")
            val sKet=et_ket.text.toString().trim()
            var infaqID:String
            if(data!=null){
                infaqID=data.id.toString()
            }
            else{
                infaqID=refInfaq.push().key.toString()
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
                saveData(sNama, sAlamat, sHub, sBeras, sUang, sTanggal,sKet,infaqID)
            }
        }
    }

    private fun editData(data: Infaq) {
        et_nama.setText(data.nama)
        et_alamat.setText(data.alamat)
        et_hubungan_keluarga.setText(data.hubungan_keluarga)
        et_beras.setText(data.beras)
        et_uang.setText(data.uang)
        et_tanggal.setText(formatDate2(data.tanggal.toString(),"dd MMMM yyyy"))
        et_ket.setText(data.keterangan)
    }

    private fun saveData(sNama: String, sAlamat: String, sHub: String, sBeras: String, sUang: String, sTanggal: String, sKet: String, infaqID: String?) {
        var infaq= Infaq()

        if(sBeras.equals("")){
            infaq.beras="0"
        }else{
            infaq.beras=sBeras
        }
        if(sUang.equals("")){
            infaq.uang="0"
        }else{
            infaq.uang=sUang
        }
        infaq.nama=sNama
        infaq.alamat=sAlamat
        infaq.hubungan_keluarga=sHub
        infaq.tanggal=sTanggal
        infaq.keterangan=sKet
        infaq.id=infaqID

        if(sNama!=null && infaqID!=null){
            simpanData(infaqID,infaq)
        }
    }


    private fun simpanData(id: String, data: Infaq) {
        refInfaq.child(id).setValue(data)

        var intent= Intent(this@FormInfaqActivity, HomeActivity::class.java).putExtra("toLoad","InfaqFragment")
        startActivity(intent)
        finish()

        Toast.makeText(this@FormInfaqActivity,"Berhasil Disimpan", Toast.LENGTH_SHORT).show()
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
}