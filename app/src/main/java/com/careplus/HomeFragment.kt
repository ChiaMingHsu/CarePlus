package com.careplus

import android.graphics.Bitmap
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import kotlin.properties.Delegates


class HomeFragment : Fragment() {

    var framesValueEventListener: ValueEventListener? = null

    var observableBase64Str: String by Delegates.observable("") {_, _, newBase64Str->
        if (newBase64Str.isEmpty())
            return@observable

        val bitmap = decodeBase64ToBitmap(newBase64Str)

        ivFullFrame?.apply {
            if (this.visibility != View.GONE) {
                val matrix = Matrix()
                this.attacher?.getSuppMatrix(matrix)  // Note: Do not use .getDisplayMatrix
                this.setImageBitmap(bitmap)
                this.attacher?.setDisplayMatrix(matrix)
            }
        }
        ivCircleFrame?.setImageBitmap(bitmap)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupDB()
    }

    private fun setupView() {
        tvName.isSelected = true
        tvName.text = App.user.name
        Picasso.get().load(App.user.avatarUrl).into(ivAvatar)

        Calendar.getInstance().run {
            tvYear.text = this.get(Calendar.YEAR).toString()
            tvMonth.text = this.get(Calendar.MONTH).run { this + 1 }.toString()
            tvDay.text = this.get(Calendar.DAY_OF_MONTH).toString()
        }

        ivFullFrame.apply {
            maximumScale = 10f
            setOnSingleFlingListener { _, _, _, _ ->
                ivFullFrame.visibility = View.GONE
                return@setOnSingleFlingListener true
            }
        }

        btnFullscreen.setOnClickListener {
            val bitmap = decodeBase64ToBitmap(observableBase64Str)

            ivFullFrame?.apply {
                this.visibility = View.VISIBLE
                this.setImageBitmap(bitmap)
                this.post {
                    val x = bitmap.width / 2f
                    val y = bitmap.height / 2f
                    val focalX = x * this.width / bitmap.width
                    val focalY = y * this.height / bitmap.height
                    this.setScale(this.maximumScale, focalX, focalY, true)
                }
            }
            ivCircleFrame?.setImageBitmap(bitmap)
        }
    }

    private fun setupDB() {
        framesValueEventListener = FirebaseDatabase.getInstance().getReference("frames").child(App.user.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.child("frame").getValue(String::class.java)?.let { base64Str ->
                        observableBase64Str = base64Str
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap {
        return Base64.decode(base64Str, Base64.DEFAULT)
            .run { BitmapFactory.decodeByteArray(this, 0, this.size) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        framesValueEventListener?.let { FirebaseDatabase.getInstance().getReference("frames").child(App.user.id).removeEventListener(it) }
    }
}
