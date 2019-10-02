package com.careplus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
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
        context?.let { context ->
            videoFilenames[position].let { videoFilename ->
                val uri = File(context.filesDir, videoFilename.replace(".mp4", ".jpg")).toUri()
                holder.view.ivPhoto.setImageURI(uri)
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}