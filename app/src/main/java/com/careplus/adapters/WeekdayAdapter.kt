package com.careplus.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.careplus.R
import kotlinx.android.synthetic.main.item_weekday.view.*

class WeekdayAdapter : RecyclerView.Adapter<WeekdayAdapter.ViewHolder>() {

    data class Weekday(
        val text: String?,
        var highlighted: Boolean
    )

    val weekdays = arrayListOf<Weekday>()
    var onBtnWeekdayClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weekday, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = weekdays.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        weekdays[position].let { weekday ->
            val resId = if (weekday.highlighted) R.drawable.notification_weekday_focus_bg else R.drawable.notification_weekday_normal_bg
            val textColor = if (weekday.highlighted) Color.parseColor("#ffffff") else Color.parseColor("#adadbe")
            holder.view.tvText.apply {
                text = weekday.text
                setTextColor(textColor)
            }
            holder.view.btnWeekday.apply {
                tag = position
                setImageResource(resId)
                onBtnWeekdayClickListener?.let { setOnClickListener(it) }
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}