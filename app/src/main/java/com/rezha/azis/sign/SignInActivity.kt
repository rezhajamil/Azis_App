package com.rezha.azis.sign

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import com.rezha.azis.MenuActivity
import com.rezha.azis.R
import com.rezha.azis.model.User
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.et_password
import kotlinx.android.synthetic.main.activity_sign_in.et_username
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignInActivity : AppCompatActivity() {

    lateinit var iUsername:String
    lateinit var iPassword:String
    lateinit var mDatabase: DatabaseReference
    lateinit var preferences: Preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mDatabase= FirebaseDatabase.getInstance().getReference("User")
        preferences=Preferences(this)

        if(preferences.getValues("status").equals("1")){
            finishAffinity()
            var intent= Intent(this@SignInActivity, MenuActivity::class.java)
            startActivity(intent)
        }
        btn_home.setOnClickListener {
            iUsername=et_username.text.toString()
            iPassword=et_password.text.toString()

            if (iUsername.equals("")){
                et_username.error="Silahkan isi Username Anda"
                et_username.requestFocus()
            }
            else if (iPassword.equals("")){
                et_password.error="Silahkan isi Password Anda"
                et_password.requestFocus()
            }
            else{
                var statUsername=iUsername.indexOf(".")
                if (statUsername>=0){
                    et_username.error="Simbol (.) titik tidak diizinkan"
                    et_username.requestFocus()
                }
                else{
                    pushLogin(iUsername,iPassword)
                }
            }
        }

        btn_daftar.setOnClickListener {
            var intent= Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun pushLogin(iUsername: String, iPassword: String) {
        mDatabase.child(iUsername).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var user=snapshot.getValue(User::class.java)
                if(user==null){
                    Toast.makeText(this@SignInActivity,"User tidak ditemukan", Toast.LENGTH_LONG).show()
                }
                else{
                    if(user.password.equals(iPassword)){

                        preferences.setValues("nama",user.nama.toString())
                        preferences.setValues("username",user.username.toString())
                        preferences.setValues("password",user.password.toString())
                        preferences.setValues("url",user.url.toString())
                        preferences.setValues("telp",user.telp.toString())
                        preferences.setValues("mesjid",user.mesjid.toString())
                        preferences.setValues("alamat_mesjid",user.alamat_mesjid.toString())
                        preferences.setValues("status","1")

                        var intent= Intent(this@SignInActivity, MenuActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this@SignInActivity,"Password Salah", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignInActivity,"User tidak ditemukan", Toast.LENGTH_LONG).show()
            }
        })
    }
}