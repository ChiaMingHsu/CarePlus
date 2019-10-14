package com.careplus


import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.careplus.adapters.EventAdapter
import kotlinx.android.synthetic.main.fragment_alarm.*


class AlarmFragment : Fragment() {

    val eventAdapter = EventAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        eventAdapter.resIds.addAll(arrayOf(
            R.drawable.alarm_btn_falldown_active,
            R.drawable.alarm_btn_toilet_active,
            R.drawable.alarm_btn_outdoor_active,
            R.drawable.alarm_btn_room_active
        ))
        return inflater.inflate(R.layout.fragment_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        rvAlarm.apply {
            layoutManager = GridLayoutManager(context, 2) as RecyclerView.LayoutManager
            adapter = eventAdapter
        }
    }
}
