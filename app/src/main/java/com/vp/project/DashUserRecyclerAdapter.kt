package com.vp.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vp.project.model.UserInfoDownloadEntity

class DashUserRecyclerAdapter(val itemList: ArrayList<UserInfoDownloadEntity>): RecyclerView.Adapter<DashUserRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashUserRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usersearch, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashUserRecyclerAdapter.ViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.subname.text = itemList[position].username
    }

    //커스텀 리스너
    interface OnItemClickListener{
        fun onItemClick(itemView: View, position: Int)
    }
    //객체 저장 변수
    private lateinit var mOnItemClickListener: OnItemClickListener
    //객체 전달 메서드
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        mOnItemClickListener = onItemClickListener
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val name: TextView  = itemView.findViewById(R.id.Name_TextView)
        val subname: TextView = itemView.findViewById(R.id.UserName_TextView)
        init {
            itemView.setOnClickListener {
                val pos = adapterPosition
                if(pos != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(itemView, pos)
                }
            }
        }
    }


}