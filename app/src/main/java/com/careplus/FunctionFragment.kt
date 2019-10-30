package com.careplus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.careplus.adapters.FunctionFragmentPagerAdapter
import com.careplus.model.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_function.*
import java.util.*


class FunctionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_function, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupDB()
        (activity as HomeActivity).notifyPageEntered("alarm")
    }

    private fun setupView() {
        FunctionFragmentPagerAdapter(childFragmentManager)
            .apply {
                fragments.addAll(arrayOf(
                    AlarmFragment(),
                    RemindFragment()
                ))
            }
            .let { viewPager.adapter = it }
            .run { indicator.setViewPager(viewPager) }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        ivIndicatorBg.setImageResource(R.drawable.function_indicator_alarm_bg)
                        ivIndicatorAlarm.setImageResource(R.drawable.function_indicator_alarm_active)
                        ivIndicatorRemind.setImageResource(R.drawable.function_indicator_remind_inactive)
                    }
                    1 -> {
                        ivIndicatorBg.setImageResource(R.drawable.function_indicator_remind_bg)
                        ivIndicatorAlarm.setImageResource(R.drawable.function_indicator_alarm_inactive)
                        ivIndicatorRemind.setImageResource(R.drawable.function_indicator_remind_active)
                        (activity as HomeActivity).notifyPageEntered("remind")
                    }
                }
            }
        })
    }

    private fun setupDB() {
        layoutProgress?.visibility = View.VISIBLE

        // Initialize default events if no events was found
        FirebaseDatabase.getInstance().getReference("events").orderByKey().equalTo(App.user.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.children.count() == 0) {
                        FirebaseDatabase.getInstance().getReference("events").child(App.user.id)
                            .apply {
                                val eventId = "%d-%s".format(System.currentTimeMillis() + 0, UUID.randomUUID().toString())
                                child(eventId).setValue(Event(eventId, "falldown","跌倒", "alarm", "falldown", "elapsed", "00:05"))
                            }
                            .apply {
                                val eventId = "%d-%s".format(System.currentTimeMillis() + 1, UUID.randomUUID().toString())
                                child(eventId).setValue(Event(eventId, "toilet", "廁所", "alarm", "toilet", "elapsed", "00:05"))
                            }
                            .apply {
                                val eventId = "%d-%s".format(System.currentTimeMillis() + 2, UUID.randomUUID().toString())
                                child(eventId).setValue(Event(eventId, "outdoor", "出門", "alarm", "outdoor", "elapsed", "00:05"))
                            }
                            .apply {
                                val eventId = "%d-%s".format(System.currentTimeMillis() + 3, UUID.randomUUID().toString())
                                child(eventId).setValue(Event(eventId, "room", "房間", "alarm", "room", "deadline", "08:00"))
                            }
                            .apply {
                                val eventId = "%d-%s".format(System.currentTimeMillis() + 3, UUID.randomUUID().toString())
                                child(eventId).setValue(Event(eventId, "medicine", "吃藥", "remind", "medicine", "schedule", "[09:00,12:00,18:00]"))
                            }
                            .apply {
                                val eventId = "%d-%s".format(System.currentTimeMillis() + 3, UUID.randomUUID().toString())
                                child(eventId).setValue(Event(eventId, "goout", "外出", "remind", "goout", "schedule", "[09:00,12:00,18:00]"))
                            }
                            .apply {
                                val eventId = "%d-%s".format(System.currentTimeMillis() + 3, UUID.randomUUID().toString())
                                child(eventId).setValue(Event(eventId, "exercise", "運動", "remind", "exercise", "schedule", "[09:00,12:00,18:00]"))
                            }
                    }

                    layoutProgress?.visibility = View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

}
