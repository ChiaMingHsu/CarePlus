package com.careplus


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.careplus.adapters.EventAdapter
import com.careplus.model.Event
import kotlinx.android.synthetic.main.fragment_alarm.*


class AlarmFragment : Fragment() {

    val eventAdapter = EventAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        eventAdapter.events.addAll(arrayOf(
            Event("0", "跌倒", "alarm", "falldown", "elapsed", "00:05"),
            Event("1", "廁所", "alarm", "toilet", "elapsed", "00:05"),
            Event("2", "出門", "alarm", "outdoor", "elapsed", "00:05"),
            Event("3", "房間", "alarm", "room", "elapsed", "08:00")
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
