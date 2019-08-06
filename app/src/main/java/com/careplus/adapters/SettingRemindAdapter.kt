package com.careplus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.careplus.R
import kotlinx.android.synthetic.main.item_setting_remind.view.*

class SettingRemindAdapter : RecyclerView.Adapter<SettingRemindAdapter.ViewHolder>() {

    val timeList = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_setting_remind, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = timeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        timeList[position].let {
            holder.view.tv_time.text = it
            holder.view.btn_remove.setOnClickListener {
                timeList.removeAt(position)
                notifyDataSetChanged()
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}