package com.rezha.azis.infaq

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rezha.azis.R
import com.rezha.azis.sedekah.FormSedekahActivity
import kotlinx.android.synthetic.main.fragment_infaq.*
import kotlinx.android.synthetic.main.fragment_sedekah.*


class InfaqFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_infaq, container, false)
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_add_infaq.setOnClickListener{
            var intent= Intent(activity, FormInfaqActivity::class.java)
            startActivity(intent)
    }
    }

}