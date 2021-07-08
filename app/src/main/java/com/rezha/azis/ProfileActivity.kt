package com.rezha.azis

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.rezha.azis.sign.EditDataActivity
import com.rezha.azis.sign.GantiPasswordActivity
import com.rezha.azis.sign.SignInActivity
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var preferences: Preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        preferences= Preferences(this)
        tv_profil_username.text=preferences.getValues("username")
        tv_profil_nama.text=preferences.getValues("nama")
        tv_profil_telp.text=preferences.getValues("telp")
        tv_profil_mesjid.text=preferences.getValues("mesjid")+", "+preferences.getValues("alamat_mesjid")

        btn_edit_data.setOnClickListener {
            startActivity(Intent(this@ProfileActivity,EditDataActivity::class.java))
        }
        btn_ganti_password.setOnClickListener {
            startActivity(Intent(this@ProfileActivity,GantiPasswordActivity::class.java))
        }
        btn_logout.setOnClickListener {
            var alert= AlertDialog.Builder(this)
            alert.setTitle("Logout Akun?")
            alert.setPositiveButton("Logout", DialogInterface.OnClickListener{
                dialog, which ->
                preferences.setValues("status","0")
                startActivity(Intent(this@ProfileActivity,SignInActivity::class.java))
                finishAffinity()
                Toast.makeText(this,"Logout", Toast.LENGTH_SHORT).show()
            })
            alert.setNegativeButton("Batal", DialogInterface.OnClickListener{
                dialog, which ->
                dialog.cancel()
                dialog.dismiss()
            })
            alert.create()
            alert.show()
        }

        iv_back_profil.setOnClickListener {
            startActivity(Intent(this@ProfileActivity,MenuActivity::class.java))
            finish()
        }
    }
}