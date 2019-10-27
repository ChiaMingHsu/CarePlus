package com.careplus.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.careplus.R
import com.careplus.model.Event
import kotlinx.android.synthetic.main.item_event.view.*

class EventAdapter : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    val events = arrayListOf<Event>()
    var onBtnEventClickListener: View.OnClickListener? = null
    var onBtnConfigClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        val type = event.type
        val icon = event.icon
        val enabled = event.enabled
        val activeness = if (enabled) "active" else "inactive"

        holder.view.ivIcon.apply {
            resources
                .getIdentifier("icon_%s_%s".format(icon, activeness), "drawable", context.packageName)
                .let { resId -> setImageResource(resId) }

            tag = position
            onBtnEventClickListener?.run { setOnClickListener(this) }
        }

        holder.view.tvName.apply {
            text = event.name
            if (enabled) {
                when (type) {
                    "alarm" -> setTextColor(Color.parseColor("#5067fb"))
                    "remind" -> setTextColor(Color.parseColor("#41a8a7"))
                }
            } else {
                setTextColor(Color.parseColor("#adadbe"))
            }
        }

        holder.view.btnConfig.apply {
            resources
                .getIdentifier("%s_btn_hamburger_%s".format(type, activeness), "drawable", context.packageName)
                .let { resId -> setImageResource(resId) }

            tag = position
            onBtnConfigClickListener?.run { setOnClickListener(this) }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}