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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.rezha.azis.MenuActivity
import com.rezha.azis.R
import com.rezha.azis.model.KK
import com.rezha.azis.utils.Preferences
import kotlinx.android.synthetic.main.fragment_kk.*
import kotlin.collections.ArrayList


class KKFragment : Fragment() {

    private lateinit var preferences: Preferences
    private lateinit var mDatabaseQuery: Query

    private var dataList=ArrayList<KK>()
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
                    getData()
                }

                shimmerInfaq.stopShimmer()
                shimmerInfaq.visibility=View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,""+error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

}