package com.careplus


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_account.*


class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().run {
                signOut()
                addAuthStateListener { firebaseAuth ->
                    if (firebaseAuth.currentUser == null) {
                        startActivity(Intent(context, LoginActivity::class.java))
                        activity?.finish()
                    }
                }
            }

        }
    }
}
