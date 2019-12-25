package com.careplus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.careplus.R
import com.careplus.model.Message
import kotlinx.android.synthetic.main.item_message.view.*
import kotlinx.android.synthetic.main.item_weekday.view.*
import java.text.SimpleDateFormat
import java.util.*

class WeekdayAdapter : RecyclerView.Adapter<WeekdayAdapter.ViewHolder>() {

    data class Weekday(
        val text: String?,
        val isToday: Boolean
    )

    val weekdays = arrayListOf<Weekday>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weekday, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = weekdays.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        weekdays[position].let { weekday ->
            holder.view.tvText.text = weekday.text
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}