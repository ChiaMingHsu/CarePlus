package com.careplus

import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    var framesValueEventListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDB()
    }

    private fun setupDB() {
        iv_frame.maximumScale = 10f

        framesValueEventListener = FirebaseDatabase.getInstance().getReference("frames").child(App.user.id!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.child("frame").getValue(String::class.java)?.let { base64Str ->
                        Matrix().let { matrix ->
                            iv_frame?.attacher?.getSuppMatrix(matrix)  // Note: Do not use .getDisplayMatrix
                            Base64.decode(base64Str, Base64.DEFAULT)
                                .run { BitmapFactory.decodeByteArray(this, 0, this.size) }
                                .run { iv_frame?.setImageBitmap(this) }
                            iv_frame?.attacher?.setDisplayMatrix(matrix)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        framesValueEventListener?.let { FirebaseDatabase.getInstance().getReference("frames").child(App.user.id!!).removeEventListener(it) }
    }
}
