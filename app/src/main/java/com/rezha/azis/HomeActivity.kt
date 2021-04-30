package com.rezha.azis

import android.content.Intent
import android.graphics.Insets.add
import android.os.Build
import android.os.Build.ID
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.view.OneShotPreDrawListener.add
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.rezha.azis.infaq.InfaqFragment
import com.rezha.azis.sedekah.SedekahFragment
import com.rezha.azis.zakat.ZakatFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.fragment_zakat.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fragmentZakat= ZakatFragment()
        val fragmentInfaq= InfaqFragment()
        val fragmentSedekah= SedekahFragment()
        var intentFragment= intent.getStringExtra("toLoad")

        ic_zakat.setOnClickListener{
            menuZakat(fragmentZakat)
        }
        ic_infaq.setOnClickListener{
            menuInfaq(fragmentInfaq)
        }
        ic_sedekah.setOnClickListener{
            menuSedekah(fragmentSedekah)
        }

        when(intentFragment){
            "ZakatFragment"->{
                menuZakat(fragmentZakat)
            }
            "InfaqFragment"->{
                menuInfaq(fragmentInfaq)
            }
            "SedekahFragment"->{
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
        changeIcon(ic_infaq,R.drawable.ic_infaq)
        changeBg(ic_infaq,R.color.white)
        changeIcon(ic_sedekah,R.drawable.ic_sedekah)
        changeBg(ic_sedekah,R.color.white)
    }

    private fun menuInfaq(fragmentInfaq:Fragment){
        setFragment(fragmentInfaq)
        changeIcon(ic_zakat,R.drawable.ic_zakat)
        changeBg(ic_zakat,R.color.white)
        changeIcon(ic_infaq,R.drawable.ic_infaq_active)
        changeBg(ic_infaq,R.color.premier)
        changeIcon(ic_sedekah,R.drawable.ic_sedekah)
        changeBg(ic_sedekah,R.color.white)
    }

    private fun menuSedekah(fragmentSedekah:Fragment){
        setFragment(fragmentSedekah)
        changeIcon(ic_zakat,R.drawable.ic_zakat)
        changeBg(ic_zakat,R.color.white)
        changeIcon(ic_infaq,R.drawable.ic_infaq)
        changeBg(ic_infaq,R.color.white)
        changeIcon(ic_sedekah,R.drawable.ic_sedekah_active)
        changeBg(ic_sedekah,R.color.premier)
    }
}