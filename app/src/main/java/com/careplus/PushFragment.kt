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
import kotlinx.android.synthetic.main.fragment_push.*


class PushFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_push, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupDB()
    }

    private fun setupView() {
        swPush.setOnCheckedChangeListener { _, isChecked ->
            FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("push").setValue(isChecked)
        }
    }

    private fun setupDB() {
        FirebaseDatabase.getInstance().getReference("settings").child(App.user.id).child("push")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    swPush.isChecked = dataSnapshot.value as Boolean
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }
}
