package com.careplus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.careplus.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*


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
            val name = edtName.text.toString()
            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()
            val passwordConfirm = edtPasswordConfirm.text.toString()

            if (name.isEmpty() or username.isEmpty() or password.isEmpty()) {
                Toast.makeText(this, "請填寫姓名、Email、密碼", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "密碼不得少於 6 個字元", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != passwordConfirm) {
                Toast.makeText(this, "密碼確認錯誤", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            layoutProgress?.visibility = View.VISIBLE
            firebaseAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val pushToken = getSharedPreferences("user", Context.MODE_PRIVATE)?.getString("pushToken", null)
                        val user = User(firebaseAuth.currentUser!!.uid, edtName.text.toString(), null, pushToken)
                        onRegisterSucceed(user)
                    } else {
                        onRegisterFailed(task.exception?.message)
                    }
                }
        }
    }

    private fun onRegisterSucceed(user: User) {
        FirebaseDatabase.getInstance().getReference("users").child(user.id).setValue(user)
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        layoutProgress?.visibility = View.GONE
    }

    private fun onRegisterFailed(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        layoutProgress?.visibility = View.GONE
    }

}
