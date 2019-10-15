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
    val onBtnEventClickListener: View.OnClickListener? = null
    val onBtnHamburgerClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.btnEvent.apply {
            val resId = resources.getIdentifier("icon_%s_active".format(events[position].icon), "drawable", context.packageName)
            setImageResource(resId)
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}