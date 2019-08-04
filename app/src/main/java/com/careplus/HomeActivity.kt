package com.careplus

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

        tabIndex = 0
    }

    private fun setupView() {
        btn_home.setOnClickListener { tabIndex = 0 }
        btn_region.setOnClickListener { tabIndex = 1 }
        btn_notification.setOnClickListener { tabIndex = 2 }
        btn_setting.setOnClickListener { tabIndex = 3 }
    }

    private fun navigateToTab(index: Int) {
        btn_home.setImageResource(R.mipmap.tab_home_inactive)
        btn_region.setImageResource(R.mipmap.tab_region_inactive)
        btn_notification.setImageResource(R.mipmap.tab_notification_inactive)
        btn_setting.setImageResource(R.mipmap.tab_setting_inactive)

        when (index) {
            0 -> {
                btn_home.setImageResource(R.mipmap.tab_home_active)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layout_fragment_placeholder, HomeFragment())
                    .commit()
            }
            1 -> {
                btn_region.setImageResource(R.mipmap.tab_region_active)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layout_fragment_placeholder, RegionFragment())
                    .commit()
            }
            2 -> {
                btn_notification.setImageResource(R.mipmap.tab_notification_active)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layout_fragment_placeholder, NotificationFragment())
                    .commit()
            }
            3 -> {
                btn_setting.setImageResource(R.mipmap.tab_setting_active)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layout_fragment_placeholder, SettingFragment())
                    .commit()
            }
        }
    }

}
