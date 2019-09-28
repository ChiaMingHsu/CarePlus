package com.careplus

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.careplus.model.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_playback.*
import java.util.concurrent.atomic.AtomicBoolean


class PlaybackFragment(val message: Message, val playbackId: String) : Fragment() {

    class PlayFramesThread(val frames: List<Bitmap>, val imageView: ImageView?) : Thread() {
        var shouldContinue = AtomicBoolean(true)
        override fun run() {
            while (shouldContinue.get()) {
                frames.forEach { frame ->
                    imageView?.post {
                        imageView.setImageBitmap(frame)
                    }
                    sleep(1000)
                }
            }
        }
    }

    var frames: List<Bitmap>? = null
    var playFramesThread: PlayFramesThread? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupDB()
    }

    private fun setupView() {
    }

    private fun setupDB() {
        FirebaseDatabase.getInstance().getReference("playbacks").child(playbackId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    frames = dataSnapshot.children
                        .mapNotNull { it.getValue(String::class.java) }
                        .mapNotNull { Util.decodeBase64ToBitmap(it) }
                    playFrames()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun playFrames() {
        frames?.let { frames ->
            playFramesThread = PlayFramesThread(frames, ivFrame)
            playFramesThread?.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playFramesThread?.apply { shouldContinue.set(false) }
        frames = null
        playFramesThread = null
    }
}
