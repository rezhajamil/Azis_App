package com.rezha.azis.panitia

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rezha.azis.R
import com.rezha.azis.model.Panitia
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PanitiaAdapter(private var data: ArrayList<Panitia>,
                     private val listener:(Panitia) -> Unit) : RecyclerView.Adapter<PanitiaAdapter.ViewHolder>() {

    lateinit var contextAdapter:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        contextAdapter=parent.context
        val inflatedView=layoutInflater.inflate(R.layout.row_item_panitia,parent,false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: PanitiaAdapter.ViewHolder, position: Int) {
        holder.bindItem(data[position],listener,contextAdapter)
    }

    override fun getItemCount(): Int =data.size

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private val tvMesjid=view.findViewById<TextView>(R.id.tv_mesjid)
        private val tvNama=view.findViewById<TextView>(R.id.tv_nama)
        private val tvTahun=view.findViewById<TextView>(R.id.tv_tanggal)

        fun bindItem(data: Panitia, listener: (Panitia) -> Unit, context: Context){
            tvMesjid.setText((data.mesjid?.capitalize() ))
            tvNama.setText(data.nama?.capitalize())
            tvTahun.setText(data.tahun)

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
