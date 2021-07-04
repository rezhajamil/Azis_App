package com.rezha.azis.sign

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import com.rezha.azis.ProfileActivity
import com.rezha.azis.R
import com.rezha.azis.model.User
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.activity_edit_data.*
import kotlinx.android.synthetic.main.activity_ganti_password.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.btn_masuk

class GantiPasswordActivity : AppCompatActivity() {
    lateinit var sUsername:String
    lateinit var sPassword:String
    lateinit var sCPassword:String
    lateinit var sNama:String
    lateinit var sTelp:String
    lateinit var sMesjid:String
    lateinit var sAlamat:String

    lateinit var preferences: Preferences
    lateinit var mDatabaseReference: DatabaseReference
    lateinit var mFirebaseInstance: FirebaseDatabase
    lateinit var mDatabase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ganti_password)

        preferences= Preferences(this)
        mFirebaseInstance= FirebaseDatabase.getInstance()
        mDatabase= FirebaseDatabase.getInstance().getReference()
        mDatabaseReference=mFirebaseInstance.getReference("User")

        btn_save_pw.setOnClickListener {
            sUsername= preferences.getValues("username").toString()
            sPassword=et_password_new.text.toString()
            sCPassword=et_confirm_new.text.toString()
            sNama=preferences.getValues("nama").toString()
            sTelp=preferences.getValues("telp").toString()
            sMesjid=preferences.getValues("mesjid").toString()
            sAlamat=preferences.getValues("alamat_mesjid").toString()

            if(et_password_now.text.toString().equals("")){
                et_password_now.error="Silahkan isi Password Anda"
                et_password_now.requestFocus()
            }
            else if(sPassword.equals("")){
                et_password_new.error="Silahkan isi Password Baru Anda"
                et_password_new.requestFocus()
            }
            else if(sCPassword.equals("")){
                et_confirm_new.error="Silahkan isi Konfirmasi Password Baru Anda"
                et_confirm_new.requestFocus()
            }
            else if(sCPassword!=sPassword){
                et_confirm_new.error="Konfirmasi Password Baru harus sama"
                et_confirm_new.requestFocus()
            }
            else if(et_password_now.text.toString()!=preferences.getValues("password").toString()){
                et_password_now.error="Password Anda Salah"
                et_password_now.requestFocus()
            }
            else{
                saveUsername(sUsername,sPassword,sNama,sTelp,sMesjid,sAlamat)
            }
        }

        iv_back_pw.setOnClickListener {
            startActivity(Intent(this@GantiPasswordActivity, ProfileActivity::class.java))
            finish()
        }


    }

    private fun saveUsername(sUsername: String, sPassword: String, sNama: String, sTelp: String, sMesjid: String, sAlamat: String) {
        var user= User()
        user.username= sUsername
        user.password= sPassword
        user.nama= sNama
        user.telp=sTelp
        user.mesjid=sMesjid
        user.alamat_mesjid=sAlamat

        if(sUsername !=null){
            saveEdit(sUsername,user)
        }
    }

    private fun saveEdit(sUsername: String, data: User) {
        mDatabaseReference.child(sUsername).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mDatabaseReference.child(sUsername).setValue(data)
                startActivity(Intent(this@GantiPasswordActivity, ProfileActivity::class.java))
                finish()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GantiPasswordActivity,""+error.message, Toast.LENGTH_LONG).show()
            }

        })

    }
}