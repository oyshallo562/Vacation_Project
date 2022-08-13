package com.vp.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vp.project.model.CountEntity

class HomeRecyclerAdapter(val itemList: ArrayList<CountEntity>): RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeRecyclerAdapter.ViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.pincount.text = itemList[position].count.toString()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView  = itemView.findViewById(R.id.Place_Name)
        val pincount: TextView = itemView.findViewById(R.id.Place_PinCount)
    }
}