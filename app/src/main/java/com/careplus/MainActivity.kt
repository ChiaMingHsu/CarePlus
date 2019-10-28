package com.careplus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.careplus.model.User
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferences = getSharedPreferences("user", Context.MODE_PRIVATE)

        if ((FirebaseAuth.getInstance().currentUser == null) or
                !preferences.getBoolean("remember", false))
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        else {
            App.user = User(
                preferences.getString("id", "")!!,
                preferences.getString("name", "")!!,
                preferences.getString("email", "")!!,
                preferences.getString("avatarUrl", null),
                preferences.getString("pushToken", null)
            )
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
        }
        finish()
    }

}
