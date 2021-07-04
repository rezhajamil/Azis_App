package com.rezha.azis

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.rezha.azis.kepala_keluarga.KKFragment
import com.rezha.azis.panitia.PanitiaFragment
import com.rezha.azis.zakat.ZakatFragment
import kotlinx.android.synthetic.main.activity_home.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fragmentZakat= ZakatFragment()
        val fragmentKK= KKFragment()
        val fragmentSedekah= PanitiaFragment()
        var intentFragment= intent.getStringExtra("toLoad")

        ic_zakat.setOnClickListener{
            menuZakat(fragmentZakat)
        }
        ic_kk.setOnClickListener{
            menuInfaq(fragmentKK)
        }
        ic_panitia.setOnClickListener{
            menuSedekah(fragmentSedekah)
        }

        when(intentFragment){
            "ZakatFragment"->{
                menuZakat(fragmentZakat)
            }
            "KKFragment"->{
                menuInfaq(fragmentKK)
            }
            "PanitiaFragment"->{
                menuSedekah(fragmentSedekah)
            }
        }
    }

    private fun formatDate(date:String,format: String):String{
        var formattedDate=""
        val sdf=SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
        sdf.timeZone= TimeZone.getTimeZone("UTC")

        try{
            val parseDate=sdf.parse(date)
            formattedDate=SimpleDateFormat(format).format(parseDate)
        }catch (e:ParseException){
            e.printStackTrace()
        }
        return formattedDate
    }

    private fun setFragment(fragment: Fragment,){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout2,fragment)
        fragmentTransaction.commit()
    }
    private fun changeIcon(imageView: ImageView, int: Int){
        imageView.setImageResource(int)
    }
    private fun changeBg(imageView: ImageView,int:Int){
        imageView.setBackgroundResource(int)
    }

    private fun menuZakat(fragmentZakat:Fragment){
        setFragment(fragmentZakat)
        changeIcon(ic_zakat,R.drawable.ic_zakat_active)
        changeBg(ic_zakat,R.color.premier)
        changeIcon(ic_kk,R.drawable.ic_kk)
        changeBg(ic_kk,R.color.white)
        changeIcon(ic_panitia,R.drawable.ic_sedekah)
        changeBg(ic_panitia,R.color.white)
    }

    private fun menuInfaq(fragmentKK:Fragment){
        setFragment(fragmentKK)
        changeIcon(ic_zakat,R.drawable.ic_zakat)
        changeBg(ic_zakat,R.color.white)
        changeIcon(ic_kk,R.drawable.ic_kk_active)
        changeBg(ic_kk,R.color.premier)
        changeIcon(ic_panitia,R.drawable.ic_sedekah)
        changeBg(ic_panitia,R.color.white)
    }

    private fun menuSedekah(fragmentPanitia:Fragment){
        setFragment(fragmentPanitia)
        changeIcon(ic_zakat,R.drawable.ic_zakat)
        changeBg(ic_zakat,R.color.white)
        changeIcon(ic_kk,R.drawable.ic_kk)
        changeBg(ic_kk,R.color.white)
        changeIcon(ic_panitia,R.drawable.ic_sedekah_active)
        changeBg(ic_panitia,R.color.premier)
    }

    override fun onBackPressed() {
        startActivity(Intent(this@HomeActivity,MenuActivity::class.java))
        finish()
    }
}