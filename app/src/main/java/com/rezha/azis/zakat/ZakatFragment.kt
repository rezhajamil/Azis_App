package com.rezha.azis.zakat

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
import com.rezha.azis.model.Zakat
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.fragment_zakat.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class ZakatFragment : Fragment() {
    private lateinit var preferences: Preferences
    private lateinit var mDatabaseQuery: Query

    private var dataList=ArrayList<Zakat>()
    var handler= Handler()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_zakat, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_add_zakat.setOnClickListener {
            var intent = Intent(activity, FormZakatActivity::class.java)
            startActivity(intent)
        }


        val today= LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val formatted = today.format(formatter)
        val formattedMonth = today.withDayOfMonth(1).format(formatter)
        tv_tgl_start.setText(formattedMonth)
        tv_tgl_end.setText(formatted)

        val datePickerStart = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .build()

        tv_tgl_start.setOnClickListener {
            handler.postDelayed({
                var dice=false
                if(!dice){
                    tv_tgl_start.setBackgroundResource(R.color.premier)
                }else{
                    tv_tgl_start.setBackgroundResource(R.color.quartener)
                }
                dice=!dice
            },10)
            tv_tgl_start.setBackgroundResource(R.color.quartener)
            datePickerStart.show(fragmentManager!!, datePickerStart.toString())
        }

        datePickerStart.addOnPositiveButtonClickListener {
            var tgl=formatDate(datePickerStart.headerText,"dd MMMM yyyy")
            tv_tgl_start.setText(tgl)
            getData()
        }

        val datePickerEnd = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .build()

        tv_tgl_end.setOnClickListener {
            handler.postDelayed({
                var dice=false
                if(!dice){
                    tv_tgl_end.setBackgroundResource(R.color.premier)
                }else{
                    tv_tgl_end.setBackgroundResource(R.color.quartener)
                }
                dice=!dice
            },10)
            tv_tgl_end.setBackgroundResource(R.color.quartener)
            datePickerEnd.show(fragmentManager!!, datePickerEnd.toString())
        }

        datePickerEnd.addOnPositiveButtonClickListener {
            tv_tgl_end.setText(formatDate(datePickerEnd.headerText,"dd MMMM yyyy"))
            getData()
        }

        rv_zakat.layoutManager=LinearLayoutManager(context)
        getData()
    }

    private fun getData() {
        var tglMulai=formatDate(tv_tgl_start.text.toString(),"yyyy-MM-dd")
        var tglAkhir=formatDate(tv_tgl_end.text.toString(),"yyyy-MM-dd")

        preferences= Preferences(activity!!.applicationContext)
        mDatabaseQuery= FirebaseDatabase.getInstance().getReference("Zakat").orderByChild("tanggal").startAt(tglMulai).endAt(tglAkhir)

        mDatabaseQuery.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for(getdataSnapshot in snapshot.children){
                    var zakat=getdataSnapshot.getValue(Zakat::class.java)
                    dataList.add(zakat!!)
                }

                rv_zakat.adapter=ZakatAdapter(dataList){
                    var intent=Intent(context,ZakatDetailActivity::class.java).putExtra("data",it)
                    startActivity(intent)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,""+error.message,Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun formatDate(date:String,format: String):String{
        var formattedDate=""
        val sdf= SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
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