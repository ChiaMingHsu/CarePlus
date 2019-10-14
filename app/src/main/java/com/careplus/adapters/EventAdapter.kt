package com.careplus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.careplus.R
import kotlinx.android.synthetic.main.item_event.view.*

class EventAdapter : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    var context: Context? = null
    val resIds = arrayListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        context = null
    }

    override fun getItemCount(): Int = resIds.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.btnEvent.apply {
            setImageResource(resIds[position])
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}