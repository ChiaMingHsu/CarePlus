package com.careplus

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.careplus.adapters.MessageGroupAdapter
import com.careplus.adapters.WeekdayAdapter
import com.careplus.model.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_notification.layoutProgress
import kotlinx.android.synthetic.main.fragment_remind.*
import java.util.*


class NotificationFragment : Fragment() {

    val weekdayAdapter = WeekdayAdapter()
    val messageGroupAdapter = MessageGroupAdapter()
    var messagesValueEventListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        (activity as HomeActivity).notifyPageEntered("notification")
    }

    private fun setupView() {
        App.user.avatarUrl?.let { Picasso.get().load(it).into(ivAvatar) }

        rvWeekday.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = weekdayAdapter

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    val layoutParams = (view.layoutParams as RecyclerView.LayoutParams)
                    val adapterPosition = layoutParams.viewAdapterPosition
                    val layoutManager = parent.layoutManager as LinearLayoutManager
                    val leftPadding = 300
                    if (adapterPosition == 0)
                        outRect.left = leftPadding
                    else if (adapterPosition == layoutManager.itemCount - 1)
                        outRect.right = parent.width - leftPadding - layoutParams.width
                }
            })

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                var offsetX = 0

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    offsetX += dx
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        recyclerView.getChildAt(0)?.width?.let { childWidth ->
                            val totalWidth = childWidth * messageGroupAdapter.itemCount
                            val screenWidth = width
                            val targetX = totalWidth - screenWidth / 2 + offsetX
                            val position = targetX / childWidth
                            val lastPosition = weekdayAdapter.weekdays.indexOfFirst { it.highlighted }

                            if (lastPosition == position)
                                return

                            rvWeekday?.smoothScrollToPosition(position)

//                            weekdayAdapter.weekdays[position].highlighted = true
//                            weekdayAdapter.notifyItemChanged(position)
//
//                            weekdayAdapter.weekdays[lastPosition].highlighted = false
//                            weekdayAdapter.notifyItemChanged(lastPosition)
                        }
                    }
                }
            })

        }

        rvMessageGroup.apply {
            layoutManager = object : LinearLayoutManager(context, HORIZONTAL, false) {
                override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                    lp?.width = (width * 0.8).toInt()
                    return super.checkLayoutParams(lp)
                }
            }
            adapter = messageGroupAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                var offsetX = 0

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    offsetX += dx
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        recyclerView.getChildAt(0)?.width?.let { childWidth ->
                            val totalWidth = childWidth * messageGroupAdapter.itemCount
                            val screenWidth = width
                            val targetX = totalWidth - screenWidth / 2 + offsetX
                            val position = targetX / childWidth
                            val lastPosition = weekdayAdapter.weekdays.indexOfFirst { it.highlighted }

                            if (lastPosition == position)
                                return

                            rvWeekday?.smoothScrollToPosition(position)

                            weekdayAdapter.weekdays[position].highlighted = true
                            weekdayAdapter.notifyItemChanged(position)

                            weekdayAdapter.weekdays[lastPosition].highlighted = false
                            weekdayAdapter.notifyItemChanged(lastPosition)
                        }
                    }
                }
            })
        }

        messageGroupAdapter.onMessageAdapterBtnPlayClickListener = View.OnClickListener { view ->
            val message = view.tag as Message
            fragmentManager?.run {
                beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_to_bottom)
                    .replace(R.id.layoutFragmentPlaceholder, PlaybackFragment(message))
                    .addToBackStack(this@NotificationFragment::class.java.simpleName)
                    .commit()
            }
        }

        btnActivity.setOnClickListener {
            fragmentManager?.run {
                beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_from_top, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_to_top)
                    .replace(R.id.layoutFragmentPlaceholder, ActivityFragment())
                    .addToBackStack(this@NotificationFragment::class.java.simpleName)
                    .commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupDB()
    }

    private fun setupDB() {
        layoutProgress?.visibility = View.VISIBLE

        messagesValueEventListener = FirebaseDatabase.getInstance().getReference("messages").child(App.user.id)
            .orderByKey()
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children
                        .mapNotNull { it.getValue(Message::class.java)?.apply { id = it.key!! } }
                        .let { messages ->
                            val messageGroups = messages
                                .map { message ->
                                    val calendar = Calendar.getInstance().apply {
                                        timeInMillis = message.createdAt
                                        set(Calendar.HOUR_OF_DAY, 0)
                                        set(Calendar.MINUTE, 0)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    return@map calendar to message
                                }
                                .groupBy({ it.first }, {it.second})
                                .map { MessageGroupAdapter.MessageGroup(it.key, it.value) }

                            messageGroupAdapter.messageGroups.clear()
                            messageGroupAdapter.messageGroups.addAll(messageGroups)
                            messageGroupAdapter.notifyDataSetChanged()

                            val weekdays = messageGroups
                                .map { messageGroup ->
                                    WeekdayAdapter.Weekday(
                                        messageGroup.calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()),
                                        false
                                    )
                                }
                            weekdayAdapter.weekdays.clear()
                            weekdayAdapter.weekdays.addAll(weekdays)
                            weekdayAdapter.notifyDataSetChanged()

                            messageGroupAdapter.messageGroups.count()
                                .takeIf { it > 0 }
                                ?.let { count ->
                                    rvWeekday.apply {
                                        weekdayAdapter.weekdays[count - 1].highlighted = true
                                        weekdayAdapter.notifyItemChanged(count - 1)
                                        scrollToPosition(count - 1)
                                    }
                                    rvMessageGroup.apply {
                                        scrollToPosition(count - 1)
                                    }
                                }
                        }

                    layoutProgress?.visibility = View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    override fun onPause() {
        super.onPause()
        messagesValueEventListener?.let { FirebaseDatabase.getInstance().getReference("messages").child(App.user.id).removeEventListener(it) }
    }
}
