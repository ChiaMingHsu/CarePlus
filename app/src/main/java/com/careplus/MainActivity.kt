package com.careplus

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.careplus.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
    }

    private fun setupView() {
        btn_login.setOnClickListener {
            pb_loading?.visibility = View.VISIBLE
            verify(edt_username.text.toString(), edt_password.text.toString(),
                onSuccess = { user ->
                    App.user = user
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    pb_loading?.visibility = View.GONE
                },
                onFailure = {
                    Toast.makeText(this, R.string.login_failed_message, Toast.LENGTH_SHORT).show()
                    pb_loading?.visibility = View.GONE
                }
            )
        }
    }

    private fun verify(username: String, password: String, onSuccess: (user: User) -> Unit, onFailure: () -> Unit) {
        FirebaseDatabase.getInstance().getReference("users").orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children
                        .map { it.getValue(User::class.java) }
                        .find { it?.password == password }
                        ?.run(onSuccess)
                        ?: onFailure()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

}
