package com.careplus


import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import kotlinx.android.synthetic.main.dialog_config_alarm_elapsed.view.*
import kotlinx.android.synthetic.main.fragment_alarm.*


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
            val event = eventAdapter.events[position]

            event.enabled = event.enabled.not()
            FirebaseDatabase.getInstance().getReference("events").child(App.user.id).child(event.id).setValue(event)
        }

        eventAdapter.onBtnConfigClickListener = View.OnClickListener { view ->
            val position = view.tag as Int
            val event = eventAdapter.events[position]

            if (event.enabled) {
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_config_alarm_elapsed, layout_root, false)
                val dialog = AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create()
                    .apply {
                        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    }

                dialogView.tvName.text = event.name
                dialogView.edtMinute.setText(event.value.split(":")[0])
                dialogView.edtSecond.setText(event.value.split(":")[1])
                dialogView.btnOk.setOnClickListener {
                    dialogView.edtMinute.text.toString().toIntOrNull()?.let { minute ->
                        dialogView.edtSecond.text.toString().toIntOrNull()?.let { second ->
                            event.value = "%02d:%02d".format(minute, second)
                            FirebaseDatabase.getInstance().getReference("events").child(App.user.id).child(event.id)
                                .setValue(event)
                            dialog.dismiss()
                        }
                    }
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
                        .let { events ->
                            eventAdapter.events.clear()
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
