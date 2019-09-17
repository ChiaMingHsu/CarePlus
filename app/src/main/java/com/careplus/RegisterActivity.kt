package com.careplus

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.careplus.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.edtPassword
import kotlinx.android.synthetic.main.activity_register.edtUsername


class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupView()

        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun setupView() {
        btnSubmit.setOnClickListener {
            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()
            val passwordConfirm = edtPasswordConfirm.text.toString()

            if (username.isEmpty() or password.isEmpty()) {
                Toast.makeText(this, "Username or password is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must have at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != passwordConfirm) {
                Toast.makeText(this, "Specified passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            pbLoading?.visibility = View.VISIBLE

            firebaseAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser!!
                            .run {
                                User(uid, displayName, photoUrl.toString())
                            }
                        onRegisterSucceed(user)
                    } else {
                        onRegisterFailed()
                    }
                }
        }
    }

    private fun onRegisterSucceed(user: User) {
        FirebaseDatabase.getInstance().getReference("users").push().setValue(user)
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        pbLoading?.visibility = View.GONE
    }

    private fun onRegisterFailed() {
        Toast.makeText(this, R.string.login_failed_message, Toast.LENGTH_SHORT).show()
        pbLoading?.visibility = View.GONE
    }

}
