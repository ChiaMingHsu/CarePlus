package com.careplus.adapters

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.careplus.BuildConfig
import com.careplus.R
import kotlinx.android.synthetic.main.item_photo.view.*
import java.io.File

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    var context: Context? = null
    val videoFilenames = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        context = null
    }

    override fun getItemCount(): Int = videoFilenames.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        videoFilenames[position].let { videoFilename ->
            context?.let { context ->
                val videoFile = File(context.filesDir, videoFilename)
                val thumbnailFile = File(context.filesDir, videoFilename.replace(".mp4", ".jpg"))
                holder.view.btnPhoto.apply {
                    setImageURI(Uri.fromFile(thumbnailFile))
                    setOnClickListener {
                        MediaPlayer()
                            .apply {
                                val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", videoFile)
                                context.grantUriPermission(context.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                val intent = Intent(Intent.ACTION_VIEW)
                                    .setDataAndType(uri, "video/mp4")
                                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                context.startActivity(intent)
                            }
                    }
                }
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}