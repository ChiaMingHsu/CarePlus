package com.careplus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_region.*

/**
 * A placeholder fragment containing a simple view.
 */
class RegionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_region, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupDB()
    }

    private fun setupView() {
        sw_door.setOnCheckedChangeListener { _, isChecked ->
            FirebaseDatabase.getInstance().getReference("settings").child(App.user.id!!).child("alarm_stuck_outdoor").setValue(isChecked)
        }
        sw_toilet.setOnCheckedChangeListener { _, isChecked ->
            FirebaseDatabase.getInstance().getReference("settings").child(App.user.id!!).child("alarm_stuck_toilet").setValue(isChecked)
        }
        sw_room.setOnCheckedChangeListener { _, isChecked ->
            FirebaseDatabase.getInstance().getReference("settings").child(App.user.id!!).child("alarm_stuck_room").setValue(isChecked)
        }
    }

    private fun setupDB() {
        FirebaseDatabase.getInstance().getReference("settings").child(App.user.id!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children
                        .forEach {
                            when (it.key) {
                                "alarm_stuck_outdoor" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_door?.isChecked = isChecked }
                                "alarm_stuck_toilet" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_toilet?.isChecked = isChecked }
                                "alarm_stuck_room" -> it.getValue(Boolean::class.java)?.let { isChecked -> sw_room?.isChecked = isChecked }
                            }
                        }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }
}
