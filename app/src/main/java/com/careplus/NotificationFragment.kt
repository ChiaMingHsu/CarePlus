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
import java.util.*


/**
 * A placeholder fragment containing a simple view.
 */
class NotificationFragment : Fragment() {

    val messageAdapter = MessageAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val database = FirebaseDatabase.getInstance()
        val messagesRef = database.getReference("messages")

        // TODO: remove while it is unused
//        // Write three mock messages to the database
//        messagesRef.child(System.currentTimeMillis().toString() + "-" + UUID.randomUUID().toString()).setValue(Message("remind", "2019.10.10", "10:00 AM", "外出"))
//        messagesRef.child(System.currentTimeMillis().toString() + "-" + UUID.randomUUID().toString()).setValue(Message("remind", "2019.10.10", "11:00 AM", "回家"))
//        messagesRef.child(System.currentTimeMillis().toString() + "-" + UUID.randomUUID().toString()).setValue(Message("alarm", "2019.10.16", "11:05 AM", "跌倒"))

        messagesRef
            .orderByKey()
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children
                        .map {
                            it.getValue(Message::class.java)
                        }
                        .filterNotNull()
                        .let {
                            messageAdapter.addAll(it)
                        }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        rv_notification.layoutManager = LinearLayoutManager(context)
        rv_notification.adapter = messageAdapter
    }
}
