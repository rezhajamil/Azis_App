package com.rezha.azis.sedekah

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rezha.azis.R
import com.rezha.azis.model.Sedekah
import com.rezha.azis.model.Zakat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SedekahAdapter(private var data: ArrayList<Sedekah>,
                     private val listener:(Sedekah) -> Unit) : RecyclerView.Adapter<SedekahAdapter.ViewHolder>() {

    lateinit var contextAdapter:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        contextAdapter=parent.context
        val inflatedView=layoutInflater.inflate(R.layout.row_item_zakat,parent,false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: SedekahAdapter.ViewHolder, position: Int) {
        holder.bindItem(data[position],listener,contextAdapter)
    }

    override fun getItemCount(): Int =data.size

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private val tvJenis=view.findViewById<TextView>(R.id.tv_jenis)
        private val tvNama=view.findViewById<TextView>(R.id.tv_nama)
        private val tvTanggal=view.findViewById<TextView>(R.id.tv_tanggal)
        private val tvBeras=view.findViewById<TextView>(R.id.tv_beras)
        private val tvUang=view.findViewById<TextView>(R.id.tv_uang)

        fun bindItem(data: Sedekah, listener: (Sedekah) -> Unit,context: Context){
            tvJenis.setText("("+data.jenis+")")
            tvNama.setText(data.nama)
            tvTanggal.setText(formatDate(data.tanggal.toString(),"dd MMMM yyyy"))
            if(data.beras.equals("0")){
                tvBeras.setText("-")
            }else{
                tvBeras.setText(data.beras + " Kg")
            }
            if(data.uang.equals("0")){
                tvUang.setText("-")
            }else{
                tvUang.setText("Rp. "+data.uang)
            }

            itemView.setOnClickListener {
                listener(data)
            }
        }

        private fun formatDate(date: String, format: String):String{
            var formattedDate=""
            val sdf= SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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

}
