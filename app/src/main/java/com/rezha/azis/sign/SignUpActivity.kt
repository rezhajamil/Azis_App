package com.rezha.azis.sign

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.*
import com.rezha.azis.MenuActivity
import com.rezha.azis.R
import com.rezha.azis.model.User
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.activity_form_zakat.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var sUsername:String
    lateinit var sPassword:String
    lateinit var sCPassword:String
    lateinit var sNama:String
    lateinit var sTelp:String
    lateinit var sMesjid:String
    lateinit var sAlamat:String

    private  var dataMesjid=ArrayList<String>()
    private var datalist=ArrayList<User>()

    lateinit var preferences: Preferences
    lateinit var mDatabaseReference: DatabaseReference
    lateinit var mFirebaseInstance: FirebaseDatabase
    lateinit var mDatabase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        preferences= Preferences(this)
        mFirebaseInstance= FirebaseDatabase.getInstance()
        mDatabase= FirebaseDatabase.getInstance().getReference()
        mDatabaseReference=mFirebaseInstance.getReference("User")

        dataMesjid.add("Lainnya")
        mDatabaseReference.orderByChild("mesjid").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var nMesjid=""
                for (snapshot in snapshot.children){
                    var namaMesjid=snapshot.getValue(User::class.java)
                    if (nMesjid!=namaMesjid?.mesjid){
                        Log.v("mesjid","nMesjid= "+nMesjid)
                        Log.v("mesjid","namamesjid= "+namaMesjid?.mesjid)
                        dataMesjid.add(namaMesjid?.mesjid!!.toString().capitalize())
                        datalist.add(namaMesjid!!)
                        nMesjid=namaMesjid?.mesjid!!
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        Log.v("mesjid",""+dataMesjid)

        var adapter= ArrayAdapter(this,R.layout.spinner_dropdown_layout_signup,dataMesjid)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinner_mesjid.adapter=adapter
        spinner_mesjid.onItemSelectedListener=this

        btn_masuk.setOnClickListener {
            sUsername=et_username.text.toString()
            sPassword=et_password.text.toString()
            sCPassword=et_confirm.text.toString()
            sNama=et_nama.text.toString().capitalize()
            sTelp=et_telp.text.toString()
            if (et_mesjid.visibility==View.VISIBLE){
                sMesjid="Mesjid "+et_mesjid.text.toString().capitalize()
            }
            else{
                sMesjid=spinner_mesjid.selectedItem.toString()
            }
            sAlamat=et_alamat_mesjid.text.toString().capitalize()

            if(sUsername.equals("")){
                et_username.error="Silahkan isi Username Anda"
                et_username.requestFocus()
            }
            else if(sPassword.equals("")){
                et_password.error="Silahkan isi Password Anda"
                et_password.requestFocus()
            }
            else if(sCPassword.equals("")){
                et_confirm.error="Silahkan isi Konfirmasi Password Anda"
                et_confirm.requestFocus()
            }
            else if(sNama.equals("")){
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
            else if(sCPassword!=sPassword){
                et_confirm.error="Konfirmasi Password harus sama"
                et_confirm.requestFocus()
            }
            else{
                var statUsername=sUsername.indexOf(".")
                if (statUsername>=0){
                    et_username.error="Simbol (.) titik tidak diizinkan"
                    et_username.requestFocus()
                }
                else{
                    saveUsername(sUsername,sPassword,sNama,sTelp,sMesjid,sAlamat)
                }
            }
        }

        iv_back.setOnClickListener {
            startActivity(Intent(this@SignUpActivity,SignInActivity::class.java))
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
            checkingUsername(sUsername,user)
        }
    }

    private fun checkingUsername(sUsername: String, data: User) {
        mDatabaseReference.child(sUsername).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var user=snapshot.getValue(User::class.java)
                if(user==null){
                    mDatabaseReference.child(sUsername).setValue(data)
                    preferences.setValues("nama", data.nama.toString())
                    preferences.setValues("username",data.username.toString())
                    preferences.setValues("password",data.password.toString())
                    preferences.setValues("url",data.url.toString())
                    preferences.setValues("telp",data.telp.toString())
                    preferences.setValues("mesjid",data.mesjid.toString())
                    preferences.setValues("alamat_mesjid",data.alamat_mesjid.toString())
                    preferences.setValues("status","1")
                    startActivity(Intent(this@SignUpActivity,MenuActivity::class.java))
                    finishAffinity()
                }
                else{
                    Toast.makeText(this@SignUpActivity,"User sudah digunakan", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignUpActivity,""+error.message, Toast.LENGTH_LONG).show()
            }

        })

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var selected=spinner_mesjid.selectedItem.toString()
        if (selected=="Lainnya"){
            et_mesjid.visibility=View.VISIBLE
            tx_mesjid.visibility=View.VISIBLE
            et_alamat_mesjid.isClickable=true
            et_alamat_mesjid.isCursorVisible=true
            et_alamat_mesjid.isEnabled=true
            et_alamat_mesjid.setText("")
        }
        else{
            et_mesjid.visibility=View.GONE
            tx_mesjid.visibility=View.GONE
            et_alamat_mesjid.isClickable=false
            et_alamat_mesjid.isCursorVisible=false
            et_alamat_mesjid.isEnabled=false

            for (data in datalist){
                if (selected==data?.mesjid){
                    et_alamat_mesjid.setText(data?.alamat_mesjid)
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}