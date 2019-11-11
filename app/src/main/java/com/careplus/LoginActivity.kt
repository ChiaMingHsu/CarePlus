package com.careplus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.careplus.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val RC_GOOGLE_SIGN_IN = 1
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupView()

        firebaseAuth = FirebaseAuth.getInstance()
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
            .let { googleSignInClient = GoogleSignIn.getClient(this, it) }

        // NOTE: Sign out here to forget previous `Google Login`
        googleSignInClient.signOut()
    }

    private fun setupView() {
        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()

            if (username.isEmpty() or password.isEmpty()) {
                Toast.makeText(this, "帳號或密碼不可為空白", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            layoutProgress?.visibility = View.VISIBLE
            firebaseAuthWithEmailAndPassword(username, password)
        }

        btnGoogle.setOnClickListener {
            layoutProgress?.visibility = View.VISIBLE
            requestGoogleSignIn()
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        btnRemember.setOnClickListener {
            ivRememberCheck.visibility = if (ivRememberCheck.isVisible) View.GONE else View.VISIBLE
        }
    }

    private fun firebaseAuthWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = firebaseAuth.currentUser!!.uid

                FirebaseDatabase.getInstance().getReference("users").orderByKey().equalTo(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            dataSnapshot.children
                                .map { it.getValue(User::class.java)?.apply { id = it.key!! } }
                                .firstOrNull()
                                ?.let { user ->
                                    user.pushToken = getSharedPreferences("user", Context.MODE_PRIVATE)?.getString("pushToken", null)  // Always update `pushToken`
                                    onLoginSucceed(user)
                                }
                                ?: run {
                                    // Successfully sing-in Firebase but no user was found in DB,
                                    // this usually occurred by inconsistency user list between authentication and DB
                                    Toast.makeText(this@LoginActivity, "異常的使用者", Toast.LENGTH_SHORT).show()
                                }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
            }
            .addOnFailureListener { exception ->
                onLoginFailed(exception)
            }
    }

    private fun requestGoogleSignIn() {
        startActivityForResult(googleSignInClient.signInIntent, RC_GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener { account ->
                    firebaseAuthWithGoogle(account)
                }
                .addOnFailureListener { exception ->
                    onLoginFailed(exception)
                }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                val user = firebaseAuth.currentUser!!
                    .run {
                        val pushToken = getSharedPreferences("user", Context.MODE_PRIVATE)?.getString("pushToken", null)
                        User(uid, displayName ?: "", email ?: "", photoUrl?.toString(), pushToken)
                    }
                onLoginSucceed(user)
            }
            .addOnFailureListener {exception ->
                onLoginFailed(exception)
            }
    }

    private fun onLoginSucceed(user: User) {
        FirebaseDatabase.getInstance().run {
            getReference("users").child(user.id).setValue(user)
            getReference("settings").child(user.id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            FirebaseDatabase.getInstance().getReference("settings").child(user.id).run{
                                child("private").setValue(false)
                                child("push").setValue(true)
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        }


        App.user = user
        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
        layoutProgress?.visibility = View.GONE

        val preferences = getSharedPreferences("user", Context.MODE_PRIVATE).edit()
        val isRemember = ivRememberCheck.isVisible
        preferences.putBoolean("remember", isRemember)
        if (isRemember) {
            preferences.putString("id", user.id)
            preferences.putString("name", user.name)
            preferences.putString("email", user.email)
            preferences.putString("avatarUrl", user.avatarUrl)
        }
        preferences.apply()

        finish()
    }

    private fun onLoginFailed(exception: Exception) {
        when (exception) {
            is FirebaseAuthInvalidUserException -> ivErrorTip.visibility = View.VISIBLE
            is FirebaseAuthInvalidCredentialsException -> ivErrorTip.visibility = View.VISIBLE
            else -> Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
        }
        layoutProgress?.visibility = View.GONE
    }
}
