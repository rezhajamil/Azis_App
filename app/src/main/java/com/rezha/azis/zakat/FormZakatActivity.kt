package com.rezha.azis.zakat

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
import com.rezha.azis.model.Zakat
import kotlinx.android.synthetic.main.activity_form_zakat.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class FormZakatActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {

    val refZakat=FirebaseDatabase.getInstance().getReference("Zakat")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_zakat)

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
                R.array.jenis_zakat,
                R.layout.color_spinner_layout
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinner_jenis_zakat.adapter=adapter
        spinner_jenis_zakat.onItemSelectedListener=this

        val data=intent.getParcelableExtra<Zakat>("data")
        if(data!=null){
            editData(data)
        }

        btn_simpan.setOnClickListener {
            val sNama=et_nama.text.toString().capitalize().trim()
            val sAlamat=et_alamat.text.toString().capitalize().trim()
            val sHub=et_hubungan_keluarga.text.toString().capitalize().trim()
            val sJenis=spinner_jenis_zakat.selectedItem.toString().trim()
            val sBeras=et_beras.text.toString().trim()
            val sUang=et_uang.text.toString().trim()
            val sTanggal=formatDate(et_tanggal.text.toString(),"yyyy-MM-dd")
            val sKet=et_ket.text.toString().trim()
            var zakatID:String
            if(data!=null){
                zakatID=data.id.toString()
            }
            else{
                zakatID=refZakat.push().key.toString()
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
                saveData(sNama, sAlamat, sHub, sJenis, sBeras, sUang, sTanggal,sKet,zakatID)
            }
        }

    }

    private fun editData(data: Zakat) {
        et_nama.setText(data.nama)
        et_alamat.setText(data.alamat)
        et_hubungan_keluarga.setText(data.hubungan_keluarga)
        if(data.jenis=="Zakat Fitrah"){
            spinner_jenis_zakat.setSelection(0)
        }else if(data.jenis=="Zakat Harta"){
            spinner_jenis_zakat.setSelection(1)
        }
        et_beras.setText(data.beras)
        et_uang.setText(data.uang)
        et_tanggal.setText(formatDate2(data.tanggal.toString(),"dd MMMM yyyy"))
        et_ket.setText(data.keterangan)
    }

    private fun saveData(sNama: String, sAlamat: String, sHub: String, sJenis: String, sBeras: String, sUang: String, sTanggal: String, sKet: String, zakatID: String?) {
        var zakat= Zakat()

        if(sBeras.equals("")){
            zakat.beras="0"
        }else{
            zakat.beras=sBeras
        }
        if(sUang.equals("")){
            zakat.uang="0"
        }else{
            zakat.uang=sUang
        }
        zakat.nama=sNama
        zakat.alamat=sAlamat
        zakat.hubungan_keluarga=sHub
        zakat.jenis=sJenis
        zakat.tanggal=sTanggal
        zakat.keterangan=sKet
        zakat.id=zakatID

        if(sNama!=null && zakatID!=null){
            simpanData(zakatID,zakat)
        }
    }


    private fun simpanData(id: String, data: Zakat) {
        refZakat.child(id).setValue(data)

        var intent=Intent(this@FormZakatActivity,HomeActivity::class.java).putExtra("toLoad","ZakatFragment")
        startActivity(intent)
        finish()

        Toast.makeText(this@FormZakatActivity,"Berhasil Disimpan",Toast.LENGTH_SHORT).show()
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
        var pilihan=spinner_jenis_zakat.selectedItem.toString()
        if(pilihan=="Zakat Harta"){
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