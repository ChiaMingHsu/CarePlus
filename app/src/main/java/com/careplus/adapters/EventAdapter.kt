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
        val code = event.code
        val type = event.type
        val icon = event.icon
        val enabled = event.enabled
        val activeness = if (enabled) "active" else "inactive"

        holder.view.ivBg.apply {
            when (event.code) {
                "create" -> {
                    resources
                        .getIdentifier("event_bg_dark", "drawable", context.packageName)
                        .let { resId -> setImageResource(resId) }
                }
                else -> {
                    resources
                        .getIdentifier("event_bg", "drawable", context.packageName)
                        .let { resId -> setImageResource(resId) }
                }
            }
        }

        holder.view.ivIcon.apply {
            when (event.code) {
                 "custom" -> {
                    resources
                        .getIdentifier("icon_%s".format(if (enabled) icon else "color_gray"), "drawable", context.packageName)
                        .let { resId -> setImageResource(resId) }
                    scaleX = 0.5f
                    scaleY = 0.5f
                }
                else -> {
                    resources
                        .getIdentifier("icon_%s_%s".format(icon, activeness), "drawable", context.packageName)
                        .let { resId -> setImageResource(resId) }
                    scaleX = 1.0f
                    scaleY = 1.0f
                }
            }

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
            if (code == "create") {
                setImageResource(android.R.color.transparent)
                setOnClickListener(null)
            } else {
                resources
                    .getIdentifier("%s_btn_hamburger_%s".format(type, activeness), "drawable", context.packageName)
                    .let { resId -> setImageResource(resId) }

                tag = position
                onBtnConfigClickListener?.run { setOnClickListener(this) }
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}