package com.rezha.azis.zakat

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.rezha.azis.R
import kotlinx.android.synthetic.main.activity_form_zakat.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class FormZakatActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_zakat)

        val today= LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val formatted = today.format(formatter)

        et_tanggal.setText(formatted)

        val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .build()

        et_tanggal.setOnClickListener {
            datePicker.show(supportFragmentManager,datePicker.toString())
        }
        datePicker.addOnPositiveButtonClickListener {
            et_tanggal.setText(formatDate(datePicker.headerText,"dd MMMM yyyy"))
        }

        var adapter=ArrayAdapter.createFromResource(
                this,
                R.array.jenis_zakat,
                R.layout.color_spinner_layout
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinner_jenis_zakat.adapter=adapter
        spinner_jenis_zakat.onItemSelectedListener=this

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
        Toast.makeText(this,spinner_jenis_zakat.selectedItem.toString(),Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}