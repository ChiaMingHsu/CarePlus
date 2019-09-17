package com.careplus

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_home.*
import kotlin.properties.Delegates

class HomeActivity : AppCompatActivity() {

    var tabIndex: Int by Delegates.observable(-1) { _, oldValue, newValue ->
        if (newValue != oldValue)
            navigateToTab(newValue)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupView()
        tabIndex = 1
    }

    private fun setupView() {
        btn_notification.setOnClickListener { tabIndex = 0 }
        btn_home.setOnClickListener { tabIndex = 1 }
        btn_setting.setOnClickListener { tabIndex = 2 }
    }

    private fun navigateToTab(index: Int) {
        btn_notification.apply {
            setImageResource(R.drawable.component_237_1)
            setBackgroundColor(Color.rgb(248, 248, 248))
        }
        btn_home.apply {
            setImageResource(R.drawable.component_241)
            setBackgroundColor(Color.rgb(248, 248, 248))
        }
        btn_setting.apply {
            setImageResource(R.drawable.component_236_1)
            setBackgroundColor(Color.rgb(248, 248, 248))
        }

        when (index) {
            0 -> {
                btn_notification.apply {
                    setImageResource(R.drawable.component_237_5)
                    setBackgroundColor(Color.rgb(255, 255, 255))
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layout_fragment_placeholder, NotificationFragment())
                    .commit()
            }
            1 -> {
                btn_home.apply {
                    setImageResource(R.drawable.component_22_2)
                    setBackgroundColor(Color.rgb(255, 255, 255))
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layout_fragment_placeholder, HomeFragment())
                    .commit()
            }
            2 -> {
                btn_setting.apply {
                    setImageResource(R.drawable.component_236_4)
                    setBackgroundColor(Color.rgb(255, 255, 255))
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layout_fragment_placeholder, SettingFragment())
                    .commit()
            }
        }
    }

}
