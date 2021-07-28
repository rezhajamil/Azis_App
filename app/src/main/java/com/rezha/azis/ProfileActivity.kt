package com.rezha.azis

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.rezha.azis.kepala_keluarga.KKAdapter
import com.rezha.azis.kepala_keluarga.KKDetailActivity
import com.rezha.azis.model.KK
import com.rezha.azis.model.User
import com.rezha.azis.sign.EditDataActivity
import com.rezha.azis.sign.GantiPasswordActivity
import com.rezha.azis.sign.SignInActivity
import com.rezha.azis.sign.UserAdapter
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_kk.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var preferences: Preferences
    private lateinit var mDatabaseQuery: Query
    private var dataList=ArrayList<User>()
    
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

        if (preferences.getValues("role")!="admin"){
            rv_user.visibility=View.GONE
            tvu.visibility=View.GONE
        }
        else{
            rv_user.layoutManager= LinearLayoutManager(this)
            getData()
        }
    }

    private fun getData() {
        mDatabaseQuery= FirebaseDatabase.getInstance().getReference("User").orderByChild("username")
        mDatabaseQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for(getdataSnapshot in snapshot.children){
                    var user=getdataSnapshot.getValue(User::class.java)
                    dataList.add(user!!)
                }
                if (rv_user!=null){
                    rv_user.adapter= UserAdapter(dataList){
                       hapusUser(it)
                    }
                }
                else{
                    finish()
                    startActivity(Intent(this@ProfileActivity, ProfileActivity::class.java))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity,""+error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun hapusUser(it: User) {
        val data=it
        var alert= AlertDialog.Builder(this)
        alert.setTitle("Hapus User "+data.username+" ?")
        alert.setPositiveButton("Hapus", DialogInterface.OnClickListener{
            dialog, which ->
            val dbUser= FirebaseDatabase.getInstance().getReference("User").child(data.username.toString())
            dbUser.removeValue()
            Toast.makeText(this,"User telah dihapus", Toast.LENGTH_SHORT).show()
        })
        alert.setNegativeButton("Batal", DialogInterface.OnClickListener{
            dialog, which ->
            dialog.cancel()
            dialog.dismiss()
        })
        alert.create()
        alert.show()
    }
}