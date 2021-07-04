package com.rezha.azis.panitia

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.rezha.azis.HomeActivity
import com.rezha.azis.R
import com.rezha.azis.model.Panitia
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.activity_panitia_detail.*
import kotlinx.android.synthetic.main.activity_zakat_detail.*
import kotlinx.android.synthetic.main.activity_zakat_detail.btn_edit
import kotlinx.android.synthetic.main.activity_zakat_detail.tv_alamat_zakat
import kotlinx.android.synthetic.main.activity_zakat_detail.tv_mesjid
import kotlinx.android.synthetic.main.activity_zakat_detail.tv_nama
import kotlinx.android.synthetic.main.activity_zakat_detail.tv_tanggal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class PanitiaDetailActivity : AppCompatActivity() {

    lateinit var preferences: Preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panitia_detail)

        preferences=Preferences(this)
        val data=intent.getParcelableExtra<Panitia>("data")

        tv_nama.text=data.nama
        tv_alamat_zakat.text=data.alamat_mesjid
        tv_mesjid.text=data.mesjid
        tv_tahun.text=data.tahun
        tv_harga.text=data.harga_beras
        tv_fidyah.text=data.fidyah

        iv_back_panitia.setOnClickListener {
            var intent=Intent(this@PanitiaDetailActivity,HomeActivity::class.java).putExtra("toLoad","PanitiaFragment")
            startActivity(intent)
        }

        btn_edit.setOnClickListener{
            var intent=Intent(this@PanitiaDetailActivity,FormPanitiaActivity::class.java).putExtra("data",data)
            startActivity(intent)
        }

        btn_delete_panitia.setOnClickListener{
            hapusData()
        }
    }

    private fun hapusData() {
        val data=intent.getParcelableExtra<Panitia>("data")
        var alert=AlertDialog.Builder(this)
        alert.setTitle("Hapus Data")
        alert.setPositiveButton("Hapus",DialogInterface.OnClickListener{
            dialog, which ->
            val dbPanitia=FirebaseDatabase.getInstance().getReference("Panitia").child(preferences.getValues("mesjid").toString()).child(data.id.toString())
            dbPanitia.removeValue()
            var intent=Intent(this@PanitiaDetailActivity,HomeActivity::class.java).putExtra("toLoad","PanitiaFragment")
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

    override fun onBackPressed() {
        startActivity(Intent(this@PanitiaDetailActivity,HomeActivity::class.java).putExtra("toLoad","PanitiaFragment"))
        finish()
    }
}