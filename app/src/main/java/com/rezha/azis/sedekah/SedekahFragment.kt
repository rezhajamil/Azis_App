package com.rezha.azis.sedekah

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.*
import com.rezha.azis.R
import com.rezha.azis.model.Sedekah
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.fragment_sedekah.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class SedekahFragment : Fragment() {
    private lateinit var preferences: Preferences
    private lateinit var mDatabaseQuery: Query

    private var dataList=ArrayList<Sedekah>()
    var handler= Handler()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sedekah, container, false)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_add_sedekah.setOnClickListener{
            var intent= Intent(activity,FormSedekahActivity::class.java)
            startActivity(intent)
    }
        val today= LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val formatted = today.format(formatter)
        val formattedMonth = today.withDayOfMonth(1).format(formatter)
        tv_tgl_start_sedekah.setText(formattedMonth)
        tv_tgl_end_sedekah.setText(formatted)

        val datePickerStart = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .build()

        tv_tgl_start_sedekah.setOnClickListener {
            handler.postDelayed({
                var dice=false
                if(!dice){
                    tv_tgl_start_sedekah.setBackgroundResource(R.color.premier)
                }else{
                    tv_tgl_start_sedekah.setBackgroundResource(R.color.quartener)
                }
                dice=!dice
            },10)
            tv_tgl_start_sedekah.setBackgroundResource(R.color.quartener)
            datePickerStart.show(fragmentManager!!, datePickerStart.toString())
        }

        datePickerStart.addOnPositiveButtonClickListener {
            var tgl=formatDate(datePickerStart.headerText,"dd MMMM yyyy")
            tv_tgl_start_sedekah.setText(tgl)
            getData()
        }

        val datePickerEnd = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .build()

        tv_tgl_end_sedekah.setOnClickListener {
            handler.postDelayed({
                var dice=false
                if(!dice){
                    tv_tgl_end_sedekah.setBackgroundResource(R.color.premier)
                }else{
                    tv_tgl_end_sedekah.setBackgroundResource(R.color.quartener)
                }
                dice=!dice
            },10)
            tv_tgl_end_sedekah.setBackgroundResource(R.color.quartener)
            datePickerEnd.show(fragmentManager!!, datePickerEnd.toString())
        }

        datePickerEnd.addOnPositiveButtonClickListener {
            tv_tgl_end_sedekah.setText(formatDate(datePickerEnd.headerText,"dd MMMM yyyy"))
            getData()
        }

        rv_sedekah.layoutManager= LinearLayoutManager(context)
        getData()
    }

    private fun getData() {
        var tglMulai=formatDate(tv_tgl_start_sedekah.text.toString(),"yyyy-MM-dd")
        var tglAkhir=formatDate(tv_tgl_end_sedekah.text.toString(),"yyyy-MM-dd")

        preferences= Preferences(activity!!.applicationContext)
        mDatabaseQuery= FirebaseDatabase.getInstance().getReference("Sedekah").orderByChild("tanggal").startAt(tglMulai).endAt(tglAkhir)

        mDatabaseQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for(getdataSnapshot in snapshot.children){
                    var sedekah=getdataSnapshot.getValue(Sedekah::class.java)
                    dataList.add(sedekah!!)
                }

                rv_sedekah.adapter= SedekahAdapter(dataList){
                    var intent=Intent(context, SedekahDetailActivity::class.java).putExtra("data",it)
                    startActivity(intent)
                }

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

}