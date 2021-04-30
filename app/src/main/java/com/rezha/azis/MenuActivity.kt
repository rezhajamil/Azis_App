package com.rezha.azis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.fragment_sedekah.*

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        var handler=Handler()

        iv_zakat.setOnClickListener{
            handler.postDelayed({
               var dice=false
               if(!dice){
                   iv_zakat.setImageResource(R.drawable.zakat_menu)
               }else{
                   iv_zakat.setImageResource(R.drawable.zakat_menu_active)
               }
                dice=!dice
            },100)
            iv_zakat.setImageResource(R.drawable.zakat_menu_active)
            var intent=Intent(this@MenuActivity,HomeActivity::class.java).putExtra("toLoad","ZakatFragment")
            startActivity(intent)
        }
        iv_infaq.setOnClickListener{
            handler.postDelayed({
                var dice=false
                if(!dice){
                    iv_infaq.setImageResource(R.drawable.infaq_menu)
                }else{
                    iv_infaq.setImageResource(R.drawable.infaq_menu_active)
                }
                dice=!dice
            },100)
            iv_infaq.setImageResource(R.drawable.infaq_menu_active)
            var intent=Intent(this@MenuActivity,HomeActivity::class.java).putExtra("toLoad","InfaqFragment")
            startActivity(intent)
        }
        iv_sedekah.setOnClickListener{
            handler.postDelayed({
                var dice=false
                if(!dice){
                    iv_sedekah.setImageResource(R.drawable.sedekah_menu)
                }else{
                    iv_sedekah.setImageResource(R.drawable.sedekah_menu_active)
                }
                dice=!dice
            },100)
            iv_sedekah.setImageResource(R.drawable.sedekah_menu_active)
            var intent=Intent(this@MenuActivity,HomeActivity::class.java).putExtra("toLoad","SedekahFragment")
            startActivity(intent)
        }
    }
}

