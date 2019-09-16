package com.careplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.careplus.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupView()
    }

    private fun setupView() {
        btnLogin.setOnClickListener {
            pbLoading?.visibility = View.VISIBLE
            verify(edtUsername.text.toString(), edtPassword.text.toString(),
                onSuccess = { user ->
                    App.user = user
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    pbLoading?.visibility = View.GONE
                },
                onFailure = {
                    Toast.makeText(this, R.string.login_failed_message, Toast.LENGTH_SHORT).show()
                    pbLoading?.visibility = View.GONE
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
