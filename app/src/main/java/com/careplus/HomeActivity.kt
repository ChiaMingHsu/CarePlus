package com.careplus

import android.graphics.Color
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
                FirebaseDatabase.getInstance().getReference("heartbeats").child(App.user.id).child("timestamp").setValue(System.currentTimeMillis())
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
        btnNotification.apply {
            setImageResource(R.drawable.component_237_1)
            setBackgroundColor(Color.rgb(248, 248, 248))
        }
        btnHome.apply {
            setImageResource(R.drawable.component_241)
            setBackgroundColor(Color.rgb(248, 248, 248))
        }
        btnSetting.apply {
            setImageResource(R.drawable.component_236_1)
            setBackgroundColor(Color.rgb(248, 248, 248))
        }

        when (index) {
            0 -> {
                btnNotification.apply {
                    setImageResource(R.drawable.component_237_5)
                    setBackgroundColor(Color.rgb(255, 255, 255))
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layoutFragmentPlaceholder, NotificationFragment())
                    .commit()
            }
            1 -> {
                btnHome.apply {
                    setImageResource(R.drawable.component_22_2)
                    setBackgroundColor(Color.rgb(255, 255, 255))
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layoutFragmentPlaceholder, HomeFragment())
                    .commit()
            }
            2 -> {
                btnSetting.apply {
                    setImageResource(R.drawable.component_236_4)
                    setBackgroundColor(Color.rgb(255, 255, 255))
                }
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
