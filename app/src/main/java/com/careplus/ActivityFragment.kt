package com.careplus

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewDisplayable
import com.alamkanak.weekview.WeekViewEvent
import com.careplus.model.Activity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.fragment_activity.*
import java.util.*


class ActivityFragment : Fragment() {

    val activities = arrayListOf<Activity>()

    data class Event(
        val id: Long,
        val title: String,
        val startTime: Calendar,
        val endTime: Calendar,
        val location: String,
        val color: Int,
        val isAllDay: Boolean,
        val isCanceled: Boolean
    ) : WeekViewDisplayable<Event> {

        companion object {
            fun create(index: Int, activity: Activity): Event {
                val (year, month, dayOfMonth) = activity.date.split("-").map { it.toInt() }
                val startTime = activity.start_time.split(":")
                    .map { it.toInt() }
                    .let { (hour, minute, second) ->
                        Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month - 1)  // note that Calendar.Month is 0-base index
                            set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                            set(Calendar.SECOND, second)
                            set(Calendar.MILLISECOND, 0)
                        }
                    }
                val endTime = activity.end_time.split(":")
                    .map { it.toInt() }
                    .let { (hour, minute, second) ->
                        Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month - 1)  // note that Calendar.Month is 0-base index
                            set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                            set(Calendar.SECOND, second)
                            set(Calendar.MILLISECOND, 0)
                        }
                    }
                val title = activity.region
                return Event(index.toLong(), title, startTime, endTime, "", Color.RED, isAllDay = false, isCanceled = false)
            }
        }

        override fun toWeekViewEvent(): WeekViewEvent<Event> {
            val style = WeekViewEvent.Style.Builder()
                .setBackgroundColor(color)
                .setTextStrikeThrough(isCanceled)
                .build()

            return WeekViewEvent.Builder<Event>(this)
                .setId(id)
                .setTitle(title)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setLocation(location)
                .setAllDay(isAllDay)
                .setStyle(style)
                .build()
        }

    }

    private val weekView: WeekView<Event> by lazy {
        requireActivity().findViewById<WeekView<Event>>(R.id.weekView)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupDB()
    }

    private fun setupView() {
        btnCalendar.setOnClickListener {
            val now = Calendar.getInstance()
            val dialog = DatePickerDialog.newInstance(
                { _, year, monthOfYear, dayOfMonth ->
                    weekView.goToDate(Calendar.getInstance().apply {
                        set(year, monthOfYear, dayOfMonth)
                    })
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )

            activities
                .groupBy { it.date }
                .keys
                .map { date ->
                    val (year, month, dayOfMonth) = date.split("-").map { it.toInt() }
                    return@map Calendar.getInstance().apply {
                        set(year, month - 1, dayOfMonth)
                    }
                }
                .let { calendars ->
                    dialog.highlightedDays = calendars.toTypedArray()
                }

            dialog.show(childFragmentManager, "DatePickerDialog")
        }
    }

    private fun setupDB() {
        FirebaseDatabase.getInstance().getReference("activities").child(App.user.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children
                        .map { it.getValue(Activity::class.java)?.apply { id = it.key!! } }
                        .filterNotNull()
                        .let {
                            activities.addAll(it)
                        }

                    activities
                        .mapIndexed { index, activity -> Event.create(index, activity) }
                        .let {events ->
                            val displayableEvents = mutableListOf<WeekViewDisplayable<Event>>()
                            displayableEvents.addAll(events)
                            weekView.submit(displayableEvents)
                        }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

}
