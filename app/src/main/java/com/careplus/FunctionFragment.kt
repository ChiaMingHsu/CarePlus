package com.careplus

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.careplus.adapters.FunctionFragmentPagerAdapter
import kotlinx.android.synthetic.main.fragment_function.*


class FunctionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_function, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        (activity as HomeActivity).notifyPageEntered("alarm")
    }

    private fun setupView() {
        FunctionFragmentPagerAdapter(childFragmentManager)
            .apply {
                fragments.addAll(arrayOf(
                    AlarmFragment(),
                    RemindFragment()
                ))
            }
            .let { viewPager.adapter = it }
            .run { indicator.setViewPager(viewPager) }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // change alpha only whiling actually scrolling
                if (positionOffset > 0.01 && positionOffset < 0.99) {
                    ivIndicatorAlarmBg.alpha = 1 - positionOffset
                    ivIndicatorRemindBg.alpha = positionOffset
                }
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        tvIndicatorAlarm.setTextColor(Color.parseColor("#4b5cff"))
                        tvIndicatorRemind.setTextColor(Color.parseColor("#adadbe"))
                        indicator.setDotIndicatorColor(Color.parseColor("#6170ff"))
                    }
                    1 -> {
                        tvIndicatorAlarm.setTextColor(Color.parseColor("#adadbe"))
                        tvIndicatorRemind.setTextColor(Color.parseColor("#4dc9c8"))
                        indicator.setDotIndicatorColor(Color.parseColor("#4dc9c8"))
                        (activity as HomeActivity).notifyPageEntered("remind")
                    }
                }
            }
        })
    }

}
