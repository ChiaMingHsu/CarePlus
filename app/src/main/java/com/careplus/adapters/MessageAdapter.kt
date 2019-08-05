package com.careplus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.careplus.R
import com.careplus.model.Message
import kotlinx.android.synthetic.main.item_message.view.*

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    val messages = arrayListOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        messages[position].let {
            when (it.type) {
                "alarm" -> holder.view.tv_content.setBackgroundResource(R.mipmap.notification_message_content_bg_alarm)
                "remind" -> holder.view.tv_content.setBackgroundResource(R.mipmap.notification_message_content_bg_remind)
            }
            holder.view.tv_date.text = it.date
            holder.view.tv_content.text = String.format("%s %s", it.time, it.content)
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}