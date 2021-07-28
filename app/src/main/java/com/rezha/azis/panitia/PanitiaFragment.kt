package com.rezha.azis.panitia

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.*
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.VerticalAlignment
import com.rezha.azis.HomeActivity
import com.rezha.azis.MenuActivity
import com.rezha.azis.R
import com.rezha.azis.model.Panitia
import com.rezha.azis.model.User
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.fragment_panitia.*
import kotlinx.android.synthetic.main.fragment_zakat.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class PanitiaFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var preferences: Preferences
    private lateinit var mDatabaseQuery: Query

    private var dataList=ArrayList<Panitia>()
    private var dataMesjid=ArrayList<String>()
    var handler= Handler()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_panitia, container, false)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        preferences= Preferences(context!!)
        FirebaseDatabase.getInstance().getReference("User").orderByChild("mesjid").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var nMesjid=""
                for (snapshot in snapshot.children){
                    var namaMesjid=snapshot.getValue(User::class.java)
                    if (nMesjid!=namaMesjid?.mesjid){
                        dataMesjid.add(namaMesjid?.mesjid!!.toString().capitalize())
                        Log.v("mesjid",""+dataMesjid)
                        nMesjid=namaMesjid?.mesjid!!
                        Log.v("mesjid2",""+dataMesjid)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        Log.v("mesjid3",""+dataMesjid)

        if (preferences.getValues("role")=="admin"){
            spinner_mesjid_panitia.visibility=View.VISIBLE
        }
        else{
            spinner_mesjid_panitia.visibility=View.GONE
        }
        var adapter= ArrayAdapter(context!!.applicationContext,R.layout.spinner_dropdown_layout_mesjid,dataMesjid)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinner_mesjid_panitia.adapter=adapter
        spinner_mesjid_panitia.onItemSelectedListener=this

        btn_add_panitia.setOnClickListener{
            var intent= Intent(activity,FormPanitiaActivity::class.java)
            startActivity(intent)
            activity?.finish()
    }
        val today= LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy")
        val formatted = today.format(formatter)
        tv_tahun_start.setText(formatted)

        val datePickerStart = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tahun")
                .build()

        tv_tahun_start.setOnClickListener {
            handler.postDelayed({
                var dice=false
                if(!dice){
                    tv_tahun_start.setBackgroundResource(R.color.premier)
                }else{
                    tv_tahun_start.setBackgroundResource(R.color.quartener)
                }
                dice=!dice
            },10)
            tv_tahun_start.setBackgroundResource(R.color.quartener)
            datePickerStart.show(fragmentManager!!, datePickerStart.toString())
        }

        datePickerStart.addOnPositiveButtonClickListener {
            var tgl=formatDate(datePickerStart.headerText,"yyyy")
            tv_tahun_start.setText(tgl)
            getData()
        }

        rv_panitia.layoutManager= LinearLayoutManager(this.context)
        getData()

        iv_back_menu.setOnClickListener {
            startActivity(Intent(context,MenuActivity::class.java))
            activity?.finish()
        }
    }


    private fun getData() {
        var thnMulai=tv_tahun_start.text.toString()

        mDatabaseQuery= FirebaseDatabase.getInstance().getReference("Panitia").child(preferences.getValues("mesjid").toString()).orderByChild("tahun").equalTo(thnMulai)
        mDatabaseQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for(getdataSnapshot in snapshot.children){
                    var panitia=getdataSnapshot.getValue(Panitia::class.java)
                    dataList.add(panitia!!)
                }

                if (rv_panitia!=null){
                    rv_panitia.adapter= PanitiaAdapter(dataList){
                        var intent=Intent(context, PanitiaDetailActivity::class.java).putExtra("data",it)
                        startActivity(intent)
                    }
                }
                else{
                    activity?.finish()
                    startActivity(Intent(activity?.applicationContext, HomeActivity::class.java).putExtra("toLoad","PanitiaFragment"))
                }


                shimmerSedekah.stopShimmer()
                shimmerSedekah.visibility=View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,""+error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun formatDate(date:String,format: String):String{
        var formattedDate=""
        val sdf= SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        sdf.timeZone= TimeZone.getTimeZone("UTC")

        try{
            val parseDate=sdf.parse(date)
            formattedDate= SimpleDateFormat(format).format(parseDate)
        }catch (e: ParseException){
            e.printStackTrace()
        }
        return formattedDate
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        preferences.setValues("mesjid",spinner_mesjid_panitia.selectedItem.toString())
        getData()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}