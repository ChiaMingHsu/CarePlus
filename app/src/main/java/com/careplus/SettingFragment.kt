package com.careplus

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dialog_setting_stuck_toilet.*
import kotlinx.android.synthetic.main.dialog_setting_stuck_toilet.view.*
import kotlinx.android.synthetic.main.fragment_setting.*

/**
 * A placeholder fragment containing a simple view.
 */
class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()

        FirebaseDatabase.getInstance().getReference("setting")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children
                        .forEach {
                            when (it.key) {
                                "alarm_fall_down" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_alarm_fall_down.isChecked = isChecked }
                                "alarm_stuck_toilet" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_alarm_stuck_toilet.isChecked = isChecked }
                                "alarm_stuck_room" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_alarm_stuck_room.isChecked = isChecked }
                                "alarm_stuck_outdoor" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_alarm_stuck_outdoor.isChecked = isChecked }
                                "remind_exercise" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_remind_exercise.isChecked = isChecked }
                                "remind_go_out" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_remind_go_out.isChecked = isChecked }
                                "remind_take_medicine" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_remind_take_medicine.isChecked = isChecked }
                            }
                        }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun setupView() {
        btn_alarm.setOnClickListener {
            layout_alarm.visibility = View.VISIBLE
            layout_remind.visibility = View.GONE
        }
        btn_alarm_fall_down_apply.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("setting").child("alarm_fall_down").setValue(sw_alarm_fall_down.isChecked)
        }
        btn_alarm_stuck_toilet_apply.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("setting").child("alarm_stuck_toilet").setValue(sw_alarm_stuck_toilet.isChecked)

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
                        FirebaseDatabase.getInstance().getReference("setting").child("alarm_stuck_toilet_minutes")
                            .setValue(minutes)
                        dialog.dismiss()
                    }
                }
            }
        }
        btn_alarm_stuck_room_apply.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("setting").child("alarm_stuck_room").setValue(sw_alarm_stuck_room.isChecked)
        }
        btn_alarm_stuck_outdoor_apply.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("setting").child("alarm_stuck_outdoor").setValue(sw_alarm_stuck_outdoor.isChecked)
        }

        btn_remind.setOnClickListener {
            layout_alarm.visibility = View.GONE
            layout_remind.visibility = View.VISIBLE
        }
        btn_remind_exercise_apply.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("setting").child("remind_exercise").setValue(sw_remind_exercise.isChecked)
        }
        btn_remind_go_out_apply.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("setting").child("remind_go_out").setValue(sw_remind_go_out.isChecked)

            if (sw_remind_go_out.isChecked) {
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_setting_go_out, layout_root, false)
                val dialog = AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create()
                    .apply {
                        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    }
                dialog.show()
                dialogView.btn_ok.setOnClickListener {
//                    dialogView.edt_stuck_toilet_minutes.text.toString().toIntOrNull()?.let { minutes ->
//                        FirebaseDatabase.getInstance().getReference("setting").child("alarm_stuck_toilet_minutes")
//                            .setValue(minutes)
//                        dialog.dismiss()
//                    }
                    dialog.dismiss()  // TODO
                }
            }
        }
        btn_remind_take_medicine_apply.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("setting").child("remind_take_medicine").setValue(sw_remind_take_medicine.isChecked)
        }
    }
}
