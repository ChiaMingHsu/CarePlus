package com.careplus

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_setting.*

/**
 * A placeholder fragment containing a simple view.
 */
class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        btn_alarm.setOnClickListener {
            layout_alarm.visibility = View.VISIBLE
            layout_remind.visibility = View.GONE
        }

        btn_remind.setOnClickListener {
            layout_alarm.visibility = View.GONE
            layout_remind.visibility = View.VISIBLE
        }
    }
}
