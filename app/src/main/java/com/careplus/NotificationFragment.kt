package com.careplus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.careplus.adapters.MessageAdapter
import com.careplus.model.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_notification.*


class NotificationFragment : Fragment() {

    val messageAdapter = MessageAdapter()
    var messagesValueEventListener: ValueEventListener? = null

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
        rvNotification.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = messageAdapter
        }
    }

    private fun setupDB() {
        pbLoading?.visibility = View.VISIBLE

        messageAdapter.onBtnPlayClickListener = View.OnClickListener { view ->
            val position = view.tag as Int
            val message = messageAdapter.messages[position]
            val playbackId = message.playbackId

            if (playbackId.isEmpty()) {
                Toast.makeText(context, "此訊息沒有回放影像", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }


            fragmentManager?.run {
                beginTransaction()
                    .replace(R.id.layoutFragmentPlaceholder, PlaybackFragment(message, playbackId))
                    .addToBackStack(this@NotificationFragment::class.java.simpleName)
                    .commit()
            }
        }

        messagesValueEventListener = FirebaseDatabase.getInstance().getReference("messages").child(App.user.id)
            .orderByKey()
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children
                        .map { it.getValue(Message::class.java) }
                        .filterNotNull()
                        .let {
                            messageAdapter.messages.clear()
                            messageAdapter.messages.addAll(it)
                            messageAdapter.notifyDataSetChanged()
                        }

                    pbLoading?.visibility = View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        messagesValueEventListener?.let { FirebaseDatabase.getInstance().getReference("messages").child(App.user.id).removeEventListener(it) }
    }
}
