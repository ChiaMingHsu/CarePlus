package com.careplus

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.andrognito.flashbar.Flashbar
import com.andrognito.flashbar.anim.FlashAnim
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

    var isNetworkEverUnavailable = false

    var networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            super.onLost(network)
            showNetworkUnreachable()
            isNetworkEverUnavailable = true
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            if (isNetworkEverUnavailable) {
                showNetworkRecovered()
                isNetworkEverUnavailable = false
            }
        }
    }

    var tabIndex: Int by Delegates.observable(-1) { _, oldTabIndex, newTabIndex ->
        if (newTabIndex != oldTabIndex)
            navigateToTab(newTabIndex)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupView()
        setupDB()
        tabIndex = 1
    }

    private fun setupView() {
        btnNotification.setOnClickListener { tabIndex = 0 }
        btnHome.setOnClickListener { tabIndex = 1 }
        btnSetting.setOnClickListener { tabIndex = 2 }
        ivTutorial.setOnClickListener {
            ivTutorial.visibility = View.GONE
        }
    }

    private fun setupDB() {
        FirebaseDatabase.getInstance().apply {
            getReference("messages").child(App.user.id).keepSynced(true)
            getReference("activities").child(App.user.id).keepSynced(true)
            getReference("events").child(App.user.id).keepSynced(true)
        }
    }

    private fun navigateToTab(index: Int) {
        // Clear stack before switching to other fragment
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        when (index) {
            0 -> {
                ivActiveTab.setImageResource(R.drawable.home_tab_notification_active)
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.layoutFragmentPlaceholder, NotificationFragment())
                    .commit()
            }
            1 -> {
                ivActiveTab.setImageResource(R.drawable.home_tab_home_active)
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.layoutFragmentPlaceholder, HomeFragment())
                    .commit()
            }
            2 -> {
                ivActiveTab.setImageResource(R.drawable.home_tab_setting_active)
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.layoutFragmentPlaceholder, SettingFragment())
                    .commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        heartbeatThread = HeartbeatThread().apply { start() }

        (applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .apply {
                val networkRequest = NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
                registerNetworkCallback(networkRequest, networkCallback)
            }
    }

    override fun onPause() {
        super.onPause()
        heartbeatThread?.apply { shouldContinue.set(false) }
        (applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .apply { unregisterNetworkCallback(networkCallback) }
    }

    fun notifyPageEntered(pageName: String) {
        val preferences = getSharedPreferences("tutorial", Context.MODE_PRIVATE)
        val tutorialShown = preferences.getBoolean(pageName, false)
        var resId = -1
        when (pageName) {
            "home" -> resId = R.drawable.home_tutorial
            "notification" -> resId = R.drawable.notification_tutorial
            "setting" -> resId = R.drawable.setting_tutorial
            "alarm" -> resId = R.drawable.alarm_tutorial
            "remind" -> resId = R.drawable.remind_tutorial
        }
        if (!tutorialShown and (resId != -1)) {
            ivTutorial.setImageResource(resId)
            ivTutorial.visibility = View.VISIBLE
            preferences.edit().putBoolean(pageName, true).apply()
        }
    }

    private fun showNetworkUnreachable() {
        runOnUiThread {
            Flashbar.Builder(this@HomeActivity)
                .gravity(Flashbar.Gravity.TOP)
                .message("Network is unreachable")
                .backgroundColor(Color.parseColor("#d9534f"))
                .enterAnimation(FlashAnim.with(this@HomeActivity)
                    .animateBar()
                    .duration(750)
                    .alpha()
                    .overshoot())
                .exitAnimation(
                    FlashAnim.with(this@HomeActivity)
                        .animateBar()
                        .duration(400)
                        .accelerateDecelerate())
                .primaryActionText("GOT IT")
                .primaryActionTapListener(object : Flashbar.OnActionTapListener {
                    override fun onActionTapped(bar: Flashbar) {
                        bar.dismiss()
                    }
                })
                .build()
                .show()
        }
    }

    private fun showNetworkRecovered() {
        runOnUiThread {
            Flashbar.Builder(this@HomeActivity)
                .gravity(Flashbar.Gravity.TOP)
                .message("Network is recovered")
                .backgroundColor(Color.parseColor("#5cb85c"))
                .enterAnimation(FlashAnim.with(this@HomeActivity)
                    .animateBar()
                    .duration(750)
                    .alpha()
                    .overshoot())
                .exitAnimation(
                    FlashAnim.with(this@HomeActivity)
                        .animateBar()
                        .duration(400)
                        .accelerateDecelerate())
                .duration(Flashbar.DURATION_LONG)
                .build()
                .show()
        }
    }
}
