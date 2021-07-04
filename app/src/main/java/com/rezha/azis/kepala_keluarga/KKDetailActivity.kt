package com.rezha.azis.kepala_keluarga

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.rezha.azis.HomeActivity
import com.rezha.azis.R
import com.rezha.azis.model.KK
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.activity_kk_detail.*
import kotlinx.android.synthetic.main.activity_zakat_detail.*
import kotlinx.android.synthetic.main.activity_zakat_detail.btn_edit
import java.util.*

class KKDetailActivity : AppCompatActivity() {
    lateinit var preferences:Preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kk_detail)

        preferences=Preferences(this)
        val data=intent.getParcelableExtra<KK>("data")

        tv_nama_anggota.text=data.nama?.capitalize()
        tv_alamat_kk.text=data.alamat?.capitalize()
        tv_anggota.text=data.jlh_anggota?.capitalize()
        tv_kk_detail.text=data.kk?.capitalize()
        tv_hub_detail.text=data.hubungan?.capitalize()

        if (data.alamat==""){
            field_alamat.visibility= View.GONE
            c_alamat.visibility= View.GONE
            tv_alamat_kk.visibility=View.GONE
        }
        if (data.jlh_anggota==""){
            field_anggota.visibility= View.GONE
            c_anggota.visibility= View.GONE
            tv_anggota.visibility=View.GONE
        }

        iv_back_kk.setOnClickListener {
            var intent= Intent(this@KKDetailActivity, HomeActivity::class.java).putExtra("toLoad","KKFragment")
            startActivity(intent)
        }

        btn_edit.setOnClickListener{
            var intent= Intent(this@KKDetailActivity, FormKKActivity::class.java).putExtra("data",data)
            startActivity(intent)
        }

        btn_delete_kk.setOnClickListener{
            hapusData()
        }
    }

    private fun hapusData() {
        val data=intent.getParcelableExtra<KK>("data")
        var alert= AlertDialog.Builder(this)
        alert.setTitle("Hapus Data")
        alert.setPositiveButton("Hapus", DialogInterface.OnClickListener{
            dialog, which ->
            val dbKK= FirebaseDatabase.getInstance().getReference("KK").child(preferences.getValues("mesjid").toString()).child(data.id.toString())
            dbKK.removeValue()
            var intent=Intent(this@KKDetailActivity,HomeActivity::class.java).putExtra("toLoad","KKFragment")
            startActivity(intent)
            finish()
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

    override fun onBackPressed() {
        startActivity(Intent(this@KKDetailActivity,HomeActivity::class.java).putExtra("toLoad","KKFragment"))
        finish()
    }
}