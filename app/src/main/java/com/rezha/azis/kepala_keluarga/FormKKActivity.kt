package com.rezha.azis.kepala_keluarga

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rezha.azis.HomeActivity
import com.rezha.azis.MenuActivity
import com.rezha.azis.R
import com.rezha.azis.model.KK
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.activity_form_kk.*
import kotlinx.android.synthetic.main.activity_form_zakat.*
import kotlinx.android.synthetic.main.activity_form_zakat.btn_simpan
import java.util.*

class FormKKActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    val refKK= FirebaseDatabase.getInstance().getReference("KK")
    lateinit var preferences: Preferences
    private var datalist= arrayListOf<String>()
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_kk)

        preferences= Preferences(this)
        val data=intent.getParcelableExtra<KK>("data")
        if(data!=null){
            editData(data)
        }

        btn_simpan.setOnClickListener {
            val sNama=et_nama_kk.text.toString().capitalize().trim()
            val sAlamat=et_alamat_kk.text.toString().capitalize().trim()
            var sKK=""
            var sStatus=spinner_status.selectedItem.toString().capitalize().trim()

            if (spinner_kk.visibility==View.VISIBLE){
                sKK=spinner_kk.selectedItem.toString().capitalize().trim()
            }

            val sAnggota=et_anggota.text.toString().capitalize().trim()
            var sHubungan=et_hubungan.text.toString().capitalize().trim()
            var kkID:String
            if(data!=null){
                kkID=data.id.toString()
            }
            else{
                kkID=refKK.push().key.toString()
            }

            if (sStatus=="Kepala Keluarga" && sHubungan==""){
                sHubungan="Kepala Keluarga"
            }
            if (sStatus=="Kepala Keluarga" && sKK==""){
                sKK=sNama
            }

            if(sNama.equals("")){
                et_nama_kk.error="Isi Nama"
                et_nama_kk.requestFocus()
            }
            else if(sAlamat.equals("") && sStatus=="Kepala Keluarga"){
                et_alamat_kk.error="Isi Alamat"
                et_alamat_kk.requestFocus()
            }
            else if(sAnggota.equals("") && sStatus=="Kepala Keluarga"){
                et_anggota.error="Isi Jumlah Anggota"
                et_anggota.requestFocus()
            }
            else {
                saveData(sNama, sAlamat,sStatus, sAnggota,sKK,sHubungan,kkID)
            }
        }

        refKK.child(preferences.getValues("mesjid").toString()).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                datalist.clear()
                for (datasnapshot in snapshot.children){
                    var kk=datasnapshot.getValue(KK::class.java)
                    if(kk?.status=="Kepala Keluarga"){
                        datalist.add(kk?.nama.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        var adapter1= ArrayAdapter.createFromResource(
                this,
                R.array.status,
                R.layout.color_spinner_layout
        )
        var adapter2= ArrayAdapter(this,R.layout.spinner_dropdown_layout,datalist)

        adapter1.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinner_status.adapter=adapter1
        spinner_status.onItemSelectedListener=this

        adapter2.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinner_kk.adapter=adapter2
        spinner_kk.onItemSelectedListener=this


    }

    private fun editData(data: KK) {
        et_nama_kk.setText(data.nama)
        et_alamat_kk.setText(data.alamat)
        if (data.status=="Kepala Keluarga"){
            spinner_status.setSelection(0)
        }
        else{
            spinner_status.setSelection(1)
        }
        et_anggota.setText(data.jlh_anggota)
        spinner_kk.setSelection(0)
        et_hubungan.setText(data.hubungan)
    }

    private fun saveData(sNama: String, sAlamat: String, sStatus: String, sAnggota: String, sKK: String, sHubungan: String, kkID: String) {
        var kk= KK()

        kk.nama=sNama
        kk.alamat=sAlamat
        kk.status=sStatus
        kk.jlh_anggota=sAnggota
        kk.kk=sKK
        kk.hubungan=sHubungan
        kk.id=kkID

        if(sNama!=null && kkID!=null){
            simpanData(kkID,kk)
        }
    }

    private fun simpanData(id: String, data: KK) {
        refKK.child(preferences.getValues("mesjid").toString()).child(id).setValue(data)

        var intent= Intent(this@FormKKActivity, HomeActivity::class.java).putExtra("toLoad","KKFragment")
        startActivity(intent)
        finish()

        Toast.makeText(this@FormKKActivity,"Berhasil Disimpan", Toast.LENGTH_SHORT).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var pilihan=spinner_status.selectedItem.toString()
        if(pilihan=="Kepala Keluarga"){
            tv_kk.visibility=View.GONE
            spinner_kk.visibility=View.GONE
            tv_hub.visibility=View.GONE
            et_hubungan.visibility=View.GONE

            tv_alamat_kk.visibility=View.VISIBLE
            et_alamat_kk.visibility=View.VISIBLE
            tv_jumlah_anggota.visibility=View.VISIBLE
            et_anggota.visibility=View.VISIBLE
            tv_orang.visibility=View.VISIBLE
        }else{
            tv_kk.visibility=View.VISIBLE
            spinner_kk.visibility=View.VISIBLE
            tv_hub.visibility=View.VISIBLE
            et_hubungan.visibility=View.VISIBLE

            tv_alamat_kk.visibility=View.GONE
            et_alamat_kk.visibility=View.GONE
            tv_jumlah_anggota.visibility=View.GONE
            et_anggota.visibility=View.GONE
            tv_orang.visibility=View.GONE
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onBackPressed() {
        startActivity(Intent(this@FormKKActivity,HomeActivity::class.java).putExtra("toLoad","KKFragment"))
        finish()
    }
}