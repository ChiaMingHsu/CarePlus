package com.careplus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.careplus.adapters.MessageAdapter
import com.careplus.model.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_notification.*


/**
 * A placeholder fragment containing a simple view.
 */
class NotificationFragment : Fragment() {

    val messageAdapter = MessageAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupDB()
    }

    private fun setupView() {
        rv_notification.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = messageAdapter
        }
    }

    private fun setupDB() {
        pb_loading?.visibility = View.VISIBLE

        FirebaseDatabase.getInstance().getReference("messages").child(App.user.id!!)
            .orderByKey()
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children
                        .map {
                            it.getValue(Message::class.java)
                        }
                        .filterNotNull()
                        .let {
                            messageAdapter.messages.clear()
                            messageAdapter.messages.addAll(it)
                            messageAdapter.notifyDataSetChanged()
                        }

                    pb_loading?.visibility = View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }
}
