package com.careplus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_home.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates

class HomeActivity : AppCompatActivity() {

    class HeartbeatThread : Thread() {
        var shouldContinue = AtomicBoolean(true)
        override fun run() {
            while (shouldContinue.get()) {
                // TODO
//                FirebaseDatabase.getInstance().getReference("heartbeats").child(App.user.id).child("timestamp").setValue(System.currentTimeMillis())
                sleep(5000)
            }
        }
    }

    var heartbeatThread: HeartbeatThread? = null

    var tabIndex: Int by Delegates.observable(-1) { _, oldTabIndex, newTabIndex ->
        if (newTabIndex != oldTabIndex)
            navigateToTab(newTabIndex)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupView()
        tabIndex = 1
    }

    private fun setupView() {
        btnNotification.setOnClickListener { tabIndex = 0 }
        btnHome.setOnClickListener { tabIndex = 1 }
        btnSetting.setOnClickListener { tabIndex = 2 }
    }

    private fun navigateToTab(index: Int) {
        ivTabIconNotification.setImageResource(R.drawable.home_tab_icon_notification_inactive)
        ivTabIconHome.setImageResource(R.drawable.home_tab_icon_home_inactive)
        ivTabIconSetting.setImageResource(R.drawable.home_tab_icon_setting_inactive)

        when (index) {
            0 -> {
                ivTabIconNotification.setImageResource(R.drawable.home_tab_icon_notification_active)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layoutFragmentPlaceholder, NotificationFragment())
                    .commit()
            }
            1 -> {
                ivTabIconHome.setImageResource(R.drawable.home_tab_icon_home_active)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layoutFragmentPlaceholder, HomeFragment())
                    .commit()
            }
            2 -> {
                ivTabIconSetting.setImageResource(R.drawable.home_tab_icon_setting_active)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layoutFragmentPlaceholder, SettingFragment())
                    .commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        heartbeatThread = HeartbeatThread().apply { start() }
    }

    override fun onPause() {
        super.onPause()
        heartbeatThread?.apply { shouldContinue.set(false) }
    }
}
