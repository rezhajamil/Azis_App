package com.rezha.azis.infaq

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.rezha.azis.HomeActivity
import com.rezha.azis.R
import com.rezha.azis.model.Infaq
import com.rezha.azis.model.Zakat
import com.rezha.azis.zakat.FormZakatActivity
import kotlinx.android.synthetic.main.activity_infaq_detail.*
import kotlinx.android.synthetic.main.activity_zakat_detail.*
import kotlinx.android.synthetic.main.activity_zakat_detail.btn_edit
import kotlinx.android.synthetic.main.activity_zakat_detail.iv_back
import kotlinx.android.synthetic.main.activity_zakat_detail.tv_alamat
import kotlinx.android.synthetic.main.activity_zakat_detail.tv_beras
import kotlinx.android.synthetic.main.activity_zakat_detail.tv_hubungan
import kotlinx.android.synthetic.main.activity_zakat_detail.tv_ket
import kotlinx.android.synthetic.main.activity_zakat_detail.tv_nama
import kotlinx.android.synthetic.main.activity_zakat_detail.tv_tanggal
import kotlinx.android.synthetic.main.activity_zakat_detail.tv_uang
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class InfaqDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infaq_detail)
        val data=intent.getParcelableExtra<Infaq>("data")

        tv_nama.text=data.nama
        tv_alamat.text=data.alamat
//        tv_hubungan.text=data.hubungan_keluarga
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

        iv_back_infaq.setOnClickListener {
            var intent= Intent(this@InfaqDetailActivity, HomeActivity::class.java).putExtra("toLoad","InfaqFragment")
            startActivity(intent)
        }

        btn_edit.setOnClickListener{
            var intent= Intent(this@InfaqDetailActivity, FormInfaqActivity::class.java).putExtra("data",data)
            startActivity(intent)
            finish()
        }

        btn_delete_infaq.setOnClickListener{
            hapusData()
        }
    }

    private fun hapusData() {
        val data=intent.getParcelableExtra<Infaq>("data")
        var alert= AlertDialog.Builder(this)
        alert.setTitle("Hapus Data")
        alert.setPositiveButton("Hapus", DialogInterface.OnClickListener{
            dialog, which ->
            val dbInfaq= FirebaseDatabase.getInstance().getReference("Transaksi").child(data.id.toString())
            dbInfaq.removeValue()
            var intent=Intent(this@InfaqDetailActivity,HomeActivity::class.java).putExtra("toLoad","InfaqFragment")
            startActivity(intent)
            finishAffinity()
            Toast.makeText(this,"Data telah dihapus", Toast.LENGTH_SHORT).show()
        })
        alert.setNegativeButton("Batal", DialogInterface.OnClickListener{
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