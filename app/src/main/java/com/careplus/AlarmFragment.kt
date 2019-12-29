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
import com.careplus.adapters.EventAdapter
import com.careplus.model.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_alarm.*


class AlarmFragment : Fragment() {

    val eventAdapter = EventAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alarm, container, false)
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

            event.enabled = event.enabled.not()

            FirebaseDatabase.getInstance().getReference("events").child(App.user.id).child(event.id).setValue(event)
            eventAdapter.notifyItemChanged(position)

            layoutProgress?.visibility = View.GONE
        }

        layoutBody.visibility = View.GONE

        wvValue.data = (1..99).toList()
        wvUnit.data = listOf("sec", "min")

        btnConfirm.setOnClickListener { view ->
            layoutProgress?.visibility = View.VISIBLE

            val index = view.tag as Int
            val event = eventAdapter.events[index]

            when (wvUnit.selectedItemData as String) {
                "sec" -> {
                    val minute = 0
                    val second = wvValue.selectedItemData as Int
                    event.value = "%02d:%02d".format(minute, second)
                }
                "min" -> {
                    val minute = wvValue.selectedItemData as Int
                    val second = 0
                    event.value = "%02d:%02d".format(minute, second)
                }
            }

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
                        .filter { it.type == "alarm" }
                        .let { events ->
                            eventAdapter.events.clear()
                            eventAdapter.events.addAll(events)
                            eventAdapter.notifyDataSetChanged()

                            if (events.count() > 0) {
                                layoutBody?.visibility = View.VISIBLE
                                updateBody(0)
                            } else
                                layoutBody?.visibility = View.GONE
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
        event.value.split(":")
            .let { (minute, second) -> Pair(minute.toInt(), second.toInt()) }
            .let { (minute, second) ->
                // TODO: for now, `event.value` is in format `mm:ss`
                //       1. means `mm` minutes OR `ss` seconds
                //       2. `mm` and `ss` must not equal to 0 at the same time
                //       3. `mm` and `ss` must not unequal to 0 at the same time (if so, use mm first)
                //       4. both mm and ss are in range of [1, 99]
                //       Maybe change to format `%02d {min, sec}` later
                if (minute > 0) {
                    wvValue?.selectedItemPosition = minute - 1
                    wvUnit?.selectedItemPosition = 1  // min
                }
                else if (second > 0) {
                    wvValue?.selectedItemPosition = second - 1
                    wvUnit?.selectedItemPosition = 0  // sec
                }
            }
    }

}
