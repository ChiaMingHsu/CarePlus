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
import kotlinx.android.synthetic.main.fragment_alarm.*
import kotlinx.android.synthetic.main.fragment_function.*
import kotlinx.android.synthetic.main.fragment_function.layoutProgress
import java.util.*


class FunctionFragment : Fragment() {

//    var alarmFallDownMinute: Int = 0
//    var alarmStuckToiletMinute: Int = 0
//    var alarmStuckRoomMinute: Int = 0
//    var alarmStuckOutdoorMinute: Int = 0
//    val remindExerciseTimeAdapter = TimeAdapter()
//    val remindGoOutTimeAdapter = TimeAdapter()
//    val remindTakeMedicineTimeAdapter = TimeAdapter()

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
                                val eventId =
                                    "%d-%s".format(System.currentTimeMillis() + 0, UUID.randomUUID().toString())
                                child(eventId).setValue(Event(eventId, "跌倒", "alarm", "falldown", "elapsed", "00:05"))
                            }
                            .apply {
                                val eventId =
                                    "%d-%s".format(System.currentTimeMillis() + 1, UUID.randomUUID().toString())
                                child(eventId).setValue(Event(eventId, "廁所", "alarm", "toilet", "elapsed", "00:05"))
                            }
                            .apply {
                                val eventId =
                                    "%d-%s".format(System.currentTimeMillis() + 2, UUID.randomUUID().toString())
                                child(eventId).setValue(Event(eventId, "出門", "alarm", "outdoor", "elapsed", "00:05"))
                            }
                            .apply {
                                val eventId =
                                    "%d-%s".format(System.currentTimeMillis() + 3, UUID.randomUUID().toString())
                                child(eventId).setValue(Event(eventId, "房間", "alarm", "room", "deadline", "08:00"))
                            }
                    }

                    layoutProgress?.visibility = View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }


    //
