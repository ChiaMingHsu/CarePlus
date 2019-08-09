package com.careplus

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.careplus.adapters.SettingRemindAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dialog_setting_go_out.view.*
import kotlinx.android.synthetic.main.dialog_setting_stuck_toilet.view.*
import kotlinx.android.synthetic.main.dialog_setting_stuck_toilet.view.btn_ok
import kotlinx.android.synthetic.main.fragment_setting.*
import java.util.*


class SettingFragment : Fragment() {

    val settingRemindAdapter = SettingRemindAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupDB()
    }

    private fun setupView() {
        /**
         * Alarm
         */
        btn_alarm.setOnClickListener {
            layout_alarm.visibility = View.VISIBLE
            layout_remind.visibility = View.GONE
        }
        btn_alarm_fall_down_apply.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("settings").child(App.user.id!!).child("alarm_fall_down").setValue(sw_alarm_fall_down.isChecked)
        }
        btn_alarm_stuck_toilet_apply.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("settings").child(App.user.id!!).child("alarm_stuck_toilet").setValue(sw_alarm_stuck_toilet.isChecked)

            if (sw_alarm_stuck_toilet.isChecked) {
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_setting_stuck_toilet, layout_root, false)
                val dialog = AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create()
                    .apply {
                        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    }
                dialog.show()
                dialogView.btn_ok.setOnClickListener {
                    dialogView.edt_stuck_toilet_minutes.text.toString().toIntOrNull()?.let { minutes ->
                        FirebaseDatabase.getInstance().getReference("settings").child(App.user.id!!).child("alarm_stuck_toilet_minutes").setValue(minutes)
                        dialog.dismiss()
                    }
                }
            }
        }
        btn_alarm_stuck_room_apply.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("settings").child(App.user.id!!).child("alarm_stuck_room").setValue(sw_alarm_stuck_room.isChecked)
        }
        btn_alarm_stuck_outdoor_apply.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("settings").child(App.user.id!!).child("alarm_stuck_outdoor").setValue(sw_alarm_stuck_outdoor.isChecked)
        }

        /**
         * Remind
         */
        btn_remind.setOnClickListener {
            layout_alarm.visibility = View.GONE
            layout_remind.visibility = View.VISIBLE
        }
        btn_remind_exercise_apply.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("settings").child(App.user.id!!).child("remind_exercise").setValue(sw_remind_exercise.isChecked)
        }
        btn_remind_go_out_apply.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("settings").child(App.user.id!!).child("remind_go_out").setValue(sw_remind_go_out.isChecked)

            if (sw_remind_go_out.isChecked) {
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_setting_go_out, layout_root, false)
                val dialog = AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create()
                    .apply {
                        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    }
                dialog.show()
                dialogView.rv_remind.run {
                    layoutManager = LinearLayoutManager(context)
                    adapter = settingRemindAdapter
                }
                dialogView.btn_add.setOnClickListener {
                    val calendar = Calendar.getInstance()
                    TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        val ap = if (hourOfDay < 12) "AM" else "PM"
                        val hour = (if (hourOfDay < 12) hourOfDay else hourOfDay - 12).run { if (this == 0) 12 else this }
                        settingRemindAdapter.timeList.add("%02d:%02d %s".format(hour, minute, ap))
                        settingRemindAdapter.notifyDataSetChanged()
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
                }
                dialogView.btn_ok.setOnClickListener {
                    FirebaseDatabase.getInstance().getReference("settings").child(App.user.id!!).child("remind_go_out_times")
                        .setValue(settingRemindAdapter.timeList.map { it })
                    dialog.dismiss()
                }
            }
        }
        btn_remind_take_medicine_apply.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("settings").child(App.user.id!!).child("remind_take_medicine").setValue(sw_remind_take_medicine.isChecked)
        }
    }

    private fun setupDB() {
        pb_loading?.visibility = View.VISIBLE

        FirebaseDatabase.getInstance().getReference("settings").child(App.user.id!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children
                        .forEach {
                            when (it.key) {
                                "alarm_fall_down" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_alarm_fall_down?.isChecked = isChecked }
                                "alarm_stuck_toilet" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_alarm_stuck_toilet?.isChecked = isChecked }
                                "alarm_stuck_room" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_alarm_stuck_room?.isChecked = isChecked }
                                "alarm_stuck_outdoor" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_alarm_stuck_outdoor?.isChecked = isChecked }
                                "remind_exercise" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_remind_exercise?.isChecked = isChecked }
                                "remind_go_out" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_remind_go_out?.isChecked = isChecked }
                                "remind_go_out_times" -> {
                                    it.children.forEach {
                                        it.getValue(String::class.java)?.let { settingRemindAdapter.timeList.add(it) }
                                    }
                                    settingRemindAdapter.notifyDataSetChanged()
                                }
                                "remind_take_medicine" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_remind_take_medicine?.isChecked = isChecked }
                            }
                        }

                    pb_loading?.visibility = View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }
}
