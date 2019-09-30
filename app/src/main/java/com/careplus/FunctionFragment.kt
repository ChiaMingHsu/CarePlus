package com.careplus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


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

//    private fun setupView() {
//        /**
//         * Alarm
//         */
//        btn_alarm.setOnClickListener {
//            layout_alarm.visibility = View.VISIBLE
//            layout_remind.visibility = View.GONE
//        }
//        btn_alarm_fall_down_apply.setOnClickListener { onBtnAlarmFallDownApplyClickListener() }
//        btn_alarm_stuck_toilet_apply.setOnClickListener { onBtnAlarmStuckToiletApplyClickListener() }
//        btn_alarm_stuck_room_apply.setOnClickListener { onBtnAlarmStuckRoomApplyClickListener() }
//        btn_alarm_stuck_outdoor_apply.setOnClickListener { onBtnAlarmStuckOutdoorApplyClickListener() }
//
//        /**
//         * Remind
//         */
//        btn_remind.setOnClickListener {
//            layout_alarm.visibility = View.GONE
//            layout_remind.visibility = View.VISIBLE
//        }
//        btn_remind_exercise_apply.setOnClickListener { onBtnRemindExerciseApplyClickListener() }
//        btn_remind_go_out_apply.setOnClickListener { onBtnRemindGoOutApplyClickListener() }
//        btn_remind_take_medicine_apply.setOnClickListener { onBtnRemindTakeMedicineApplyClickListener() }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        setupDB()
//    }
//
//    private fun setupDB() {
//        pbLoading?.visibility = View.VISIBLE
//
//        FirebaseDatabase.getInstance().getReference("settings").child(App.user.id)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
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
//    }
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
