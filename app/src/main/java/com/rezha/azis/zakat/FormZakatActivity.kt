package com.rezha.azis.zakat

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rezha.azis.*
import com.rezha.azis.model.KK
import com.rezha.azis.model.Panitia
import com.rezha.azis.model.Zakat
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.activity_form_kk.*
import kotlinx.android.synthetic.main.activity_form_zakat.*
import kotlinx.android.synthetic.main.activity_form_zakat.btn_simpan
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class FormZakatActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {

    val refZakat=FirebaseDatabase.getInstance().getReference("Zakat")
    val refKK=FirebaseDatabase.getInstance().getReference("KK")
    val refPanitia=FirebaseDatabase.getInstance().getReference("Panitia")
    lateinit var preferences: Preferences

    private var datalistPanitia= arrayListOf<String>()
    private var datalistKK= arrayListOf<String>()
    private var datalistHarga= arrayListOf<String>()
    private var datalistFidyah= arrayListOf<Panitia>()
    var dataAlamat=""
    var dataAnggota=""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_zakat)

        preferences=Preferences(this)

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

        refKK.child(preferences.getValues("mesjid").toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                datalistKK.clear()
                for (datasnapshot in snapshot.children){
                    var kk=datasnapshot.getValue(KK::class.java)
                    if(kk?.status=="Kepala Keluarga"){
                        datalistKK.add(kk?.nama.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        refPanitia.child(preferences.getValues("mesjid").toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                datalistHarga.clear()
                for (datasnapshot in snapshot.children){
                    var panitia=datasnapshot.getValue(Panitia::class.java)
//                    var nama= panitia?.nama?.split(",").toTypedArray()
                    var nama= panitia?.nama
//                    Collections.addAll(datalistHarga,harga)
                    datalistPanitia.add(nama!!)
                    datalistFidyah.add(panitia!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        var adapter=ArrayAdapter.createFromResource(
                this,
                R.array.jenis_zakat_fitrah,
                R.layout.color_spinner_layout
        )

        var adapter2= ArrayAdapter(this,R.layout.spinner_dropdown_layout,datalistKK)
        var adapter3= ArrayAdapter(this,R.layout.spinner_dropdown_layout,datalistPanitia)
        var adapter4= ArrayAdapter(this,R.layout.spinner_dropdown_layout,datalistHarga)

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinner_jenis_zakat.adapter=adapter
        spinner_jenis_zakat.onItemSelectedListener=this

        adapter2.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinner_kk_zakat.adapter=adapter2
        spinner_kk_zakat.onItemSelectedListener=this

        adapter3.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinner_panitia.adapter=adapter3
        spinner_panitia.onItemSelectedListener=this

        adapter4.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinner_harga.adapter=adapter4
        spinner_harga.onItemSelectedListener=this

        val data=intent.getParcelableExtra<Zakat>("data")
        if(data!=null){
            editData(data)
        }

        btn_simpan.setOnClickListener {
            var sPanitia=spinner_panitia.selectedItem.toString().capitalize().trim()
            var sNama=spinner_kk_zakat.selectedItem.toString().capitalize().trim()
            var sAlamat=dataAlamat
            var sAnggota=dataAnggota
            val sJenis=spinner_jenis_zakat.selectedItem.toString().trim()
            var sHarga=""
            if (spinner_harga.visibility==View.VISIBLE){
                sHarga=spinner_harga.selectedItem.toString().trim()
            }
            var sBeras=et_beras.text.toString().trim()
            var sUang=et_uang.text.toString().trim()
            var sZHarta=et_zakat_harta.text.toString().trim()
            var sFidyah=et_fidyah2.text.toString().trim()
            val sTanggal=formatDate(et_tanggal.text.toString(),"yyyy-MM-dd")
            val sKet=spinner_ket.selectedItem.toString().trim()
            var zakatID:String
            if(data!=null){
                zakatID=data.id.toString()
            }
            else{
                zakatID=refZakat.push().key.toString()
            }

            if (sJenis=="Beras"){
                sHarga="0"
                sUang="0"
            }
            else{
                sBeras="0"
            }
            if (sZHarta==""){
                sZHarta="0"
            }

            if(sUang.equals("") && sBeras.equals("")){
                et_uang.error="Isi Jumlah Uang"
                et_beras.error="Isi Jumlah Beras"
                et_uang.requestFocus()
                et_beras.requestFocus()
            }
            else if (sFidyah==""){
                sFidyah="0"
                saveData(sPanitia,sNama,sAlamat,sAnggota, sJenis, sHarga, sBeras, sUang,sZHarta,sFidyah, sTanggal,sKet,zakatID)
            }
            else if (sFidyah!=""){
                var dt=0
                for(d in datalistFidyah){
                    if (d?.nama==sPanitia){
                        dt= d?.fidyah?.toInt()!!
                    }
                }
                if (sFidyah.toInt()<dt){
                    et_fidyah2.error="Minimal Fidyah sebesar Rp."+dt.toString()
                    et_fidyah2.requestFocus()
                }
                else {
                    saveData(sPanitia,sNama,sAlamat,sAnggota, sJenis, sHarga, sBeras, sUang,sZHarta,sFidyah, sTanggal,sKet,zakatID)
                }
            }
        }

        iv_info.setOnClickListener {
            initDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initDialog() {
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.info_dialog)
        dialog.setCancelable(true)
        dialog.show()

        var btnMengerti = dialog.findViewById<Button>(R.id.btn_mengerti)
        btnMengerti.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun getBio(sNama: String) {
        refKK.child(preferences.getValues("mesjid").toString()).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot in snapshot.children){
                    var kk = snapshot.getValue(KK::class.java)
                    if (kk?.nama==sNama){
                        dataAlamat=kk?.alamat.toString()
                        dataAnggota=kk?.jlh_anggota.toString()
                    }
                }
                Log.v("save","aa "+dataAlamat+dataAnggota)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun editData(data: Zakat) {
        if(data.jenis=="Beras"){
            spinner_jenis_zakat.setSelection(0)
        }else if(data.jenis=="Uang"){
            spinner_jenis_zakat.setSelection(1)
        }
        et_beras.setText(data.beras)
        et_uang.setText(data.uang)
        et_tanggal.setText(formatDate2(data.tanggal.toString(),"dd MMMM yyyy"))
        et_zakat_harta.setText(data.zakat_harta)
        et_fidyah2.setText(data.fidyah)
    }

    private fun saveData(sPanitia: String, sNama: String, sAlamat: String, sAnggota: String, sJenis: String, sHarga: String, sBeras: String, sUang: String, sZHarta: String, sFidyah: String, sTanggal: String, sKet: String, zakatID: String) {
        var zakat= Zakat()

        zakat.beras=sBeras
        zakat.uang=sUang
        zakat.panitia=sPanitia
        zakat.nama=sNama
        zakat.alamat=sAlamat
        zakat.anggota=sAnggota
        zakat.jenis=sJenis
        zakat.harga_beras=sHarga
        zakat.zakat_harta=sZHarta
        zakat.fidyah=sFidyah
        zakat.tanggal=sTanggal
        zakat.keterangan=sKet
        zakat.id=zakatID

        if(sNama!=null && zakatID!=null){
            simpanData(zakatID,zakat)
        }
    }


    private fun simpanData(id: String, data: Zakat) {
        refZakat.child(preferences.getValues("mesjid").toString()).child(id).setValue(data)
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
        var jenis=spinner_jenis_zakat.selectedItem.toString()
        if(parent?.id==R.id.spinner_jenis_zakat){
            if(jenis=="Uang"){
                tv_beras.visibility=View.GONE
                et_beras.visibility=View.GONE

                textView13.visibility=View.VISIBLE
                et_uang.visibility=View.VISIBLE
                textView111.visibility=View.VISIBLE
                spinner_harga.visibility=View.VISIBLE

                spinner_ket.setSelection(0)
                spinner_ket.isClickable=true
                if (spinner_harga.selectedItem==null){
                    btn_simpan.visibility=View.INVISIBLE
                }
                else{
                    btn_simpan.visibility=View.VISIBLE
                }
            }else{
                tv_beras.visibility=View.VISIBLE
                et_beras.visibility=View.VISIBLE

                textView13.visibility=View.GONE
                et_uang.visibility=View.GONE
                textView111.visibility=View.GONE
                spinner_harga.visibility=View.GONE

                spinner_ket.setSelection(1)
                spinner_ket.isClickable=false
//                btn_simpan.visibility=View.VISIBLE
            }
        }
        else if (parent?.id==R.id.spinner_panitia){
            var panitia=spinner_panitia.selectedItem.toString()
            var query=refPanitia.child(preferences.getValues("mesjid").toString()).orderByChild("nama").equalTo(panitia)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    datalistHarga.clear()
                    for (datasnapshot in snapshot.children){
                        var panitia=datasnapshot.getValue(Panitia::class.java)
                        var harga= panitia?.harga_beras?.split(",")?.toTypedArray()
                        if (harga != null) {
                            for (harga in harga){
                                datalistHarga.add(harga)
                            }
                        }
                        Log.v("hrg",""+datalistHarga)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
        else if(parent?.id==R.id.spinner_kk_zakat){
            getBio(spinner_kk_zakat.selectedItem.toString())
            if (spinner_panitia.selectedItem!=null){
                if (jenis=="Uang"){
                    if (spinner_harga.selectedItem!=null){
                        btn_simpan.visibility=View.VISIBLE
                    }
                }
                else{
                    btn_simpan.visibility=View.VISIBLE
                }
            }
        }
        else if (parent?.id==R.id.spinner_harga){
            if (spinner_panitia.selectedItem!=null && spinner_kk_zakat.selectedItem!=null){
                btn_simpan.visibility=View.VISIBLE
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        var jenis=spinner_jenis_zakat.selectedItem.toString()
        if (parent?.id==R.id.spinner_panitia){
            btn_simpan.visibility=View.INVISIBLE
        }
        else if (parent?.id==R.id.spinner_kk_zakat){
            btn_simpan.visibility=View.INVISIBLE
        }
        else if (parent?.id==R.id.spinner_harga){
            if (jenis=="Uang"){
                btn_simpan.visibility=View.INVISIBLE
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@FormZakatActivity,HomeActivity::class.java).putExtra("toLoad","ZakatFragment"))
        finish()
    }

}