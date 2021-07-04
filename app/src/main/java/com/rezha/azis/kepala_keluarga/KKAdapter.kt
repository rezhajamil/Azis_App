package com.rezha.azis.kepala_keluarga

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rezha.azis.R
import com.rezha.azis.model.KK
import java.util.*
import kotlin.collections.ArrayList

class KKAdapter(private var data: ArrayList<KK>,
                private val listener: (KK) -> Unit) : RecyclerView.Adapter<KKAdapter.ViewHolder>() {
    lateinit var contextAdapter: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater= LayoutInflater.from(parent.context)
        contextAdapter=parent.context
        val inflatedView=layoutInflater.inflate(R.layout.row_item_kk,parent,false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data[position],listener,contextAdapter)
    }

    override fun getItemCount(): Int =data.size

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private val tvNama=view.findViewById<TextView>(R.id.tv_nama_anggota)
        private val tvAlamat=view.findViewById<TextView>(R.id.tv_alamat_kk)
        private val tvKK=view.findViewById<TextView>(R.id.tv_nama_kk)
        private val tvHub=view.findViewById<TextView>(R.id.tv_hubungan_keluarga)

        fun bindItem(data: KK, listener: (KK) -> Unit, contextAdapter: Context) {
            tvNama.setText(data.nama.toString().capitalize())
            tvAlamat.setText(data.alamat.toString().capitalize())
            tvKK.setText(data.kk.toString().capitalize())
            tvHub.setText(data.hubungan.toString().capitalize())
            itemView.setOnClickListener {
                listener(data)
            }
        }

    }
}

