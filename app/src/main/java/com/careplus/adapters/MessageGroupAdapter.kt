package com.careplus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.careplus.R
import com.careplus.model.Message
import kotlinx.android.synthetic.main.item_message_group.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageGroupAdapter : RecyclerView.Adapter<MessageGroupAdapter.ViewHolder>() {

    data class MessageGroup (
        val calendar: Calendar,
        val messages: List<Message>
    )

    val messageGroups = ArrayList<MessageGroup>()
    var onMessageAdapterBtnPlayClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_group, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = messageGroups.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        messageGroups[position].let { messageGroup ->
            holder.view.tvDay.text = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date(messageGroup.calendar.timeInMillis))
            holder.view.rvMessage.apply {
                layoutManager = LinearLayoutManager(context).apply { orientation = RecyclerView.VERTICAL }
                adapter = MessageAdapter().apply {
                    onBtnPlayClickListener = onMessageAdapterBtnPlayClickListener

                    messages.clear()
                    messages.addAll(messageGroups[position].messages)
                    notifyDataSetChanged()

                    messages.count()
                        .takeIf { it > 0 }
                        ?.let { count -> rvMessage.scrollToPosition(count - 1) }
                }
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}