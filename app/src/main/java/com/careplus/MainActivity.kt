package com.careplus

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
    }

    private fun setupView() {
        btn_login.setOnClickListener {
            if (!verify(edt_username.text.toString(), edt_password.text.toString())) {
                Toast.makeText(this, R.string.login_failed_message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
        }
    }

    private fun verify(username: String, password: String): Boolean {
        return true  // TODO: remove it
        return (username == "4caiot@gmail.com") and (password == "12345678")
    }

}
