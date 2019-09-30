package com.careplus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_setting.*


class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        btnFunction.setOnClickListener {
            fragmentManager?.run {
                beginTransaction()
                    .replace(R.id.layoutFragmentPlaceholder, FunctionFragment())
                    .addToBackStack(this@SettingFragment::class.java.simpleName)
                    .commit()
            }
        }

        btnCalendar.setOnClickListener {
            fragmentManager?.run {
                beginTransaction()
                    .replace(R.id.layoutFragmentPlaceholder, CalendarFragment())
                    .addToBackStack(this@SettingFragment::class.java.simpleName)
                    .commit()
            }
        }

        btnStorage.setOnClickListener {
            fragmentManager?.run {
                beginTransaction()
                    .replace(R.id.layoutFragmentPlaceholder, StorageFragment())
                    .addToBackStack(this@SettingFragment::class.java.simpleName)
                    .commit()
            }
        }

        btnPush.setOnClickListener {
            fragmentManager?.run {
                beginTransaction()
                    .replace(R.id.layoutFragmentPlaceholder, PushFragment())
                    .addToBackStack(this@SettingFragment::class.java.simpleName)
                    .commit()
            }
        }

        btnAccount.setOnClickListener {
            fragmentManager?.run {
                beginTransaction()
                    .replace(R.id.layoutFragmentPlaceholder, AccountFragment())
                    .addToBackStack(this@SettingFragment::class.java.simpleName)
                    .commit()
            }
        }
    }

}
