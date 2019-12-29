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
    var onBtnDestroyClickListener: View.OnClickListener? = null

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

        holder.view.btnIcon.apply {
            tag = position

            val resourceName = when (code) {
                "custom" -> "event_icon_color_%s".format(if (enabled) icon else "0")
                else -> "event_icon_%s_%s".format(icon, if (enabled) "active" else "inactive")
            }
            resources.getIdentifier(resourceName, "drawable", context.packageName)
                .let { resId -> setImageResource(resId) }

            onBtnEventClickListener?.let { setOnClickListener(it) }
        }

        holder.view.ivCreate.apply {
            visibility = when (code) {
                "create" -> View.VISIBLE
                else -> View.GONE
            }
        }

        holder.view.btnDestroy.apply {
            tag = position
            visibility = when (code) {
                "custom" -> View.VISIBLE
                else -> View.GONE
            }
            onBtnDestroyClickListener?.let { setOnClickListener(it) }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}