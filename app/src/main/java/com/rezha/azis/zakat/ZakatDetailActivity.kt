package com.rezha.azis.zakat

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.rezha.azis.HomeActivity
import com.rezha.azis.R
import com.rezha.azis.model.Zakat
import kotlinx.android.synthetic.main.activity_zakat_detail.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ZakatDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zakat_detail)

        val data=intent.getParcelableExtra<Zakat>("data")

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
            var intent=Intent(this@ZakatDetailActivity,HomeActivity::class.java).putExtra("toLoad","ZakatFragment")
            startActivity(intent)
        }

        btn_edit.setOnClickListener{
            var intent=Intent(this@ZakatDetailActivity,FormZakatActivity::class.java).putExtra("data",data)
            startActivity(intent)
            finish()
        }

        btn_delete_zakat.setOnClickListener{
            hapusData()
        }
    }

    private fun hapusData() {
        val data=intent.getParcelableExtra<Zakat>("data")
        var alert=AlertDialog.Builder(this)
        alert.setTitle("Hapus Data")
        alert.setPositiveButton("Hapus",DialogInterface.OnClickListener{
            dialog, which ->
            val dbZakat=FirebaseDatabase.getInstance().getReference("Transaksi").child(data.id.toString())
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
}