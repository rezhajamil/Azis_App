package com.rezha.azis.zakat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rezha.azis.R
import com.rezha.azis.model.Zakat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ZakatAdapter(private var zakat: ArrayList<Zakat>,
                   private val listener:(Zakat) -> Unit) : RecyclerView.Adapter<ZakatAdapter.ViewHolder>() {

    lateinit var contextAdapter:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        contextAdapter=parent.context
        val inflatedView=layoutInflater.inflate(R.layout.row_item_zakat,parent,false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ZakatAdapter.ViewHolder, position: Int) {
        holder.bindItem(zakat[position],listener,contextAdapter)
    }

    override fun getItemCount(): Int =zakat.size

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private val tvNama=view.findViewById<TextView>(R.id.tv_nama)
        private val tvTanggal=view.findViewById<TextView>(R.id.tv_tanggal)
        private val tvAnggota=view.findViewById<TextView>(R.id.tv_anggota)
        private val tvUang=view.findViewById<TextView>(R.id.tv_uang)

        fun bindItem(zakat: Zakat, listener: (Zakat) -> Unit, contextAdapter: Context){
            if (zakat.jenis=="Beras"){
                tvUang.setText(zakat.beras+" Kg")
            }
            else{
                tvUang.setText("Rp."+zakat.uang)
            }
            tvNama.setText(zakat.nama)
            tvTanggal.setText(formatDate(zakat.tanggal.toString(),"dd MMMM yyyy"))
            tvAnggota.setText(zakat.anggota+" Orang")

            itemView.setOnClickListener {
                listener(zakat)
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
