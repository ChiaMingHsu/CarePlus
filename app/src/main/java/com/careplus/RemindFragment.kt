package com.careplus


import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.careplus.adapters.EventAdapter
import com.careplus.adapters.TimeAdapter
import com.careplus.model.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dialog_config_remind_create.view.*
import kotlinx.android.synthetic.main.dialog_config_remind_schedule.view.btnOk
import kotlinx.android.synthetic.main.fragment_remind.*
import kotlinx.android.synthetic.main.fragment_remind.btnConfirm
import kotlinx.android.synthetic.main.fragment_remind.layoutBodyGeneral
import kotlinx.android.synthetic.main.fragment_remind.layoutProgress
import kotlinx.android.synthetic.main.fragment_remind.layout_root
import kotlinx.android.synthetic.main.fragment_remind.rvEvent
import kotlinx.android.synthetic.main.fragment_remind.tvName
import java.util.*


class RemindFragment : Fragment() {

    val eventAdapter = EventAdapter()
    val timeAdapter = TimeAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_remind, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupDB()
    }

    private fun setupView() {
        rvEvent.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false) as RecyclerView.LayoutManager
            adapter = eventAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    val layoutParams = (view.layoutParams as RecyclerView.LayoutParams)
                    val adapterPosition = layoutParams.viewAdapterPosition
                    val layoutManager = parent.layoutManager as LinearLayoutManager
                    if (adapterPosition == 0)
                        outRect.left = parent.width / 2 - layoutParams.width / 2
                    else if (adapterPosition == layoutManager.itemCount - 1)
                        outRect.right = parent.width / 2 - layoutParams.width / 2
                }
            })

            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(rvEvent)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                var selectedPosition = 0

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        layoutManager
                            ?.let { layoutManager ->
                                snapHelper.findSnapView(layoutManager)
                                    ?.let { view -> layoutManager.getPosition(view) }
                                    ?.takeIf { position -> position != selectedPosition }
                                    ?.let { position ->
                                        selectedPosition = position
                                        updateBody(selectedPosition)
                                    }
                            }
                    }
                }
            })
        }

        eventAdapter.onBtnEventClickListener = View.OnClickListener { view ->
            layoutProgress?.visibility = View.VISIBLE

            val position = view.tag as Int
            val event = eventAdapter.events[position]

            if (event.code != "create") {
                event.enabled = event.enabled.not()
                FirebaseDatabase.getInstance().getReference("events").child(App.user.id).child(event.id).setValue(event)
                eventAdapter.notifyItemChanged(position)
            }

            layoutProgress?.visibility = View.GONE
        }

        eventAdapter.onBtnDestroyClickListener = View.OnClickListener { view ->
            layoutProgress?.visibility = View.VISIBLE

            val position = view.tag as Int
            val event = eventAdapter.events[position]

            FirebaseDatabase.getInstance().getReference("events").child(App.user.id).child(event.id).removeValue()
            eventAdapter.events.remove(event)
            eventAdapter.notifyDataSetChanged()

            layoutProgress?.visibility = View.GONE
        }

        /* Body Create */

        layoutBodyCreate.visibility = View.GONE

        val clearAllImages = {
            layoutColorGrid.children
                .map { it as ImageView }
                .forEach { it.setImageResource(android.R.color.transparent) }
        }

        layoutColorGrid.children
            .map { it as ImageView }
            .forEach { imageView ->
                imageView.apply {
                    setOnClickListener { view ->
                        clearAllImages()
                        setImageResource(R.drawable.remind_check)
                        btnConfirmCreate.tag = view.tag
                    }
                }
            }

        btnConfirmCreate.setOnClickListener { view ->
            val eventId = "%d-%s".format(System.currentTimeMillis(), UUID.randomUUID().toString())
            val name = edtName.text.toString()
            val colorId = view.tag as String
            val customEvent = Event(eventId, "custom", name, "remind", colorId, "schedule", "[09:00,12:00,18:00]", true)
            FirebaseDatabase.getInstance().getReference("events").child(App.user.id).child(eventId).setValue(customEvent)
            eventAdapter.events.add(customEvent)
            eventAdapter.notifyDataSetChanged()
        }

        /* Body General */

        layoutBodyGeneral.visibility = View.GONE

        rvTime.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = timeAdapter
        }

        btnAdd.setOnClickListener {
            val hour = wvHour.selectedItemData as Int
            val minute = wvMinute.selectedItemData as Int
            timeAdapter.times.add("%02d:%02d".format(hour, minute))
            timeAdapter.notifyDataSetChanged()
            rvTime.smoothScrollToPosition(timeAdapter.times.size - 1)
        }

        wvHour.data = (0..23).toList()
        wvMinute.data = (0..59).toList()

        btnConfirm.setOnClickListener { view ->
            layoutProgress?.visibility = View.VISIBLE

            val index = view.tag as Int
            val event = eventAdapter.events[index]

            event.value = timeAdapter.times.joinToString(",", "[", "]")
            FirebaseDatabase.getInstance().getReference("events").child(App.user.id).child(event.id).setValue(event)
            eventAdapter.notifyItemChanged(index)

            layoutProgress?.visibility = View.GONE
        }
    }

    private fun setupDB() {
        layoutProgress?.visibility = View.VISIBLE

        FirebaseDatabase.getInstance().getReference("events").child(App.user.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children
                        .map { it.getValue(Event::class.java)?.apply { id = it.key!! } }
                        .filterNotNull()
                        .filter { it.type == "remind" }
                        .let { events ->
                            eventAdapter.events.clear()
                            eventAdapter.events.add(Event("", "create", "Create", "remind", "create", "", "", true))
                            eventAdapter.events.addAll(events)
                            eventAdapter.notifyDataSetChanged()

                            if (events.count() > 0)
                                updateBody(0)
                        }

                    layoutProgress?.visibility = View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun updateBody(index: Int) {
        val event = eventAdapter.events[index]

        btnConfirm?.tag = index
        tvName?.text = event.name

        if (event.code == "create") {
            layoutBodyCreate?.visibility = View.VISIBLE
            layoutBodyGeneral?.visibility = View.GONE
        }
        else {
            layoutBodyCreate?.visibility = View.GONE
            layoutBodyGeneral?.visibility = View.VISIBLE

            event.value
                .run { trim('[', ']').split(",") }
                .filter { it.isNotBlank() }
                .let { times ->
                    timeAdapter.times.clear()
                    timeAdapter.times.addAll(times)
                    timeAdapter.notifyDataSetChanged()
                }
        }
    }

}
