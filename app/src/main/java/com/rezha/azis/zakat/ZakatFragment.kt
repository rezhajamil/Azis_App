package com.rezha.azis.zakat

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.rezha.azis.R
import kotlinx.android.synthetic.main.fragment_zakat.*
import java.util.*


class ZakatFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_zakat, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_add_zakat.setOnClickListener {
            var intent = Intent(activity, FormZakatActivity::class.java)
            startActivity(intent)
        }
    }
}