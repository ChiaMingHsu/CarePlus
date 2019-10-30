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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.careplus.adapters.EventAdapter
import com.careplus.adapters.TimeAdapter
import com.careplus.model.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dialog_config_remind_create.view.*
import kotlinx.android.synthetic.main.dialog_config_remind_schedule.view.*
import kotlinx.android.synthetic.main.dialog_config_remind_schedule.view.btnOk
import kotlinx.android.synthetic.main.dialog_remind_remove_confirm.view.*
import kotlinx.android.synthetic.main.fragment_remind.*
import java.util.*


class RemindFragment : Fragment() {

    val eventAdapter = EventAdapter()
    var eventsValueEventListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_remind, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        (activity as HomeActivity).notifyPageEntered("remind")
    }

    private fun setupView() {
        rvEvent.apply {
            layoutManager = GridLayoutManager(context, 2) as RecyclerView.LayoutManager
            adapter = eventAdapter
        }

        eventAdapter.onBtnEventClickListener = View.OnClickListener { view ->
            val position = view.tag as Int
            val event = eventAdapter.events[position]

            if (event.code == "create") {
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_config_remind_create, layout_root, false)
                val dialog = AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create()
                    .apply {
                        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    }

                with(dialogView) {
                    arrayOf(ivIconColorRed, ivIconColorOrange, ivIconColorYellow,
                            ivIconColorGreen, ivIconColorBlue, ivIconColorPurple,
                            ivIconColorTiffany, ivIconColorPink, ivIconColorCeleste
                        ).forEach { imageView ->
                            imageView.setOnClickListener { dialogView.tag = imageView.tag }
                        }
                }
                dialogView.btnOk.setOnClickListener {
                    val eventId = "%d-%s".format(System.currentTimeMillis(), UUID.randomUUID().toString())
                    val name = dialogView.edtName.text.toString()
                    val color = dialogView.tag as String
                    val customEvent = Event(eventId, "custom", name, "remind", "color_%s".format(color), "schedule", "[09:00,12:00,18:00]", true)
                    FirebaseDatabase.getInstance().getReference("events").child(App.user.id).child(eventId).setValue(customEvent)
                    dialog.dismiss()
                }
                dialog.show()
            } else {
                layoutProgress?.visibility = View.VISIBLE
                event.enabled = event.enabled.not()
                FirebaseDatabase.getInstance().getReference("events").child(App.user.id).child(event.id).setValue(event)
            }
        }

        eventAdapter.onBtnConfigClickListener = View.OnClickListener { view ->
            // Button config for `create` event is guaranteed that unable to reach here

            val position = view.tag as Int
            val event = eventAdapter.events[position]

            if (event.enabled) {
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_config_remind_schedule, layout_root, false)
                val dialog = AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create()
                    .apply {
                        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    }
                val timeAdapter = TimeAdapter()
                    .apply {
                        times.addAll(event.value.trim('[', ']').split(","))
                    }

                dialogView.tvName.text = event.name
                dialogView.rvTime.apply {
                    layoutManager = LinearLayoutManager(context)
                    dialogView.rvTime.adapter = timeAdapter
                }
                dialogView.tvControlTime.setOnClickListener {
                    val calendar = Calendar.getInstance()
                    TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        dialogView.tvControlTime.text = "%02d:%02d".format(hourOfDay, minute)
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
                }
                dialogView.btnAdd.setOnClickListener {
                    timeAdapter.times.add(dialogView.tvControlTime.text.toString())
                    timeAdapter.notifyDataSetChanged()
                }
                dialogView.btnOk.setOnClickListener {
                    event.value = timeAdapter.times.joinToString(",", "[", "]")
                    FirebaseDatabase.getInstance().getReference("events").child(App.user.id).child(event.id).setValue(event)
                    dialog.dismiss()
                }
                dialogView.btnMore.setOnClickListener {
                    val confirmDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_remind_remove_confirm, layout_root, false)
                    val confirmDialog = AlertDialog.Builder(context)
                        .setView(confirmDialogView)
                        .create()
                        .apply {
                            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        }

                    confirmDialogView.btnYes.setOnClickListener {
                        FirebaseDatabase.getInstance().getReference("events").child(App.user.id).child(event.id).removeValue()
                        confirmDialog.dismiss()
                        dialog.dismiss()
                    }
                    confirmDialogView.btnNo.setOnClickListener {
                        confirmDialog.dismiss()
                    }
                    confirmDialog.show()
                }
                dialog.show()
            }
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
                        .filter { it.type == "remind" }
                        .let { events ->
                            eventAdapter.events.clear()
                            eventAdapter.events.add(Event("", "create", "新增", "remind", "create", "", "", true))
                            eventAdapter.events.addAll(events)
                            eventAdapter.notifyDataSetChanged()
                        }

                    layoutProgress?.visibility = View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    override fun onPause() {
        super.onPause()
        eventsValueEventListener?.let { FirebaseDatabase.getInstance().getReference("events").child(App.user.id).removeEventListener(it) }
    }

}
