package com.example.roomdatabase.RecycleView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabase.databinding.DataShowBinding

class ViewAdapter(private val dataList: List<ShowData>):RecyclerView.Adapter<ViewAdapter.myViewHolder>() {
    var onClick:((ShowData) -> Unit)?= null
    class myViewHolder(val binding: DataShowBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return myViewHolder(DataShowBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val allData = dataList[position]
        holder.binding.apply {
            fNameList.text = allData.fname
            lNameList.text = allData.lName
            rollList.text = allData.roll.toString()
        }
        holder.itemView.setOnClickListener {
            onClick?.invoke(allData)
        }
    }
}