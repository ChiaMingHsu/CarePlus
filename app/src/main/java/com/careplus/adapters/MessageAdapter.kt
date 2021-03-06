package com.careplus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.careplus.R
import com.careplus.model.Message
import kotlinx.android.synthetic.main.item_message.view.*
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    val messages = arrayListOf<Message>()
    var onBtnPlayClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        messages[position].let { message ->
            when (Pair(message.type, message.priority)) {
                Pair("alarm", "standard") -> {
                    holder.view.ivContentBg.setImageResource(R.drawable.notification_content_alarm_standard_bg)
                    holder.view.btnPlay.setImageResource(R.drawable.notification_btn_play_alarm_standard)
                }
                Pair("alarm", "emergent") -> {
                    holder.view.ivContentBg.setImageResource(R.drawable.notification_content_alarm_emergent_bg)
                    holder.view.btnPlay.setImageResource(R.drawable.notification_btn_play_alarm_emergent)
                }
                Pair("remind", "standard") -> {
                    holder.view.ivContentBg.setImageResource(R.drawable.notification_content_remind_standard_bg)
                    holder.view.btnPlay.setImageResource(R.drawable.notification_btn_play_remind_standard)
                }
            }
            holder.view.tvControlTime.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(message.createdAt)
            holder.view.tvContent.text = message.content
            holder.view.btnPlay.apply {
                tag = message
                onBtnPlayClickListener?.run { setOnClickListener(this) }
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}