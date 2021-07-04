package com.rezha.azis.panitia

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.FirebaseDatabase
import com.rezha.azis.*
import com.rezha.azis.model.Panitia
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.activity_form_kk.*
import kotlinx.android.synthetic.main.activity_form_panitia.*
import kotlinx.android.synthetic.main.activity_form_zakat.*
import kotlinx.android.synthetic.main.activity_form_zakat.btn_simpan
import kotlinx.android.synthetic.main.activity_form_zakat.et_tanggal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class FormPanitiaActivity : AppCompatActivity() {

    val refPanitia=FirebaseDatabase.getInstance().getReference("Panitia")
    lateinit var preferences: Preferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_panitia)

        preferences= Preferences(this)

        val today= LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy")
        val formatted = today.format(formatter)

        et_tahun.setText(formatted)
        et_alamat_mesjid.setText(preferences.getValues("alamat_mesjid"))
        et_mesjid.setText(preferences.getValues("mesjid"))

        val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tahun")
                .build()

        et_tahun.setOnClickListener {
            datePicker.show(supportFragmentManager, datePicker.toString())
        }
        datePicker.addOnPositiveButtonClickListener {
            et_tahun.setText(formatDate(datePicker.headerText, "yyyy"))
        }

        val data=intent.getParcelableExtra<Panitia>("data")
        if(data!=null){
            editData(data)
        }

        btn_simpan.setOnClickListener {
            val sNama=et_nama_kk_zakat.text.toString().capitalize().trim()
            val sAlamat=et_alamat_mesjid.text.toString().capitalize().trim()
            val sMesjid=et_mesjid.text.toString().capitalize().trim()
            val sHarga=et_harga_beras.text.toString().trim()
            val sFidyah=et_fidyah.text.toString().trim()
            val sTahun=et_tahun.text.toString()
            var panitiaID:String
            if(data!=null){
                panitiaID=data.id.toString()
            }
            else{
                panitiaID=refPanitia.push().key.toString()
            }

            if(sNama.equals("")){
                et_nama_kk_zakat.error="Isi Nama"
                et_nama_kk_zakat.requestFocus()
            }
            else if(sMesjid.equals("")){
                et_mesjid.error="Isi Nama Mesjid"
                et_mesjid.requestFocus()
            }
            else if(sAlamat.equals("")){
                et_alamat_mesjid.error="Isi Alamat Mesjid"
                et_alamat_mesjid.requestFocus()
            }
            else if(sHarga.equals("")){
                et_harga_beras.error="Isi Alamat Mesjid"
                et_harga_beras.requestFocus()
            }
            else if(sFidyah.equals("")){
                et_fidyah.error="Isi Alamat Mesjid"
                et_fidyah.requestFocus()
            }
            else {
                saveData(sNama, sMesjid,sAlamat,sTahun,sHarga,sFidyah,panitiaID)
            }
        }

    }

    private fun editData(data: Panitia) {
        et_nama_kk_zakat.setText(data.nama)
        et_mesjid.setText(data.mesjid)
        et_alamat_mesjid.setText(data.alamat_mesjid)
        et_harga_beras.setText(data.harga_beras)
        et_fidyah.setText(data.fidyah)
        et_tahun.setText(data.tahun)
    }

    private fun saveData(sNama: String, sMesjid: String, sAlamat: String, sTahun: String, sHarga: String, sFidyah: String, panitiaID: String) {
        var panitia= Panitia()

        panitia.nama=sNama
        panitia.alamat_mesjid=sAlamat
        panitia.mesjid=sMesjid
        panitia.tahun=sTahun
        panitia.harga_beras=sHarga
        panitia.fidyah=sFidyah
        panitia.id=panitiaID

        if(sNama!=null && panitiaID!=null){
            simpanData(panitiaID,panitia)
        }
    }


    private fun simpanData(id: String, data: Panitia) {
        refPanitia.child(preferences.getValues("mesjid").toString()).child(id).setValue(data)

        var intent=Intent(this@FormPanitiaActivity,HomeActivity::class.java).putExtra("toLoad","PanitiaFragment")
        startActivity(intent)
        finishAffinity()

        Toast.makeText(this@FormPanitiaActivity,"Berhasil Disimpan",Toast.LENGTH_SHORT).show()
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

    override fun onBackPressed() {
        startActivity(Intent(this@FormPanitiaActivity,HomeActivity::class.java).putExtra("toLoad","PanitiaFragment"))
        finish()
    }

}