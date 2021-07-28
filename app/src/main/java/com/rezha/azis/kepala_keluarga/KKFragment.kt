package com.rezha.azis.kepala_keluarga

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
import com.google.firebase.database.*
import com.rezha.azis.HomeActivity
import com.rezha.azis.MenuActivity
import com.rezha.azis.R
import com.rezha.azis.model.KK
import com.rezha.azis.model.User
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.fragment_kk.*
import kotlinx.android.synthetic.main.fragment_kk.iv_back_menu
import kotlinx.android.synthetic.main.fragment_panitia.*
import kotlinx.android.synthetic.main.fragment_zakat.*
import kotlin.collections.ArrayList


class KKFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var preferences: Preferences
    private lateinit var mDatabaseQuery: Query

    private var dataList=ArrayList<KK>()
    private var dataMesjid=ArrayList<String>()
    var handler= Handler()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kk, container, false)
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
            spinner_mesjid_kk.visibility=View.VISIBLE
        }
        else{
            spinner_mesjid_kk.visibility=View.GONE
        }
        var adapter= ArrayAdapter(context!!.applicationContext,R.layout.spinner_dropdown_layout_mesjid,dataMesjid)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinner_mesjid_kk.adapter=adapter
        spinner_mesjid_kk.onItemSelectedListener=this
        btn_add_kk.setOnClickListener{
            var intent= Intent(activity, FormKKActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        rv_kk.layoutManager= LinearLayoutManager(context)
        getData()

        iv_back_menu.setOnClickListener {
            startActivity(Intent(context,MenuActivity::class.java))
            activity?.finish()
        }

    }
    private fun getData() {
//        preferences= Preferences(activity!!.applicationContext)
        mDatabaseQuery= FirebaseDatabase.getInstance().getReference("KK").child(preferences.getValues("mesjid").toString()).orderByChild("kk")
        mDatabaseQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for(getdataSnapshot in snapshot.children){
                    var kk=getdataSnapshot.getValue(KK::class.java)
                    dataList.add(kk!!)
                }
                if (rv_kk!=null){
                    rv_kk.adapter= KKAdapter(dataList){
                        var intent=Intent(context, KKDetailActivity::class.java).putExtra("data",it)
                        startActivity(intent)
                    }
                }
                else{
                    activity?.finish()
                    startActivity(Intent(activity?.applicationContext, HomeActivity::class.java).putExtra("toLoad","KKFragment"))
                }

                shimmerInfaq.stopShimmer()
                shimmerInfaq.visibility=View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,""+error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        preferences.setValues("mesjid",spinner_mesjid_kk.selectedItem.toString())
        getData()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}