package com.careplus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.careplus.R
import kotlinx.android.synthetic.main.item_time.view.*

class TimeAdapter : RecyclerView.Adapter<TimeAdapter.ViewHolder>() {

    val times = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_time, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = times.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        times[position].let {
            holder.view.tvItemTime.text = it
            holder.view.btnRemove.setOnClickListener {
                times.removeAt(position)
                notifyDataSetChanged()
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}