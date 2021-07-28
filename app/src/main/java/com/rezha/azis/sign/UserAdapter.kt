package com.rezha.azis.sign

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rezha.azis.R
import com.rezha.azis.model.User
import java.util.*
import kotlin.collections.ArrayList

class UserAdapter(private var data: ArrayList<User>,
                  private val listener: (User) -> Unit) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    lateinit var contextAdapter: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater= LayoutInflater.from(parent.context)
        contextAdapter=parent.context
        val inflatedView=layoutInflater.inflate(R.layout.row_item_user,parent,false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data[position],listener,contextAdapter)
    }

    override fun getItemCount(): Int =data.size

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private val tvUsername=view.findViewById<TextView>(R.id.tv_username)
        private val tvMesjid=view.findViewById<TextView>(R.id.tv_mesjid)

        fun bindItem(data: User, listener: (User) -> Unit, contextAdapter: Context) {
            tvUsername.setText(data.username.toString().capitalize())
            tvMesjid.setText(data.mesjid.toString().capitalize())
            itemView.setOnClickListener {
                listener(data)
            }
        }

    }
}

