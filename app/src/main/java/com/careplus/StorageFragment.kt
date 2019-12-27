package com.careplus


import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.careplus.adapters.PhotoAdapter
import kotlinx.android.synthetic.main.fragment_storage.*
import java.io.File


class StorageFragment : Fragment() {

    val photoAdapter = PhotoAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        photoAdapter.videoFilenames.addAll(listMediaFilenames())
        return inflater.inflate(R.layout.fragment_storage, container, false)
    }

    private fun listMediaFilenames(): Array<String> {
        return context?.let { context ->
            File(context.filesDir.path).list()
                ?.filterNotNull()
                ?.filter { it.endsWith(".mp4") }
                ?.toTypedArray()
        } ?: emptyArray()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        rvAlbum.apply {
            layoutManager = GridLayoutManager(context, 3) as RecyclerView.LayoutManager
            adapter = photoAdapter
        }
    }

}
