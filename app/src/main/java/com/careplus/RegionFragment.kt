package com.careplus

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_region.*
import kotlinx.android.synthetic.main.fragment_region.iv_frame

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
        btn_door.setOnClickListener {
            with(iv_mask) {
                if (tag == "door") {
                    setImageDrawable(null)
                    visibility = View.GONE
                    tag = "none"
                }
                else {
                    setImageResource(R.mipmap.region_door_mask)
                    visibility = View.VISIBLE
                    tag = "door"
                }
            }
        }
        btn_toilet.setOnClickListener {
            with(iv_mask) {
                if (tag == "toilet") {
                    setImageDrawable(null)
                    visibility = View.GONE
                    tag = "none"
                }
                else {
                    setImageResource(R.mipmap.region_toilet_mask)
                    visibility = View.VISIBLE
                    tag = "toilet"
                }
            }
        }
        btn_room.setOnClickListener {
            with(iv_mask) {
                if (tag == "room") {
                    setImageDrawable(null)
                    visibility = View.GONE
                    tag = "none"
                }
                else {
                    setImageDrawable(null)
                    visibility = View.VISIBLE
                    tag = "room"
                }
            }
        }
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
        FirebaseDatabase.getInstance().getReference("frames").child(App.user.id!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.child("frame").getValue(String::class.java)?.let { base64Str ->
                        Base64.decode(base64Str, Base64.DEFAULT)
                            .run { BitmapFactory.decodeByteArray(this, 0, this.size) }
                            .run { iv_frame?.setImageBitmap(this) }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
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
