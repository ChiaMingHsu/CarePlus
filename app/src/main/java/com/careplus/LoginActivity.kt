package com.careplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.careplus.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
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
    }

    override fun onStart() {
        super.onStart()

        firebaseAuth.currentUser?.let { user ->
            Snackbar.make(root, "%s already sign in (by email)".format(user.email), Snackbar.LENGTH_SHORT).show()
            return
        }

        GoogleSignIn.getLastSignedInAccount(this)?.let { account ->
            Snackbar.make(root, "%s already sign in (by Google)".format(account.email), Snackbar.LENGTH_SHORT).show()
            return
        }
    }

    private fun setupView() {
        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()

            if (username.isEmpty() or password.isEmpty()) {
                Toast.makeText(this, "Username or password is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            pbLoading?.visibility = View.VISIBLE

            firebaseAuthWithEmailAndPassword(username, password)
        }

        btnGoogle.setOnClickListener {
            pbLoading?.visibility = View.VISIBLE
            startActivityForResult(googleSignInClient.signInIntent, RC_GOOGLE_SIGN_IN)
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser!!
                        .run {
                            User(uid, displayName)
                        }
                    onLoginSucceed(user)
                } else {
                    onLoginFailed()
                }
            }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser!!
                        .run {
                            User(uid, displayName)
                        }
                    onLoginSucceed(user)
                } else {
                    onLoginFailed()
                }
            }
    }

    private fun onLoginSucceed(user: User) {
        FirebaseDatabase.getInstance().getReference("users").push().setValue(user)
        App.user = user
        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
        pbLoading?.visibility = View.GONE
    }

    private fun onLoginFailed() {
        Toast.makeText(this, R.string.login_failed_message, Toast.LENGTH_SHORT).show()
        pbLoading?.visibility = View.GONE
    }
}