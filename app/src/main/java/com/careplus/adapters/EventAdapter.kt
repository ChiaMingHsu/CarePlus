package com.careplus.adapters

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
        val activeness = if (event.enabled) "active" else "inactive"

        holder.view.btnEvent.apply {
            resources
                .getIdentifier("icon_%s_%s".format(icon, activeness), "drawable", context.packageName)
                .let { resId -> setImageResource(resId) }

            tag = position
            onBtnEventClickListener?.run { setOnClickListener(this) }
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