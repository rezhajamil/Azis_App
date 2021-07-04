package com.rezha.azis.sign

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.rezha.azis.MenuActivity
import com.rezha.azis.ProfileActivity
import com.rezha.azis.R
import com.rezha.azis.model.User
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.activity_edit_data.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class EditDataActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_edit_data)

        preferences= Preferences(this)
        mFirebaseInstance= FirebaseDatabase.getInstance()
        mDatabase= FirebaseDatabase.getInstance().getReference()
        mDatabaseReference=mFirebaseInstance.getReference("User")

        et_edit_nama.setText(preferences.getValues("nama").toString())
        et_edit_telp.setText(preferences.getValues("telp").toString())
        et_edit_mesjid.setText(preferences.getValues("mesjid").toString())
        et_edit_alamat_mesjid.setText(preferences.getValues("alamat_mesjid").toString())

        Log.v("pref",""+preferences.getValues("alamat_mesjid").toString())

        btn_simpan_edit.setOnClickListener {
            sUsername= preferences.getValues("username").toString()
            sPassword=preferences.getValues("password").toString()
            sCPassword=preferences.getValues("password").toString()
            sNama=et_edit_nama.text.toString()
            sTelp=et_edit_telp.text.toString()
            sMesjid=et_edit_mesjid.text.toString()
            sAlamat=et_edit_alamat_mesjid.text.toString()

           if(sNama.equals("")){
                et_nama.error="Silahkan isi Nama Anda"
                et_nama.requestFocus()
            }
            else if(sTelp.equals("")){
                et_telp.error="Silahkan isi Nomor Telepon Anda"
                et_telp.requestFocus()
            }
            else if(sMesjid.equals("")){
                et_mesjid.error="Silahkan isi Nama Mesjid Anda"
                et_mesjid.requestFocus()
            }
            else if(sAlamat.equals("")){
                et_alamat_mesjid.error="Silahkan isi Alamat Mesjid Anda"
                et_alamat_mesjid.requestFocus()
            }
            else{
                saveUsername(sUsername,sPassword,sNama,sTelp,sMesjid,sAlamat)
            }
        }

        iv_back_edit.setOnClickListener {
            startActivity(Intent(this@EditDataActivity,ProfileActivity::class.java))
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
                startActivity(Intent(this@EditDataActivity, ProfileActivity::class.java))
                finish()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditDataActivity,""+error.message, Toast.LENGTH_LONG).show()
            }

        })

    }
}