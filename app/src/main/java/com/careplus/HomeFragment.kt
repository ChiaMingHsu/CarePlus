package com.careplus

import android.graphics.Matrix
import android.os.Bundle
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

    var isCircleFrameUpdatable: Boolean = true
    var isFullFrameUpdatable: Boolean = false

    var observableBase64Str: String by Delegates.observable("") { _, _, newBase64Str->
        if (newBase64Str.isEmpty())
            return@observable

        Util.decodeBase64ToBitmap(newBase64Str)?.let { bitmap ->
            ivCircleFrame
                ?.takeIf { isCircleFrameUpdatable }
                ?.setImageBitmap(bitmap)

            ivFullFrame
                ?.takeIf { isFullFrameUpdatable }
                ?.apply {
                    // Keep previous matrix and then apply it after updating bitmap
                    val matrix = Matrix()
                    this.attacher?.getSuppMatrix(matrix)  // Note: Do not use .getDisplayMatrix
                    this.setImageBitmap(bitmap)
                    this.attacher?.setDisplayMatrix(matrix)
                }
        }
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
        (activity as HomeActivity).notifyPageEntered("home")
    }

    private fun setupView() {
        tvName.isSelected = true
        tvName.text = App.user.name
        tvEmail.text = App.user.email
        App.user.avatarUrl?.let { Picasso.get().load(it).into(ivAvatar) }

        Calendar.getInstance().run {
            tvYear.text = this.get(Calendar.YEAR).toString()
            tvMonth.text = this.get(Calendar.MONTH).run { this + 1 }.toString()
            tvDay.text = this.get(Calendar.DAY_OF_MONTH).toString()
        }

        ivFullFrame.maximumScale = 10f

        btnEnterFullscreen.setOnClickListener {
            Util.decodeBase64ToBitmap(observableBase64Str)?.let { bitmap ->
                btnEnterFullscreen.visibility = View.GONE
                isCircleFrameUpdatable = false
                ivCircleFrame.setImageBitmap(null)

                ivFullFrame
                    ?.apply {
                        this.visibility = View.VISIBLE
                        this.setImageBitmap(bitmap)
                        this.setZoomTransitionDuration(1000)
                        this.post {
                            val x = bitmap.width / 2f
                            val y = bitmap.height / 2f
                            val focalX = x * this.width / bitmap.width
                            val focalY = y * this.height / bitmap.height
                            this.setScale(this.maximumScale, focalX, focalY, true)
                        }
                    }
                    ?.postDelayed({
                        btnExitFullscreen?.visibility = View.VISIBLE
                        isFullFrameUpdatable = true
                    }, 1000)
            }
        }

        btnExitFullscreen.setOnClickListener {
            Util.decodeBase64ToBitmap(observableBase64Str)?.let { bitmap ->
                btnExitFullscreen.visibility = View.GONE
                isFullFrameUpdatable = false

                ivFullFrame
                    ?.apply {
                        this.setZoomTransitionDuration(1000)
                        this.post {
                            this.setScale(this.minimumScale, true)
                        }
                    }
                    ?.postDelayed({
                        btnEnterFullscreen?.visibility = View.VISIBLE
                        ivCircleFrame?.setImageBitmap(bitmap)
                        ivFullFrame?.apply {
                            setImageBitmap(null)
                            visibility = View.GONE
                        }
                        isCircleFrameUpdatable = true
                    }, 1000)
            }
        }

        btnPrivacy.setOnClickListener {
            val isPrivate = btnPrivacy.tag == "1"
            if (isPrivate) {
                btnPrivacy.setImageResource(R.drawable.home_btn_privacy_protection_light)
                btnPrivacy.tag = "0"
            } else {
                btnPrivacy.setImageResource(R.drawable.home_btn_privacy_protection_dark)
                btnPrivacy.tag = "1"
            }
            FirebaseDatabase.getInstance().getReference("frames").child(App.user.id).child("private").setValue(isPrivate)
        }
    }

    override fun onResume() {
        super.onResume()
        setupDB()
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

    override fun onPause() {
        super.onPause()
        framesValueEventListener?.let { FirebaseDatabase.getInstance().getReference("frames").child(App.user.id).removeEventListener(it) }
    }
}
