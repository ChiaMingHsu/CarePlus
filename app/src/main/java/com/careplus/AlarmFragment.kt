package com.careplus


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.careplus.adapters.EventAdapter
import com.careplus.model.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_alarm.*
import kotlinx.android.synthetic.main.fragment_alarm.layoutProgress
import kotlinx.android.synthetic.main.fragment_function.*
import java.util.*


class AlarmFragment : Fragment() {

    val eventAdapter = EventAdapter()
    var eventsValueEventListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        rvAlarm.apply {
            layoutManager = GridLayoutManager(context, 2) as RecyclerView.LayoutManager
            adapter = eventAdapter
        }

        eventAdapter.onBtnEventClickListener = View.OnClickListener { view ->
            layoutProgress?.visibility = View.VISIBLE

            val position = view.tag as Int
            val event = eventAdapter.events[position].apply { enabled = !enabled }
            FirebaseDatabase.getInstance().getReference("events").child(App.user.id).child(event.id).setValue(event)
        }
    }

    override fun onResume() {
        super.onResume()
        setupDB()
    }

    private fun setupDB() {
        layoutProgress?.visibility = View.VISIBLE

        eventsValueEventListener = FirebaseDatabase.getInstance().getReference("events").child(App.user.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children
                        .map { it.getValue(Event::class.java)?.apply { id = it.key!! } }
                        .filterNotNull()
                        .let { events ->
                            eventAdapter.events.clear()
                            eventAdapter.events.addAll(events)
                            eventAdapter.notifyDataSetChanged()
                        }

                    layoutProgress?.visibility = View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    dataSnapshot.children
//                        .forEach {
//                            when (it.key) {
//                                "alarm_fall_down" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_alarm_fall_down?.isChecked = isChecked }
//                                "alarm_fall_down_minute" -> {
//                                    it.getValue(Int::class.java)?.let { alarmFallDownMinute = it }
//                                }
//                                "alarm_stuck_toilet" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_alarm_stuck_toilet?.isChecked = isChecked }
//                                "alarm_stuck_toilet_minute" -> {
//                                    it.getValue(Int::class.java)?.let { alarmStuckToiletMinute = it }
//                                }
//                                "alarm_stuck_room" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_alarm_stuck_room?.isChecked = isChecked }
//                                "alarm_stuck_room_minute" -> {
//                                    it.getValue(Int::class.java)?.let { alarmStuckRoomMinute = it }
//                                }
//                                "alarm_stuck_outdoor" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_alarm_stuck_outdoor?.isChecked = isChecked }
//                                "alarm_stuck_outdoor_minute" -> {
//                                    it.getValue(Int::class.java)?.let { alarmStuckOutdoorMinute = it }
//                                }
//                                "remind_exercise" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_remind_exercise?.isChecked = isChecked }
//                                "remind_exercise_times" -> {
//                                    it.children.forEach {
//                                        it.getValue(String::class.java)?.let { remindExerciseTimeAdapter.times.add(it) }
//                                    }
//                                    remindExerciseTimeAdapter.notifyDataSetChanged()
//                                }
//                                "remind_go_out" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_remind_go_out?.isChecked = isChecked }
//                                "remind_go_out_times" -> {
//                                    it.children.forEach {
//                                        it.getValue(String::class.java)?.let { remindGoOutTimeAdapter.times.add(it) }
//                                    }
//                                    remindGoOutTimeAdapter.notifyDataSetChanged()
//                                }
//                                "remind_take_medicine" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_remind_take_medicine?.isChecked = isChecked }
//                                "remind_take_medicine_times" -> {
//                                    it.children.forEach {
//                                        it.getValue(String::class.java)?.let { remindTakeMedicineTimeAdapter.times.add(it) }
//                                    }
//                                    remindTakeMedicineTimeAdapter.notifyDataSetChanged()
//                                }
//                            }
//                        }
//
//                    pbLoading?.visibility = View.GONE
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {}
//            })
    }

    override fun onPause() {
        super.onPause()
        eventsValueEventListener?.let { FirebaseDatabase.getInstance().getReference("events").child(App.user.id).removeEventListener(it) }
    }
}