//    private fun onBtnAlarmFallDownApplyClickListener() {
//        FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("alarm_fall_down").setValue(sw_alarm_fall_down.isChecked)
//
//        if (sw_alarm_fall_down.isChecked) {
//            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_setting_fall_down, layout_root, false)
//            val dialog = AlertDialog.Builder(context)
//                .setView(dialogView)
//                .create()
//                .apply {
//                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                }
//
//            dialogView
//                .apply {
//                    edt_minute.setText(alarmFallDownMinute.toString())
//                }
//                .apply {
//                    btn_ok.setOnClickListener {
//                        dialogView.edt_minute.text.toString().toIntOrNull()?.let { minute ->
//                            alarmFallDownMinute = minute
//                            FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("alarm_fall_down_minute").setValue(minute)
//                            dialog.dismiss()
//                        } ?: Toast.makeText(context, R.string.illegal_value, Toast.LENGTH_SHORT).show()
//                    }
//                }
//            dialog.show()
//        }
//    }
//
//    private fun onBtnAlarmStuckToiletApplyClickListener() {
//        FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("alarm_stuck_toilet").setValue(sw_alarm_stuck_toilet.isChecked)
//
//        if (sw_alarm_stuck_toilet.isChecked) {
//            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_setting_stuck_toilet, layout_root, false)
//            val dialog = AlertDialog.Builder(context)
//                .setView(dialogView)
//                .create()
//                .apply {
//                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                }
//
//            dialogView
//                .apply {
//                    edt_minute.setText(alarmStuckToiletMinute.toString())
//                }
//                .apply {
//                    btn_ok.setOnClickListener {
//                        dialogView.edt_minute.text.toString().toIntOrNull()?.let { minute ->
//                            alarmStuckToiletMinute = minute
//                            FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("alarm_stuck_toilet_minute").setValue(minute)
//                            dialog.dismiss()
//                        } ?: Toast.makeText(context, R.string.illegal_value, Toast.LENGTH_SHORT).show()
//                    }
//                }
//            dialog.show()
//        }
//    }
//
//    private fun onBtnAlarmStuckRoomApplyClickListener() {
//        FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("alarm_stuck_room").setValue(sw_alarm_stuck_room.isChecked)
//
//        if (sw_alarm_stuck_room.isChecked) {
//            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_setting_stuck_room, layout_root, false)
//            val dialog = AlertDialog.Builder(context)
//                .setView(dialogView)
//                .create()
//                .apply {
//                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                }
//
//            dialogView
//                .apply {
//                    edt_minute.setText(alarmStuckRoomMinute.toString())
//                }
//                .apply {
//                    btn_ok.setOnClickListener {
//                        dialogView.edt_minute.text.toString().toIntOrNull()?.let { minute ->
//                            alarmStuckRoomMinute = minute
//                            FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("alarm_stuck_room_minute").setValue(minute)
//                            dialog.dismiss()
//                        } ?: Toast.makeText(context, R.string.illegal_value, Toast.LENGTH_SHORT).show()
//                    }
//                }
//            dialog.show()
//        }
//    }
//
//    private fun onBtnAlarmStuckOutdoorApplyClickListener() {
//        FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("alarm_stuck_outdoor").setValue(sw_alarm_stuck_outdoor.isChecked)
//
//        if (sw_alarm_stuck_outdoor.isChecked) {
//            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_setting_stuck_outdoor, layout_root, false)
//            val dialog = AlertDialog.Builder(context)
//                .setView(dialogView)
//                .create()
//                .apply {
//                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                }
//
//            dialogView
//                .apply {
//                    edt_minute.setText(alarmStuckOutdoorMinute.toString())
//                }
//                .apply {
//                    btn_ok.setOnClickListener {
//                        dialogView.edt_minute.text.toString().toIntOrNull()?.let { minute ->
//                            alarmStuckOutdoorMinute = minute
//                            FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("alarm_stuck_outdoor_minute").setValue(minute)
//                            dialog.dismiss()
//                        } ?: Toast.makeText(context, R.string.illegal_value, Toast.LENGTH_SHORT).show()
//                    }
//                }
//            dialog.show()
//        }
//    }
//
//    private fun onBtnRemindExerciseApplyClickListener() {
//        FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("remind_exercise").setValue(sw_remind_exercise.isChecked)
//
//        if (sw_remind_exercise.isChecked) {
//            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_setting_exercise, layout_root, false)
//            val dialog = AlertDialog.Builder(context)
//                .setView(dialogView)
//                .create()
//                .apply {
//                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                }
//
//            dialogView
//                .apply {
//                    rv_remind.layoutManager = LinearLayoutManager(context)
//                    rv_remind.adapter = remindExerciseTimeAdapter
//                }
//                .apply {
//                    btn_add.setOnClickListener {
//                        val calendar = Calendar.getInstance()
//                        TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
//                            val ap = if (hourOfDay < 12) "AM" else "PM"
//                            val hour = (if (hourOfDay < 12) hourOfDay else hourOfDay - 12).run { if (this == 0) 12 else this }
//                            remindExerciseTimeAdapter.times.add("%02d:%02d %s".format(hour, minute, ap))
//                            remindExerciseTimeAdapter.notifyDataSetChanged()
//                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
//                    }
//                }
//                .apply {
//                    btn_ok.setOnClickListener {
//                        FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("remind_exercise_times").setValue(remindExerciseTimeAdapter.times)
//                        dialog.dismiss()
//                    }
//                }
//            dialog.show()
//        }
//    }
//    private fun onBtnRemindGoOutApplyClickListener() {
//        FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("remind_go_out").setValue(sw_remind_go_out.isChecked)
//
//        if (sw_remind_go_out.isChecked) {
//            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_setting_go_out, layout_root, false)
//            val dialog = AlertDialog.Builder(context)
//                .setView(dialogView)
//                .create()
//                .apply {
//                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                }
//
//            dialogView
//                .apply {
//                    rv_remind.layoutManager = LinearLayoutManager(context)
//                    rv_remind.adapter = remindGoOutTimeAdapter
//                }
//                .apply {
//                    btn_add.setOnClickListener {
//                        val calendar = Calendar.getInstance()
//                        TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
//                            val ap = if (hourOfDay < 12) "AM" else "PM"
//                            val hour = (if (hourOfDay < 12) hourOfDay else hourOfDay - 12).run { if (this == 0) 12 else this }
//                            remindGoOutTimeAdapter.times.add("%02d:%02d %s".format(hour, minute, ap))
//                            remindGoOutTimeAdapter.notifyDataSetChanged()
//                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
//                    }
//                }
//                .apply {
//                    btn_ok.setOnClickListener {
//                        FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("remind_go_out_times").setValue(remindGoOutTimeAdapter.times)
//                        dialog.dismiss()
//                    }
//                }
//            dialog.show()
//        }
//    }
//    private fun onBtnRemindTakeMedicineApplyClickListener() {
//        FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("remind_take_medicine").setValue(sw_remind_take_medicine.isChecked)
//
//        if (sw_remind_take_medicine.isChecked) {
//            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_setting_take_medicine, layout_root, false)
//            val dialog = AlertDialog.Builder(context)
//                .setView(dialogView)
//                .create()
//                .apply {
//                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                }
//
//            dialogView
//                .apply {
//                    rv_remind.layoutManager = LinearLayoutManager(context)
//                    rv_remind.adapter = remindTakeMedicineTimeAdapter
//                }
//                .apply {
//                    btn_add.setOnClickListener {
//                        val calendar = Calendar.getInstance()
//                        TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
//                            val ap = if (hourOfDay < 12) "AM" else "PM"
//                            val hour = (if (hourOfDay < 12) hourOfDay else hourOfDay - 12).run { if (this == 0) 12 else this }
//                            remindTakeMedicineTimeAdapter.times.add("%02d:%02d %s".format(hour, minute, ap))
//                            remindTakeMedicineTimeAdapter.notifyDataSetChanged()
//                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
//                    }
//                }
//                .apply {
//                    btn_ok.setOnClickListener {
//                        FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("remind_take_medicine_times").setValue(remindTakeMedicineTimeAdapter.times)
//                        dialog.dismiss()
//                    }
//                }
//            dialog.show()
//        }
//    }



}
