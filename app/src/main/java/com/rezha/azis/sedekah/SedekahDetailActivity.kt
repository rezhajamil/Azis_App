package com.rezha.azis.sedekah

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.rezha.azis.HomeActivity
import com.rezha.azis.R
import com.rezha.azis.model.Sedekah
import com.rezha.azis.model.Zakat
import kotlinx.android.synthetic.main.activity_zakat_detail.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SedekahDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zakat_detail)

        val data=intent.getParcelableExtra<Sedekah>("data")

        tv_nama.text=data.nama
        tv_alamat.text=data.alamat
        tv_hubungan.text=data.hubungan_keluarga
        tv_jenis.text=data.jenis
        tv_tanggal.text=formatDate(data.tanggal.toString(),"dd MMMM yyyy")
        if(data.beras.equals("0")){
            tv_beras.text="-"
        }else{
            tv_beras.text=data.beras+" Kg"
        }
        if(data.uang.equals("0")){
            tv_uang.text="-"
        }else{
            tv_uang.text="Rp. "+data.uang
        }
        if(data.keterangan.equals("")){
            tv_ket.text="-"
        }else{
            tv_ket.text=data.uang
        }

        iv_back.setOnClickListener {
            var intent=Intent(this@SedekahDetailActivity,HomeActivity::class.java).putExtra("toLoad","SedekahFragment")
            startActivity(intent)
        }

        btn_edit.setOnClickListener{
            var intent=Intent(this@SedekahDetailActivity,FormSedekahActivity::class.java).putExtra("data",data)
            startActivity(intent)
            finish()
        }

        btn_delete_zakat.setOnClickListener{
            hapusData()
        }
    }

    private fun hapusData() {
        val data=intent.getParcelableExtra<Sedekah>("data")
        var alert=AlertDialog.Builder(this)
        alert.setTitle("Hapus Data")
        alert.setPositiveButton("Hapus",DialogInterface.OnClickListener{
            dialog, which ->
            val dbSedekah=FirebaseDatabase.getInstance().getReference("Sedekah").child(data.id.toString())
            dbSedekah.removeValue()
            var intent=Intent(this@SedekahDetailActivity,HomeActivity::class.java).putExtra("toLoad","SedekahFragment")
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
}