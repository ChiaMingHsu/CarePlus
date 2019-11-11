package com.careplus

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.careplus.model.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_playback.*
import org.jcodec.api.android.AndroidSequenceEncoder
import org.jcodec.common.io.NIOUtils
import org.jcodec.common.model.Rational
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.properties.Delegates


class PlaybackFragment(private val message: Message) : Fragment() {

    companion object {
        const val FPS = 1
        const val tempVideoFilename = "tmp.mp4"
        const val tempThumbnailFilename = "tmp.jpg"
    }

    var frames: Array<Bitmap> by Delegates.observable(initialValue = emptyArray()) { _, _, newFrames ->
        context?.let { context ->
            thread(start = true) {
                newFrames
                    .takeIf { it.count() > 0 }
                    ?.also { frames ->
                        val thumbnailFile = File(context.cacheDir, tempThumbnailFilename)
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        val fileOutputStream = FileOutputStream(thumbnailFile)

                        frames[0].compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                        fileOutputStream.write(byteArrayOutputStream.toByteArray())

                        fileOutputStream.flush()
                        fileOutputStream.close()
                    }
                    ?.also { frames ->
                        val videoFile = File(context.cacheDir, tempVideoFilename)
                        val byteChannel = NIOUtils.writableFileChannel(videoFile.path)
                        val encoder = AndroidSequenceEncoder(byteChannel, Rational.R1(FPS))

                        frames
                            .forEach { bitmap ->
                                encoder.encodeImage(bitmap)
                            }
                            .run {
                                encoder.finish()
                            }
                            .run {
                                vvPlayback.post {
                                    val uri = File(context.cacheDir, tempVideoFilename).toUri()
                                    vvPlayback.apply {
                                        setVideoURI(uri)
                                        start()
                                    }
                                }
                            }
                    }
                    ?.run {
                        btnSave.post { btnSave.visibility = View.VISIBLE }
                    }
                    ?:run {
                        btnSave.post { btnSave.visibility = View.GONE }
                    }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        tvDate.text = SimpleDateFormat("yyyy.MM.dd").format(Date(message.createdAt))
        tvTime.text = SimpleDateFormat("HH:mm").format(Date(message.createdAt))
        tvContent.text = message.content

        when (Pair(message.type, message.priority)) {
            Pair("alarm", "standard") -> {
                tvDate.setBackgroundResource(R.drawable.playback_date_bg_alarm_standard)
                tvTime.setBackgroundResource(R.drawable.playback_time_bg_alarm_standard)
                tvContent.setBackgroundResource(R.drawable.playback_content_bg_alarm_standard)
            }
            Pair("alarm", "emergent") -> {
                tvDate.setBackgroundResource(R.drawable.playback_date_bg_alarm_emergent)
                tvTime.setBackgroundResource(R.drawable.playback_time_bg_alarm_emergent)
                tvContent.setBackgroundResource(R.drawable.playback_content_bg_alarm_emergent)
            }
            Pair("remind", "standard") -> {
                tvDate.setBackgroundResource(R.drawable.playback_date_bg_remind_standard)
                tvTime.setBackgroundResource(R.drawable.playback_time_bg_remind_standard)
                tvContent.setBackgroundResource(R.drawable.playback_content_bg_remind_standard)
            }
        }

        vvPlayback.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true

            val mediaController = MediaController(context, false).apply { setAnchorView(layoutPlayback) }
            vvPlayback.setMediaController(mediaController)
        }

        btnSave.setOnClickListener {
            context
                ?.also { context ->
                    val srcFile = File(context.cacheDir, tempThumbnailFilename)
                    val dstFile = File(context.filesDir, "%s.jpg".format(message.id))
                    srcFile.copyTo(dstFile, true)
                }
                ?.also { context ->
                    val srcFile = File(context.cacheDir, tempVideoFilename)
                    val dstFile = File(context.filesDir, "%s.mp4".format(message.id))
                    srcFile.copyTo(dstFile, true)
                }
                ?.also {
                    Toast.makeText(context, "影片已儲存", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        setupDB()
    }

    private fun setupDB() {
        FirebaseDatabase.getInstance().getReference("playbacks").child(message.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    frames = dataSnapshot.children
                        .mapNotNull { it.getValue(String::class.java) }
                        .mapNotNull { Util.decodeBase64ToBitmap(it) }
                        .toTypedArray()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    override fun onPause() {
        super.onPause()
        vvPlayback.stopPlayback()
    }
}
